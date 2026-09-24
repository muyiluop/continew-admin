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

package top.continew.admin.system.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.common.api.system.MenuApi;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.model.query.MenuQuery;
import top.continew.admin.system.service.MenuService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单业务 API 实现
 *
 * @author Charles7c
 * @since 2025/7/26 9:53
 */
@Service
@RequiredArgsConstructor
public class MenuApiImpl implements MenuApi {

    private final MenuService baseService;

    @Override
    public List<Tree<Long>> listTree(List<Long> excludeMenuIds, boolean isSimple) {
        MenuQuery query = new MenuQuery();
        query.setStatus(DisEnableStatusEnum.ENABLE);
        // 过滤掉租户不能使用的菜单
        query.setExcludeMenuIdList(excludeMenuIds);
        List<Tree<Long>> treeList = baseService.tree(query, null, isSimple);
        // 补充所属模块 ID，便于前端按模块分组展示（精简树不含该字段）
        Map<Long, Long> moduleIdMap = new HashMap<>();
        baseService.list(query, null).forEach(menu -> moduleIdMap.put(menu.getId(), menu.getModuleId()));
        treeList.forEach(tree -> this.fillModuleId(tree, moduleIdMap));
        return treeList;
    }

    /**
     * 递归补充所属模块 ID
     *
     * @param tree        树节点
     * @param moduleIdMap 菜单 ID 与模块 ID 映射
     */
    private void fillModuleId(Tree<Long> tree, Map<Long, Long> moduleIdMap) {
        tree.putExtra("moduleId", moduleIdMap.get(tree.getId()));
        if (CollUtil.isNotEmpty(tree.getChildren())) {
            tree.getChildren().forEach(child -> this.fillModuleId(child, moduleIdMap));
        }
    }
}
