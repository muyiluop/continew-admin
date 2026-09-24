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

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.api.social.SocialAuthApi;
import top.continew.admin.common.model.dto.SocialPlatformDTO;
import top.continew.starter.log.annotation.Log;

import java.util.List;

/**
 * 社交登录公共 API
 *
 * <p>注意：本控制器不加 {@code @TenantIgnore}，需要保留租户上下文，以便按当前租户过滤平台配置。</p>
 *
 * @author muyiluop
 * @since 2026/9/23 21:00
 */
@Tag(name = "社交登录公共 API")
@Log(ignore = true)
@Validated
@RequiredArgsConstructor
@RestController("socialCommonController")
@RequestMapping("/social/common")
public class SocialCommonController {

    private final SocialAuthApi socialAuthApi;

    @SaIgnore
    @Operation(summary = "查询已启用的平台列表", description = "查询当前租户已启用的第三方登录平台列表")
    @GetMapping("/platforms")
    public List<SocialPlatformDTO> listEnabledPlatforms() {
        return socialAuthApi.listEnabledPlatforms();
    }
}
