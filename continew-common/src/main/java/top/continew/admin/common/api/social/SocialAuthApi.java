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

package top.continew.admin.common.api.social;

import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import top.continew.admin.common.model.dto.SocialPlatformDTO;

import java.util.List;

/**
 * 社交登录业务 API
 *
 * <p>第三方登录的配置由社交登录插件（continew-plugin-social）管理，其它模块通过本接口使用，避免依赖具体实现。</p>
 *
 * @author muyiluop
 * @since 2026/9/23 21:00
 */
public interface SocialAuthApi {

    /**
     * 查询当前租户已启用的平台列表
     *
     * @return 平台列表
     */
    List<SocialPlatformDTO> listEnabledPlatforms();

    /**
     * 查询平台名称
     *
     * @param source 平台
     * @return 平台名称（未配置时返回平台标识本身）
     */
    String getPlatformName(String source);

    /**
     * 平台是否已启用
     *
     * @param source 平台
     * @return true：已启用；false：未启用
     */
    boolean isEnabled(String source);

    /**
     * 获取授权地址
     *
     * @param source 平台
     * @param state  状态
     * @return 授权地址
     */
    String getAuthorizeUrl(String source, String state);

    /**
     * 处理第三方回调
     *
     * @param source   平台
     * @param callback 回调参数
     * @return 第三方用户信息
     */
    AuthResponse<AuthUser> login(String source, AuthCallback callback);
}
