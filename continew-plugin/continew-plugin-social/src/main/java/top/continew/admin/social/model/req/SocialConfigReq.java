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

package top.continew.admin.social.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.starter.extension.crud.validation.CrudValidationGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * 社交登录平台配置创建或修改请求参数
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Data
@Schema(description = "社交登录平台配置创建或修改请求参数")
public class SocialConfigReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台
     */
    @Schema(description = "平台", example = "GITEE")
    @NotBlank(message = "平台不能为空")
    @Length(max = 50, message = "平台长度不能超过 {max} 个字符")
    private String source;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "Gitee")
    @NotBlank(message = "名称不能为空")
    @Length(max = 50, message = "名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * Client ID
     */
    @Schema(description = "Client ID", example = "b1e2c3d4e5f6")
    @NotBlank(message = "Client ID 不能为空")
    @Length(max = 255, message = "Client ID 长度不能超过 {max} 个字符")
    private String clientId;

    /**
     * Client Secret
     */
    @Schema(description = "Client Secret（修改时留空表示不修改）", example = "a1b2c3d4e5f6")
    @NotBlank(message = "Client Secret 不能为空", groups = CrudValidationGroup.Create.class)
    @Length(max = 512, message = "Client Secret 长度不能超过 {max} 个字符")
    private String clientSecret;

    /**
     * Agent ID
     */
    @Schema(description = "Agent ID（微信、钉钉等平台需要）", example = "wx1234567890")
    @Length(max = 255, message = "Agent ID 长度不能超过 {max} 个字符")
    private String agentId;

    /**
     * 回调地址（服务端自动生成，忽略客户端传入值）
     */
    @Schema(hidden = true)
    private String redirectUri;

    /**
     * 授权范围
     */
    @Schema(description = "授权范围（多个以英文逗号分隔）", example = "user_info")
    @Length(max = 500, message = "授权范围长度不能超过 {max} 个字符")
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
     * 扩展配置
     */
    @Schema(description = "扩展配置（JSON）", example = "{}")
    private String extConfig;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "Gitee 登录")
    @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private DisEnableStatusEnum status;
}
