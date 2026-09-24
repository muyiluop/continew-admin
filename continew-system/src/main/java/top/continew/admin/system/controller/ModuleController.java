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

package top.continew.admin.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.base.controller.BaseController;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.model.query.ModuleQuery;
import top.continew.admin.system.model.req.ModuleReq;
import top.continew.admin.system.model.resp.ModuleResp;
import top.continew.admin.system.service.ModuleService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

import java.util.List;

/**
 * 业务模块管理 API
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
@Tag(name = "业务模块管理 API")
@RestController
@CrudRequestMapping(value = "/system/module", api = {Api.PAGE, Api.LIST, Api.GET, Api.CREATE, Api.UPDATE,
    Api.BATCH_DELETE})
public class ModuleController extends BaseController<ModuleService, ModuleResp, ModuleResp, ModuleQuery, ModuleReq> {

    @Operation(summary = "查询模块字典列表", description = "查询启用状态的模块列表，供按模块选择菜单的页面使用")
    @GetMapping("/dict")
    public List<ModuleResp> listModuleDict() {
        return baseService.list(new ModuleQuery(DisEnableStatusEnum.ENABLE), null);
    }
}
