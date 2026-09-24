-- liquibase formatted sql

-- changeset muyiluop:1
-- comment 初始化社交登录插件数据表
-- 初始化表结构
CREATE TABLE IF NOT EXISTS "sys_social_config" (
    "id"                 int8         NOT NULL,
    "source"             varchar(50)  NOT NULL,
    "name"               varchar(50)  NOT NULL,
    "client_id"          varchar(255) NOT NULL,
    "client_secret"      varchar(512) NOT NULL,
    "agent_id"           varchar(255) DEFAULT NULL,
    "redirect_uri"       varchar(500) DEFAULT NULL,
    "scopes"             varchar(500) DEFAULT NULL,
    "union_id"           bool         NOT NULL DEFAULT FALSE,
    "ignore_check_state" bool         NOT NULL DEFAULT FALSE,
    "ext_config"         text         DEFAULT NULL,
    "sort"               int4         NOT NULL DEFAULT 1,
    "description"        varchar(200) DEFAULT NULL,
    "status"             int2         NOT NULL DEFAULT 1,
    "tenant_id"          int8         NOT NULL DEFAULT 0,
    "create_user"        int8         NOT NULL,
    "create_time"        timestamp    NOT NULL,
    "update_user"        int8         DEFAULT NULL,
    "update_time"        timestamp    DEFAULT NULL,
    "deleted"            int8         NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_social_config_source" ON "sys_social_config" ("source", "tenant_id", "deleted");
CREATE INDEX "idx_social_config_tenant_id" ON "sys_social_config" ("tenant_id");
CREATE INDEX "idx_social_config_create_user" ON "sys_social_config" ("create_user");
CREATE INDEX "idx_social_config_update_user" ON "sys_social_config" ("update_user");
CREATE INDEX "idx_social_config_deleted" ON "sys_social_config" ("deleted");
COMMENT ON TABLE  "sys_social_config"                    IS '社交登录平台配置表';
COMMENT ON COLUMN "sys_social_config"."id"                 IS 'ID';
COMMENT ON COLUMN "sys_social_config"."source"             IS '平台（AuthDefaultSource 名称）';
COMMENT ON COLUMN "sys_social_config"."name"               IS '名称';
COMMENT ON COLUMN "sys_social_config"."client_id"          IS 'Client ID';
COMMENT ON COLUMN "sys_social_config"."client_secret"      IS 'Client Secret（加密存储）';
COMMENT ON COLUMN "sys_social_config"."agent_id"           IS 'Agent ID（微信、钉钉等平台需要）';
COMMENT ON COLUMN "sys_social_config"."redirect_uri"       IS '回调地址';
COMMENT ON COLUMN "sys_social_config"."scopes"             IS '授权范围（多个以英文逗号分隔）';
COMMENT ON COLUMN "sys_social_config"."union_id"           IS '是否使用 UnionId';
COMMENT ON COLUMN "sys_social_config"."ignore_check_state" IS '是否忽略 state 校验';
COMMENT ON COLUMN "sys_social_config"."ext_config"         IS '扩展配置（JSON）';
COMMENT ON COLUMN "sys_social_config"."sort"               IS '排序';
COMMENT ON COLUMN "sys_social_config"."description"        IS '描述';
COMMENT ON COLUMN "sys_social_config"."status"             IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_social_config"."tenant_id"          IS '租户ID';
COMMENT ON COLUMN "sys_social_config"."create_user"        IS '创建人';
COMMENT ON COLUMN "sys_social_config"."create_time"        IS '创建时间';
COMMENT ON COLUMN "sys_social_config"."update_user"        IS '修改人';
COMMENT ON COLUMN "sys_social_config"."update_time"        IS '修改时间';
COMMENT ON COLUMN "sys_social_config"."deleted"            IS '是否已删除（0：否；id：是）';

-- changeset muyiluop:2
-- comment 初始化社交登录菜单
-- 初始化默认菜单
INSERT INTO "sys_menu" ("id", "title", "parent_id", "type", "path", "name", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden", "permission", "sort", "status", "create_user", "create_time")
VALUES
(6000, '社交登录', 0, 1, '/social', 'Social', 'Layout', '/social/config', 'share-alt', false, false, false, NULL, 8, 1, 1, NOW()),

(6010, '平台配置', 6000, 2, '/social/config', 'SocialConfig', 'social/config/index', NULL, 'link', false, false, false, NULL, 1, 1, 1, NOW()),
(6011, '列表', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:list', 1, 1, 1, NOW()),
(6012, '详情', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:get', 2, 1, 1, NOW()),
(6013, '新增', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:create', 3, 1, 1, NOW()),
(6014, '修改', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:update', 4, 1, 1, NOW()),
(6015, '删除', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:delete', 5, 1, 1, NOW()),
(6016, '导出', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:export', 6, 1, 1, NOW());

-- changeset muyiluop:3
-- comment 新增社交登录平台配置状态修改菜单
INSERT INTO "sys_menu" ("id", "title", "parent_id", "type", "path", "name", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden", "permission", "sort", "status", "create_user", "create_time")
VALUES
(6017, '修改状态', 6010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'social:config:updateStatus', 7, 1, 1, NOW());

-- changeset muyiluop:4
-- comment 移除导出菜单（社交登录平台配置不提供导出）
DELETE FROM "sys_menu" WHERE "id" = 6016 AND "permission" = 'social:config:export';

-- changeset muyiluop:5
-- comment 社交登录菜单迁移到「系统管理 > 系统配置」下（页签）
INSERT INTO "sys_menu" ("id", "title", "parent_id", "type", "path", "name", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden", "permission", "sort", "status", "create_user", "create_time")
VALUES
(1260, '社交登录', 1150, 2, '/system/config?tab=social', 'SystemSocialConfig', 'system/config/social/index', NULL, 'share-alt', false, false, true, NULL, 8, 1, 1, NOW());
UPDATE "sys_menu" SET "parent_id" = 1260 WHERE "id" BETWEEN 6011 AND 6017;
DELETE FROM "sys_menu" WHERE "id" IN (6000, 6010);
