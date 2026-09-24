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

package top.continew.admin.social.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.base.controller.BaseController;
import top.continew.admin.common.model.req.CommonStatusUpdateReq;
import top.continew.admin.social.model.query.SocialConfigQuery;
import top.continew.admin.social.model.req.SocialConfigReq;
import top.continew.admin.social.model.resp.SocialConfigDetailResp;
import top.continew.admin.social.model.resp.SocialConfigResp;
import top.continew.admin.social.service.SocialConfigService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.extension.crud.model.resp.LabelValueResp;

import java.util.Arrays;
import java.util.List;

/**
 * 社交登录平台配置 API
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
@Tag(name = "社交登录平台配置 API")
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/social/config", api = {Api.PAGE, Api.LIST, Api.GET, Api.CREATE, Api.UPDATE,
    Api.BATCH_DELETE})
public class SocialConfigController extends BaseController<SocialConfigService, SocialConfigResp, SocialConfigDetailResp, SocialConfigQuery, SocialConfigReq> {

    @Operation(summary = "修改状态", description = "修改平台配置状态")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @SaCheckPermission("social:config:updateStatus")
    @PutMapping("/{id}/status")
    public void updateStatus(@RequestBody @Valid CommonStatusUpdateReq req, @PathVariable("id") Long id) {
        baseService.updateStatus(req, id);
    }

    @Operation(summary = "查询支持的平台列表", description = "查询 JustAuth 支持的第三方登录平台列表")
    @SaCheckPermission("social:config:list")
    @GetMapping("/source/list")
    public List<LabelValueResp<String>> listSource() {
        return Arrays.stream(AuthDefaultSource.values())
            .map(source -> new LabelValueResp<>(source.getName(), source.name()))
            .toList();
    }
}
