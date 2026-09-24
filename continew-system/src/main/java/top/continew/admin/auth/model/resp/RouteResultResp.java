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

package top.continew.admin.auth.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.system.model.resp.ModuleResp;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户路由与模块响应参数
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
@Data
@Schema(description = "路由与模块响应参数")
public class RouteResultResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前端可见模块列表
     */
    @Schema(description = "当前端可见模块列表")
    private List<ModuleResp> modules;

    /**
     * 路由列表
     */
    @Schema(description = "路由列表")
    private List<RouteResp> routes;
}
