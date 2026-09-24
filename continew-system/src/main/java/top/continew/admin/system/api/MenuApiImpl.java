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

package top.continew.admin.system.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.common.api.system.MenuApi;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.model.query.MenuQuery;
import top.continew.admin.system.model.resp.MenuResp;
import top.continew.admin.system.service.MenuService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单业务 API 实现
 *
 * @author Charles7c
 * @since 2025/7/26 9:53
 */
@Service
@RequiredArgsConstructor
public class MenuApiImpl implements MenuApi {

    private final MenuService baseService;

    @Override
    public List<Tree<Long>> listTree(List<Long> excludeMenuIds, boolean isSimple) {
        MenuQuery query = new MenuQuery();
        query.setStatus(DisEnableStatusEnum.ENABLE);
        // 过滤掉租户不能使用的菜单
        query.setExcludeMenuIdList(excludeMenuIds);
        List<Tree<Long>> treeList = baseService.tree(query, null, isSimple);
        // 精简树不含模块/类型/权限等字段，这里统一补充，便于前端按模块分组并按「菜单 + 权限」展示
        Map<Long, MenuResp> menuMap = new HashMap<>();
        baseService.list(query, null).forEach(menu -> menuMap.put(menu.getId(), menu));
        treeList.forEach(tree -> this.fillExtras(tree, menuMap));
        return treeList;
    }

    /**
     * 递归补充模块 ID、类型、权限标识
     *
     * @param tree    树节点
     * @param menuMap 菜单 ID 与菜单信息映射
     */
    private void fillExtras(Tree<Long> tree, Map<Long, MenuResp> menuMap) {
        MenuResp menu = menuMap.get(tree.getId());
        if (menu != null) {
            tree.putExtra("moduleId", menu.getModuleId());
            tree.putExtra("type", menu.getType() == null ? null : menu.getType().getValue());
            tree.putExtra("permission", menu.getPermission());
        }
        if (CollUtil.isNotEmpty(tree.getChildren())) {
            tree.getChildren().forEach(child -> this.fillExtras(child, menuMap));
        }
    }
}
