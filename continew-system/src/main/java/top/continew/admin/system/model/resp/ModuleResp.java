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

package top.continew.admin.system.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;
import java.util.List;

/**
 * 业务模块响应参数
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
@Data
@Schema(description = "业务模块响应参数")
public class ModuleResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    @Schema(description = "模块名称", example = "系统")
    private String name;

    /**
     * 模块编码
     */
    @Schema(description = "模块编码", example = "system")
    private String code;

    /**
     * 图标
     */
    @Schema(description = "图标", example = "settings")
    private String icon;

    /**
     * 所属端（取值于字典 client_type；为空表示不限端）
     */
    @Schema(description = "所属端", example = "PC")
    private List<String> platforms;

    /**
     * 模块默认落点路由
     */
    @Schema(description = "模块默认落点路由", example = "/system/user")
    private String homePath;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private DisEnableStatusEnum status;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
}
