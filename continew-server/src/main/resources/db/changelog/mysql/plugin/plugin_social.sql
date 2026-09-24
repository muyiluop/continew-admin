-- liquibase formatted sql

-- changeset muyiluop:1
-- comment 初始化社交登录插件数据表
-- 初始化表结构
CREATE TABLE IF NOT EXISTS `sys_social_config` (
    `id`                 bigint(20)   AUTO_INCREMENT              COMMENT 'ID',
    `source`             varchar(50)  NOT NULL                    COMMENT '平台（AuthDefaultSource 名称）',
    `name`               varchar(50)  NOT NULL                    COMMENT '名称',
    `client_id`          varchar(255) NOT NULL                    COMMENT 'Client ID',
    `client_secret`      varchar(512) NOT NULL                    COMMENT 'Client Secret（加密存储）',
    `agent_id`           varchar(255) DEFAULT NULL                COMMENT 'Agent ID（微信、钉钉等平台需要）',
    `redirect_uri`       varchar(500) DEFAULT NULL                COMMENT '回调地址',
    `scopes`             varchar(500) DEFAULT NULL                COMMENT '授权范围（多个以英文逗号分隔）',
    `union_id`           bit(1)       NOT NULL DEFAULT b'0'       COMMENT '是否使用 UnionId',
    `ignore_check_state` bit(1)       NOT NULL DEFAULT b'0'       COMMENT '是否忽略 state 校验',
    `ext_config`         text         DEFAULT NULL                COMMENT '扩展配置（JSON）',
    `sort`               int          NOT NULL DEFAULT 1          COMMENT '排序',
    `description`        varchar(200) DEFAULT NULL                COMMENT '描述',
    `status`             tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
    `tenant_id`          bigint(20)   NOT NULL DEFAULT 0          COMMENT '租户ID',
    `create_user`        bigint(20)   NOT NULL                    COMMENT '创建人',
    `create_time`        datetime     NOT NULL                    COMMENT '创建时间',
    `update_user`        bigint(20)   DEFAULT NULL                COMMENT '修改人',
    `update_time`        datetime     DEFAULT NULL                COMMENT '修改时间',
    `deleted`            bigint(20)   NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_social_config_source`(`source`, `tenant_id`, `deleted`),
    INDEX `idx_social_config_tenant_id`(`tenant_id`),
    INDEX `idx_social_config_create_user`(`create_user`),
    INDEX `idx_social_config_update_user`(`update_user`),
    INDEX `idx_social_config_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交登录平台配置表';

-- changeset muyiluop:2
-- comment 初始化社交登录菜单
-- 初始化默认菜单
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(6000, '社交登录', 0, 1, '/social', 'Social', 'Layout', '/social/config', 'share-alt', b'0', b'0', b'0', NULL, 8, 1, 1, NOW()),

(6010, '平台配置', 6000, 2, '/social/config', 'SocialConfig', 'social/config/index', NULL, 'link', b'0', b'0', b'0', NULL, 1, 1, 1, NOW()),
(6011, '列表', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:list', 1, 1, 1, NOW()),
(6012, '详情', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:get', 2, 1, 1, NOW()),
(6013, '新增', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:create', 3, 1, 1, NOW()),
(6014, '修改', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:update', 4, 1, 1, NOW()),
(6015, '删除', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:delete', 5, 1, 1, NOW()),
(6016, '导出', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:export', 6, 1, 1, NOW());

-- changeset muyiluop:3
-- comment 新增社交登录平台配置状态修改菜单
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(6017, '修改状态', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:updateStatus', 7, 1, 1, NOW());

-- changeset muyiluop:4
-- comment 移除导出菜单（社交登录平台配置不提供导出）
DELETE FROM `sys_menu` WHERE `id` = 6016 AND `permission` = 'social:config:export';

-- changeset muyiluop:5
-- comment 社交登录菜单迁移到「系统管理 > 系统配置」下（页签）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1260, '社交登录', 1150, 2, '/system/config?tab=social', 'SystemSocialConfig', 'system/config/social/index', NULL, 'share-alt', b'0', b'0', b'1', NULL, 8, 1, 1, NOW());
UPDATE `sys_menu` SET `parent_id` = 1260 WHERE `id` BETWEEN 6011 AND 6017;
DELETE FROM `sys_menu` WHERE `id` IN (6000, 6010);
