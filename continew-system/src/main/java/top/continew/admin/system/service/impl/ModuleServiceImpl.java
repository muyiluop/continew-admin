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

package top.continew.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.common.base.service.BaseServiceImpl;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.constant.SystemConstants;
import top.continew.admin.system.mapper.ModuleMapper;
import top.continew.admin.system.model.entity.MenuDO;
import top.continew.admin.system.model.entity.ModuleDO;
import top.continew.admin.system.model.query.ModuleQuery;
import top.continew.admin.system.model.req.ModuleReq;
import top.continew.admin.system.model.resp.ModuleResp;
import top.continew.admin.system.service.MenuService;
import top.continew.admin.system.service.ModuleService;
import top.continew.starter.core.util.validation.CheckUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 业务模块业务实现
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl extends BaseServiceImpl<ModuleMapper, ModuleDO, ModuleResp, ModuleResp, ModuleQuery, ModuleReq> implements ModuleService {

    private final MenuService menuService;

    @Override
    public Long create(ModuleReq req) {
        this.checkNameRepeat(req.getName(), null);
        this.checkCodeRepeat(req.getCode(), null);
        return super.create(req);
    }

    @Override
    public void update(ModuleReq req, Long id) {
        this.checkNameRepeat(req.getName(), id);
        this.checkCodeRepeat(req.getCode(), id);
        super.update(req, id);
    }

    @Override
    public void delete(List<Long> ids) {
        // 模块下存在菜单时，不允许删除，避免菜单失去归属
        CheckUtils.throwIf(menuService.lambdaQuery()
            .in(MenuDO::getModuleId, ids)
            .exists(), "模块下存在菜单，请先迁移或删除菜单");
        super.delete(ids);
    }

    @Override
    public List<ModuleResp> listEnabledByClientType(String clientType) {
        String platform = StrUtil.blankToDefault(clientType, SystemConstants.PLATFORM_PC);
        return this.list(new ModuleQuery(DisEnableStatusEnum.ENABLE), null)
            .stream()
            .filter(module -> CollUtil.isEmpty(module.getPlatforms()) || module.getPlatforms().contains(platform))
            .sorted(Comparator.comparing(ModuleResp::getSort, Comparator.nullsLast(Integer::compareTo)))
            .map(module -> BeanUtil.copyProperties(module, ModuleResp.class))
            .toList();
    }

    /**
     * 检查模块名称是否重复
     *
     * @param name 模块名称
     * @param id   ID
     */
    private void checkNameRepeat(String name, Long id) {
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(ModuleDO::getName, name)
            .ne(id != null, ModuleDO::getId, id)
            .exists(), "名称为 [{}] 的模块已存在", name);
    }

    /**
     * 检查模块编码是否重复
     *
     * @param code 模块编码
     * @param id   ID
     */
    private void checkCodeRepeat(String code, Long id) {
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(ModuleDO::getCode, code)
            .ne(id != null, ModuleDO::getId, id)
            .exists(), "编码为 [{}] 的模块已存在", code);
    }
}
