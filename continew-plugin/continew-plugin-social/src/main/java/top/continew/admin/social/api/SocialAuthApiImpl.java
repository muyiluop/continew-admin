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

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.AuthRequestBuilder;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Service;
import top.continew.admin.common.api.social.SocialAuthApi;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.common.model.dto.SocialPlatformDTO;
import top.continew.admin.social.model.entity.SocialConfigDO;
import top.continew.admin.social.service.SocialConfigService;
import top.continew.starter.core.exception.BadRequestException;

import java.util.List;

/**
 * 社交登录业务 API 实现
 *
 * @author muyiluop
 * @since 2026/9/23 21:00
 */
@Service
@RequiredArgsConstructor
public class SocialAuthApiImpl implements SocialAuthApi {

    private final SocialConfigService socialConfigService;
    private final AuthStateCache authStateCache;

    @Override
    public List<SocialPlatformDTO> listEnabledPlatforms() {
        return socialConfigService.listEnabled().stream().map(config -> {
            SocialPlatformDTO platform = new SocialPlatformDTO();
            platform.setSource(config.getSource());
            platform.setName(config.getName());
            return platform;
        }).toList();
    }

    @Override
    public String getPlatformName(String source) {
        SocialConfigDO config = socialConfigService.getBySource(source);
        return config == null ? source : config.getName();
    }

    @Override
    public boolean isEnabled(String source) {
        SocialConfigDO config = socialConfigService.getBySource(source);
        return config != null && DisEnableStatusEnum.ENABLE.equals(config.getStatus());
    }

    @Override
    public String getAuthorizeUrl(String source, String state) {
        return this.buildAuthRequest(source).authorize(state);
    }

    @Override
    public AuthResponse<AuthUser> login(String source, AuthCallback callback) {
        return this.buildAuthRequest(source).login(callback);
    }

    /**
     * 构建 AuthRequest
     *
     * @param source 平台
     * @return AuthRequest
     */
    private AuthRequest buildAuthRequest(String source) {
        SocialConfigDO config = socialConfigService.getEnabledBySource(source);
        try {
            AuthConfig authConfig = AuthConfig.builder()
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .redirectUri(socialConfigService.buildRedirectUri(config.getSource()))
                .agentId(config.getAgentId())
                .unionId(Boolean.TRUE.equals(config.getUnionId()))
                .ignoreCheckState(Boolean.TRUE.equals(config.getIgnoreCheckState()))
                .scopes(StrUtil.isBlank(config.getScopes()) ? null : StrUtil.split(config.getScopes(), ','))
                .build();
            return AuthRequestBuilder.builder()
                .source(config.getSource())
                .authConfig(authConfig)
                .authStateCache(authStateCache)
                .build();
        } catch (Exception e) {
            throw new BadRequestException("暂不支持 [%s] 平台账号登录".formatted(source));
        }
    }
}
