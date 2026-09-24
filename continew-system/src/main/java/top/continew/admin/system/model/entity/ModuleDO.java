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

package top.continew.admin.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;
import java.util.List;

/**
 * 业务模块实体
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
@Data
@TableName(value = "sys_module", autoResultMap = true)
public class ModuleDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 模块编码（唯一，建议与权限/路由前缀一致）
     */
    private String code;

    /**
     * 图标
     */
    private String icon;

    /**
     * 所属端（取值于字典 client_type；为空表示不限端）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> platforms;

    /**
     * 模块默认落点路由
     */
    private String homePath;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private DisEnableStatusEnum status;

    /**
     * 描述
     */
    private String description;
}
