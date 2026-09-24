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

package top.continew.admin.social.core;

import me.zhyd.oauth.cache.AuthStateCache;
import top.continew.admin.social.constant.SocialConstants;
import top.continew.starter.cache.redisson.util.RedisUtils;

import java.time.Duration;

/**
 * 基于 Redis 的授权 state 缓存实现
 *
 * <p>替代 justauth-spring-boot-starter 自带的实现，避免多余的自动装配。</p>
 *
 * @author muyiluop
 * @since 2026/9/23 21:00
 */
public class RedisAuthStateCache implements AuthStateCache {

    /**
     * 默认过期时间
     */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(15);

    @Override
    public void cache(String key, String value) {
        RedisUtils.set(SocialConstants.STATE_CACHE_KEY_PREFIX + key, value, DEFAULT_TIMEOUT);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        RedisUtils.set(SocialConstants.STATE_CACHE_KEY_PREFIX + key, value, Duration.ofMillis(timeout));
    }

    @Override
    public String get(String key) {
        return RedisUtils.get(SocialConstants.STATE_CACHE_KEY_PREFIX + key);
    }

    @Override
    public boolean containsKey(String key) {
        return RedisUtils.exists(SocialConstants.STATE_CACHE_KEY_PREFIX + key);
    }
}
