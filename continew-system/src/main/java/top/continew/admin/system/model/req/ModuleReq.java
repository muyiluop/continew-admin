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

package top.continew.admin.system.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 业务模块创建或修改请求参数
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
@Data
@Schema(description = "业务模块创建或修改请求参数")
public class ModuleReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    @Schema(description = "模块名称", example = "系统")
    @NotBlank(message = "模块名称不能为空")
    @Length(max = 30, message = "模块名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 模块编码
     */
    @Schema(description = "模块编码", example = "system")
    @NotBlank(message = "模块编码不能为空")
    @Length(max = 50, message = "模块编码长度不能超过 {max} 个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "模块编码只能以字母开头，且只能包含字母、数字、下划线和横线")
    private String code;

    /**
     * 图标
     */
    @Schema(description = "图标", example = "settings")
    @Length(max = 50, message = "图标长度不能超过 {max} 个字符")
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
    @Length(max = 255, message = "模块默认落点路由长度不能超过 {max} 个字符")
    private String homePath;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @NotNull(message = "排序不能为空")
    @Min(value = 1, message = "排序最小值为 {value}")
    private Integer sort;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态无效")
    private DisEnableStatusEnum status;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
    private String description;
}
