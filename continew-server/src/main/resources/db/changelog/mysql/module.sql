-- liquibase formatted sql

-- changeset continew:module-1
-- comment 新增业务模块表并为菜单补充所属模块

CREATE TABLE IF NOT EXISTS `sys_module` (
    `id`          bigint(20)   AUTO_INCREMENT                  COMMENT 'ID',
    `name`        varchar(30)  NOT NULL                        COMMENT '模块名称',
    `code`        varchar(50)  NOT NULL                        COMMENT '模块编码（唯一，建议与权限/路由前缀一致）',
    `icon`        varchar(50)  DEFAULT NULL                    COMMENT '图标',
    `platforms`   varchar(255) DEFAULT NULL                    COMMENT '所属端（JSON 数组，取值于字典 client_type；空表示不限端）',
    `home_path`   varchar(255) DEFAULT NULL                    COMMENT '模块默认落点路由',
    `sort`        int          NOT NULL DEFAULT 999            COMMENT '排序',
    `status`      tinyint(1)   UNSIGNED NOT NULL DEFAULT 1      COMMENT '状态（1：启用；2：禁用）',
    `description` varchar(200) DEFAULT NULL                    COMMENT '描述',
    `create_user` bigint(20)   NOT NULL                        COMMENT '创建人',
    `create_time` datetime     NOT NULL                        COMMENT '创建时间',
    `update_user` bigint(20)   DEFAULT NULL                    COMMENT '修改人',
    `update_time` datetime     DEFAULT NULL                    COMMENT '修改时间',
    `deleted`     bigint(20)   NOT NULL DEFAULT 0              COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_module_name`(`name`, `deleted`),
    UNIQUE INDEX `uk_module_code`(`code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务模块表';

ALTER TABLE `sys_menu` ADD COLUMN `module_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '所属模块ID（0：未分组）';
CREATE INDEX `idx_menu_module_id` ON `sys_menu`(`module_id`);

-- changeset continew:module-2
-- comment 初始化系统模块并回填现有菜单
INSERT INTO `sys_module`
(`id`, `name`, `code`, `icon`, `platforms`, `home_path`, `sort`, `status`, `description`, `create_user`, `create_time`)
VALUES
(1, '系统', 'system', 'settings', NULL, NULL, 1, 1, '平台内置功能模块', 1, NOW());

UPDATE `sys_menu` SET `module_id` = 1 WHERE `module_id` = 0;

-- changeset continew:module-3
-- comment 初始化模块管理菜单
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`,
 `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`, `module_id`)
VALUES
(1290, '模块管理', 1000, 2, '/system/module', 'SystemModule', 'system/module/index', NULL, 'apps', b'0', b'0', b'0',
 NULL, 9, 1, 1, NOW(), 1),
(1291, '列表', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:list', 1, 1, 1, NOW(), 1),
(1292, '详情', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:get', 2, 1, 1, NOW(), 1),
(1293, '新增', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:create', 3, 1, 1, NOW(), 1),
(1294, '修改', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:update', 4, 1, 1, NOW(), 1),
(1295, '删除', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:delete', 5, 1, 1, NOW(), 1);
