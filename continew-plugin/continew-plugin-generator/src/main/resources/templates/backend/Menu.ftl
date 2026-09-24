SET @parentId = ${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c};
-- 所属模块 ID（1 = 系统模块，如属其他业务模块请替换为对应模块 ID，可在「模块管理」中查看）
SET @moduleId = 1;
-- ${businessName}管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`, `module_id`)
VALUES
    (@parentId, '${businessName}管理', 1000, 2, '/${apiModuleName}/${apiName}', '${classNamePrefix}', '${apiModuleName}/${apiName}/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW(), @moduleId);

-- ${businessName}管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`, `module_id`)
VALUES
    (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '列表', @parentId, 3, '${apiModuleName}:${apiName}:list', 1, 1, 1, NOW(), @moduleId),
    (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '详情', @parentId, 3, '${apiModuleName}:${apiName}:get', 2, 1, 1, NOW(), @moduleId),
    (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '新增', @parentId, 3, '${apiModuleName}:${apiName}:create', 3, 1, 1, NOW(), @moduleId),
    (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '修改', @parentId, 3, '${apiModuleName}:${apiName}:update', 4, 1, 1, NOW(), @moduleId),
    (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '删除', @parentId, 3, '${apiModuleName}:${apiName}:delete', 5, 1, 1, NOW(), @moduleId),
    (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '导出', @parentId, 3, '${apiModuleName}:${apiName}:export', 6, 1, 1, NOW(), @moduleId);

<#---- PostgreSQL（切换 PostgreSQL 数据库时请注释掉其他数据库脚本，并解开此段注释）-->
<#--DO $$-->
<#--    DECLARE sys_menu_id_seq INT8 := ${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c};-->
<#--BEGIN-->

<#--    -- 所属模块 ID（1 = 系统模块，如属其他业务模块请替换为对应模块 ID，可在「模块管理」中查看）-->
<#--    -- ${businessName}管理菜单-->
<#--    INSERT INTO "sys_menu"-->
<#--        ("id", "title", "parent_id", "type", "path", "name", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden", "permission", "sort", "status", "create_user", "create_time", "module_id")-->
<#--    VALUES-->
<#--        (sys_menu_id_seq, '${businessName}管理', 1000, 2, '/${apiModuleName}/${apiName}', '${classNamePrefix}', '${apiModuleName}/${apiName}/index', NULL, NULL, false, false, false, NULL, 1, 1, 1, NOW(), 1);-->

<#--    -- ${businessName}管理按钮-->
<#--    INSERT INTO "sys_menu"-->
<#--        ("id", "title", "parent_id", "type", "permission", "sort", "status", "create_user", "create_time", "module_id")-->
<#--    VALUES-->
<#--        (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '列表', sys_menu_id_seq, 3, '${apiModuleName}:${apiName}:list', 1, 1, 1, NOW(), 1),-->
<#--        (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '详情', sys_menu_id_seq, 3, '${apiModuleName}:${apiName}:get', 2, 1, 1, NOW(), 1),-->
<#--        (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '新增', sys_menu_id_seq, 3, '${apiModuleName}:${apiName}:create', 3, 1, 1, NOW(), 1),-->
<#--        (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '修改', sys_menu_id_seq, 3, '${apiModuleName}:${apiName}:update', 4, 1, 1, NOW(), 1),-->
<#--        (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '删除', sys_menu_id_seq, 3, '${apiModuleName}:${apiName}:delete', 5, 1, 1, NOW(), 1),-->
<#--        (${statics["cn.hutool.core.util.IdUtil"].getSnowflakeNextId()?c}, '导出', sys_menu_id_seq, 3, '${apiModuleName}:${apiName}:export', 6, 1, 1, NOW(), 1);-->

<#--END $$;-->