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

package top.continew.admin.social.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.springframework.stereotype.Service;
import top.continew.admin.common.base.service.BaseServiceImpl;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.model.req.CommonStatusUpdateReq;
import top.continew.admin.social.mapper.SocialConfigMapper;
import top.continew.admin.social.model.entity.SocialConfigDO;
import top.continew.admin.social.model.query.SocialConfigQuery;
import top.continew.admin.social.model.req.SocialConfigReq;
import top.continew.admin.social.model.resp.SocialConfigDetailResp;
import top.continew.admin.social.model.resp.SocialConfigResp;
import top.continew.admin.social.service.SocialConfigService;
import top.continew.starter.core.autoconfigure.application.ApplicationProperties;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.util.validation.CheckUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 社交登录平台配置业务实现
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Service
@RequiredArgsConstructor
public class SocialConfigServiceImpl extends BaseServiceImpl<SocialConfigMapper, SocialConfigDO, SocialConfigResp, SocialConfigDetailResp, SocialConfigQuery, SocialConfigReq> implements SocialConfigService {

    private final ApplicationProperties applicationProperties;

    @Override
    public void beforeCreate(SocialConfigReq req) {
        req.setSource(req.getSource().toUpperCase(Locale.ROOT));
        // 回调地址由服务端自动生成
        req.setRedirectUri(this.buildRedirectUri(req.getSource()));
        this.checkSource(req.getSource(), null);
    }

    @Override
    public void beforeUpdate(SocialConfigReq req, Long id) {
        req.setSource(req.getSource().toUpperCase(Locale.ROOT));
        // 回调地址由服务端自动生成
        req.setRedirectUri(this.buildRedirectUri(req.getSource()));
        this.checkSource(req.getSource(), id);
        // Client Secret 留空表示不修改
        if (StrUtil.isBlank(req.getClientSecret())) {
            req.setClientSecret(this.getById(id).getClientSecret());
        }
    }

    @Override
    public SocialConfigDetailResp get(Long id) {
        SocialConfigDetailResp detailResp = super.get(id);
        detailResp.setClientSecretConfigured(StrUtil.isNotBlank(this.getById(id).getClientSecret()));
        return detailResp;
    }

    @Override
    public List<SocialConfigDO> listEnabled() {
        return baseMapper.lambdaQuery()
            .eq(SocialConfigDO::getStatus, DisEnableStatusEnum.ENABLE)
            .orderByAsc(SocialConfigDO::getSort)
            .list();
    }

    @Override
    public SocialConfigDO getBySource(String source) {
        if (StrUtil.isBlank(source)) {
            return null;
        }
        return baseMapper.lambdaQuery()
            .eq(SocialConfigDO::getSource, source.toUpperCase(Locale.ROOT))
            .oneOpt()
            .orElse(null);
    }

    @Override
    public SocialConfigDO getEnabledBySource(String source) {
        SocialConfigDO config = this.getBySource(source);
        CheckUtils.throwIfNull(config, "平台 [{}] 未配置", source);
        CheckUtils.throwIfEqual(DisEnableStatusEnum.DISABLE, config.getStatus(), "平台 [{}] 未启用", source);
        return config;
    }

    @Override
    public void updateStatus(CommonStatusUpdateReq req, Long id) {
        SocialConfigDO config = super.getById(id);
        DisEnableStatusEnum newStatus = req.getStatus();
        // 状态未改变
        if (config.getStatus().equals(newStatus)) {
            return;
        }
        baseMapper.lambdaUpdate().eq(SocialConfigDO::getId, id).set(SocialConfigDO::getStatus, newStatus).update();
    }

    @Override
    public String buildRedirectUri(String source) {
        String url = applicationProperties.getUrl();
        if (StrUtil.isBlank(url) || StrUtil.isBlank(source)) {
            return null;
        }
        String baseUrl = StrUtil.removeSuffix(url, StringConstants.SLASH);
        return "%s/social/callback?source=%s".formatted(baseUrl, source.toLowerCase(Locale.ROOT));
    }

    /**
     * 检查平台是否支持及是否重复
     *
     * @param source 平台
     * @param id     ID
     */
    private void checkSource(String source, Long id) {
        CheckUtils.throwIf(Arrays.stream(AuthDefaultSource.values()).noneMatch(item -> item.name().equals(source)), "不支持的平台 [{}]",
            source);
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(SocialConfigDO::getSource, source)
            .ne(id != null, SocialConfigDO::getId, id)
            .exists(), "平台 [{}] 已配置", source);
    }
}
