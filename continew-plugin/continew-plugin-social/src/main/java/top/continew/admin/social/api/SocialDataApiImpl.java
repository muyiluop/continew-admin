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

package top.continew.admin.social.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.api.tenant.TenantDataApi;
import top.continew.admin.common.config.TenantExtensionProperties;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.model.dto.TenantDTO;
import top.continew.admin.social.mapper.SocialConfigMapper;
import top.continew.admin.social.model.entity.SocialConfigDO;
import top.continew.admin.social.service.SocialConfigService;
import top.continew.starter.extension.tenant.util.TenantUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 社交登录租户数据 API 实现
 *
 * <p>新建租户时，以默认租户的平台清单为模板铺一批<b>禁用状态</b>的占位行，方便租户管理员补充自己的凭据；
 * 占位行<b>不复制任何 clientId / clientSecret</b>，避免凭据跨租户串用。</p>
 *
 * @author muyiluop
 * @since 2026/9/23 21:30
 */
@Service
@RequiredArgsConstructor
public class SocialDataApiImpl implements TenantDataApi {

    private final SocialConfigMapper baseMapper;
    private final SocialConfigService socialConfigService;
    private final TenantExtensionProperties tenantExtensionProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init(TenantDTO tenant) {
        // 租户初始化不需要进行操作
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear() {
        // 调用方已在租户上下文中执行，租户条件由拦截器自动拼接
        baseMapper.delete(Wrappers.<SocialConfigDO>query().eq("1", 1));
    }

    /**
     * 查询默认租户的平台配置（作为新租户的模板）
     *
     * @return 默认租户的平台配置列表
     */
    private List<SocialConfigDO> listDefaultTenantConfig() {
        AtomicReference<List<SocialConfigDO>> reference = new AtomicReference<>(List.of());
        TenantUtils.execute(tenantExtensionProperties.getDefaultTenantId(), () -> reference.set(socialConfigService
            .list()));
        return reference.get();
    }
}
