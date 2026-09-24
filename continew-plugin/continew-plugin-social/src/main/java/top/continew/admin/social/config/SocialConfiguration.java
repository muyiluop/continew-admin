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

package top.continew.admin.social.config;

import me.zhyd.oauth.cache.AuthStateCache;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.continew.admin.social.core.RedisAuthStateCache;

/**
 * 社交登录配置
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Configuration
public class SocialConfiguration {

    /**
     * 授权 state 缓存（基于 Redis）
     */
    @Bean
    public AuthStateCache authStateCache() {
        return new RedisAuthStateCache();
    }

    /**
     * API 文档分组配置
     */
    @Bean
    public GroupedOpenApi socialApi() {
        return GroupedOpenApi.builder().group("social").displayName("社交登录").pathsToMatch("/social/**").build();
    }
}
