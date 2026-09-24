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
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;

/**
 * 社交登录平台配置响应参数
 *
 * <p>注意：响应不包含 clientSecret，仅通过 clientSecretConfigured 表示是否已配置。</p>
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Data
@Schema(description = "社交登录平台配置响应参数")
public class SocialConfigResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台
     */
    @Schema(description = "平台", example = "GITEE")
    private String source;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "Gitee")
    private String name;

    /**
     * Client ID
     */
    @Schema(description = "Client ID", example = "b1e2c3d4e5f6")
    private String clientId;

    /**
     * Agent ID
     */
    @Schema(description = "Agent ID（微信、钉钉等平台需要）", example = "wx1234567890")
    private String agentId;

    /**
     * 回调地址
     */
    @Schema(description = "回调地址", example = "https://admin.continew.top/social/callback?source=gitee")
    private String redirectUri;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "Gitee 登录")
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private DisEnableStatusEnum status;
}
