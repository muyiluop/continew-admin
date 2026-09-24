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

package top.continew.admin.social.service;

import top.continew.admin.common.base.service.BaseService;
import top.continew.admin.common.model.req.CommonStatusUpdateReq;
import top.continew.admin.social.model.entity.SocialConfigDO;
import top.continew.admin.social.model.query.SocialConfigQuery;
import top.continew.admin.social.model.req.SocialConfigReq;
import top.continew.admin.social.model.resp.SocialConfigDetailResp;
import top.continew.admin.social.model.resp.SocialConfigResp;
import top.continew.starter.data.service.IService;

import java.util.List;

/**
 * 社交登录平台配置业务接口
 *
 * @author muyiluop
 * @since 2026/9/23 20:00
 */
public interface SocialConfigService extends BaseService<SocialConfigResp, SocialConfigDetailResp, SocialConfigQuery, SocialConfigReq>, IService<SocialConfigDO> {

    /**
     * 查询已启用的平台列表
     *
     * @return 已启用的平台列表
     */
    List<SocialConfigDO> listEnabled();

    /**
     * 根据平台查询
     *
     * @param source 平台
     * @return 平台配置（不存在时返回 null）
     */
    SocialConfigDO getBySource(String source);

    /**
     * 根据平台查询已启用的配置
     *
     * @param source 平台
     * @return 平台配置
     */
    SocialConfigDO getEnabledBySource(String source);

    /**
     * 修改状态
     *
     * @param req 状态修改请求参数
     * @param id  ID
     */
    void updateStatus(CommonStatusUpdateReq req, Long id);

    /**
     * 构建回调地址
     *
     * <p>回调地址由应用地址与平台标识自动生成，避免手工填写出错。</p>
     *
     * @param source 平台
     * @return 回调地址
     */
    String buildRedirectUri(String source);
}
