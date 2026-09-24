/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.auth.LoginHandler;
import top.continew.admin.auth.LoginHandlerFactory;
import top.continew.admin.auth.enums.AuthTypeEnum;
import top.continew.admin.auth.model.req.LoginReq;
import top.continew.admin.auth.model.resp.LoginResp;
import top.continew.admin.auth.model.resp.RouteResp;
import top.continew.admin.auth.model.resp.RouteResultResp;
import top.continew.admin.auth.service.AuthService;
import top.continew.admin.common.context.RoleContext;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.constant.SystemConstants;
import top.continew.admin.system.enums.MenuTypeEnum;
import top.continew.admin.system.model.resp.ClientResp;
import top.continew.admin.system.model.resp.MenuResp;
import top.continew.admin.system.model.resp.ModuleResp;
import top.continew.admin.system.service.ClientService;
import top.continew.admin.system.service.MenuService;
import top.continew.admin.system.service.ModuleService;
import top.continew.admin.system.service.RoleService;
import top.continew.starter.core.util.validation.ValidationUtils;
import top.continew.starter.extension.crud.annotation.TreeField;
import top.continew.starter.extension.crud.autoconfigure.CrudProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 认证业务实现
 *
 * @author Charles7c
 * @since 2022/12/21 21:49
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginHandlerFactory loginHandlerFactory;
    private final ClientService clientService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final ModuleService moduleService;
    private final CrudProperties crudProperties;

    @Override
    public LoginResp login(LoginReq req, HttpServletRequest request) {
        AuthTypeEnum authType = req.getAuthType();
        // 校验客户端
        ClientResp client = clientService.getByClientId(req.getClientId());
        ValidationUtils.throwIfNull(client, "客户端不存在");
        ValidationUtils.throwIf(DisEnableStatusEnum.DISABLE.equals(client.getStatus()), "客户端已禁用");
        ValidationUtils.throwIf(!client.getAuthType().contains(authType.getValue()), "该客户端暂未授权 [{}] 认证", authType
            .getDescription());
        // 获取处理器
        LoginHandler<LoginReq> loginHandler = loginHandlerFactory.getHandler(authType);
        // 登录前置处理
        loginHandler.preLogin(req, client, request);
        // 登录
        LoginResp loginResp = loginHandler.login(req, client, request);
        // 登录后置处理
        loginHandler.postLogin(req, client, request);
        return loginResp;
    }

    @Override
    public RouteResultResp buildRoute(Long userId) {
        // 按当前登录客户端的端过滤可见模块（取不到端时按 PC 处理）
        UserContext userContext = UserContextHolder.getContext(userId);
        String clientType = ObjectUtil.defaultIfNull(userContext == null ? null : userContext.getClientType(),
            SystemConstants.PLATFORM_PC);
        List<ModuleResp> modules = moduleService.listEnabledByClientType(clientType);
        Set<Long> moduleIdSet = new HashSet<>();
        Map<Long, String> moduleCodeMap = new HashMap<>();
        modules.forEach(module -> {
            moduleIdSet.add(module.getId());
            moduleCodeMap.put(module.getId(), module.getCode());
        });
        RouteResultResp routeResult = new RouteResultResp();
        routeResult.setModules(modules);
        routeResult.setRoutes(new ArrayList<>(0));
        Set<RoleContext> roleSet = roleService.listByUserId(userId);
        if (CollUtil.isEmpty(roleSet)) {
            return routeResult;
        }
        // 查询菜单列表
        Set<MenuResp> menuSet = new LinkedHashSet<>();
        if (roleSet.stream().anyMatch(r -> SystemConstants.SUPER_ADMIN_ROLE_ID.equals(r.getId()))) {
            menuSet.addAll(menuService.listByRoleId(SystemConstants.SUPER_ADMIN_ROLE_ID));
        } else {
            roleSet.forEach(r -> menuSet.addAll(menuService.listByRoleId(r.getId())));
        }
        List<MenuResp> menuList = menuSet.stream()
            .filter(m -> !MenuTypeEnum.BUTTON.equals(m.getType()))
            // 仅保留当前端可见模块的菜单；未分组（0/null）菜单始终可见，兼容存量数据
            .filter(m -> ObjectUtil.isNull(m.getModuleId()) || m.getModuleId() == 0L || moduleIdSet.contains(m
                .getModuleId()))
            .toList();
        if (CollUtil.isEmpty(menuList)) {
            return routeResult;
        }
        // 构建路由树
        TreeField treeField = MenuResp.class.getDeclaredAnnotation(TreeField.class);
        TreeNodeConfig treeNodeConfig = crudProperties.getTreeDictModel().genTreeNodeConfig(treeField);
        List<Tree<Long>> treeList = TreeUtil.build(menuList, treeField.rootId(), treeNodeConfig, (m, tree) -> {
            tree.setId(m.getId());
            tree.setParentId(m.getParentId());
            tree.setName(m.getTitle());
            tree.setWeight(m.getSort());
            tree.putExtra("type", m.getType().getValue());
            tree.putExtra("path", m.getPath());
            tree.putExtra("name", m.getName());
            tree.putExtra("component", m.getComponent());
            tree.putExtra("redirect", m.getRedirect());
            tree.putExtra("icon", m.getIcon());
            tree.putExtra("isExternal", m.getIsExternal());
            tree.putExtra("isCache", m.getIsCache());
            tree.putExtra("isHidden", m.getIsHidden());
            tree.putExtra("permission", m.getPermission());
            tree.putExtra("moduleId", m.getModuleId());
        });
        List<RouteResp> routes = BeanUtil.copyToList(treeList, RouteResp.class);
        routes.forEach(route -> this.fillModuleCode(route, moduleCodeMap));
        routeResult.setRoutes(routes);
        return routeResult;
    }

    /**
     * 回填路由所属模块编码
     *
     * @param route         路由
     * @param moduleCodeMap 模块 ID 与编码映射
     */
    private void fillModuleCode(RouteResp route, Map<Long, String> moduleCodeMap) {
        if (route.getModuleId() != null) {
            route.setModuleCode(moduleCodeMap.get(route.getModuleId()));
        }
        if (CollUtil.isNotEmpty(route.getChildren())) {
            route.getChildren().forEach(child -> this.fillModuleCode(child, moduleCodeMap));
        }
    }
}
