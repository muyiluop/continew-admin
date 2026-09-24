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

package top.continew.admin.system.service;

import top.continew.admin.common.base.service.BaseService;
import top.continew.admin.system.model.query.ModuleQuery;
import top.continew.admin.system.model.req.ModuleReq;
import top.continew.admin.system.model.resp.ModuleResp;

import java.util.List;

/**
 * 业务模块业务接口
 *
 * @author Charles7c
 * @since 2025/9/24 11:00
 */
public interface ModuleService extends BaseService<ModuleResp, ModuleResp, ModuleQuery, ModuleReq> {

    /**
     * 查询指定客户端端可见的启用模块
     *
     * @param clientType 客户端类型（端），为空时按 PC 处理
     * @return 模块列表
     */
    List<ModuleResp> listEnabledByClientType(String clientType);
}
