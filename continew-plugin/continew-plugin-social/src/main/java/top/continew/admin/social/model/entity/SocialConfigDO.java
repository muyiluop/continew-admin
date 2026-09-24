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

package top.continew.admin.social.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.starter.encrypt.field.annotation.FieldEncrypt;

import java.io.Serial;

/**
 * 社交登录平台配置实体
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Data
@TableName("sys_social_config")
public class SocialConfigDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台（AuthDefaultSource 名称）
     */
    private String source;

    /**
     * 名称
     */
    private String name;

    /**
     * Client ID
     */
    private String clientId;

    /**
     * Client Secret
     */
    @FieldEncrypt
    private String clientSecret;

    /**
     * Agent ID（微信、钉钉等平台需要）
     */
    private String agentId;

    /**
     * 回调地址
     */
    private String redirectUri;

    /**
     * 授权范围（多个以英文逗号分隔）
     */
    private String scopes;

    /**
     * 是否使用 UnionId
     */
    private Boolean unionId;

    /**
     * 是否忽略 state 校验
     */
    private Boolean ignoreCheckState;

    /**
     * 扩展配置（JSON，存放平台特有字段）
     */
    private String extConfig;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private DisEnableStatusEnum status;
}
