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

package top.continew.admin.social.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 社交登录平台配置详情响应参数
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Data
@Schema(description = "社交登录平台配置详情响应参数")
public class SocialConfigDetailResp extends SocialConfigResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 授权范围（多个以英文逗号分隔）
     */
    @Schema(description = "授权范围（多个以英文逗号分隔）", example = "user_info")
    private String scopes;

    /**
     * 是否使用 UnionId
     */
    @Schema(description = "是否使用 UnionId", example = "false")
    private Boolean unionId;

    /**
     * 是否忽略 state 校验
     */
    @Schema(description = "是否忽略 state 校验", example = "false")
    private Boolean ignoreCheckState;

    /**
     * 扩展配置（JSON，存放平台特有字段）
     */
    @Schema(description = "扩展配置（JSON）", example = "{}")
    private String extConfig;

    /**
     * Client Secret 是否已配置（不返回密钥本身）
     */
    @Schema(description = "Client Secret 是否已配置", example = "true")
    private Boolean clientSecretConfigured;
}
