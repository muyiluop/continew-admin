-- liquibase formatted sql

-- changeset continew:module-1
-- comment 新增业务模块表并为菜单补充所属模块

CREATE TABLE IF NOT EXISTS "sys_module" (
    "id"          int8         NOT NULL,
    "name"        varchar(30)  NOT NULL,
    "code"        varchar(50)  NOT NULL,
    "icon"        varchar(50)  DEFAULT NULL,
    "platforms"   varchar(255) DEFAULT NULL,
    "home_path"   varchar(255) DEFAULT NULL,
    "sort"        int4         NOT NULL DEFAULT 999,
    "status"      int2         NOT NULL DEFAULT 1,
    "description" varchar(200) DEFAULT NULL,
    "create_user" int8         NOT NULL,
    "create_time" timestamp    NOT NULL,
    "update_user" int8         DEFAULT NULL,
    "update_time" timestamp    DEFAULT NULL,
    "deleted"     int8         NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_module_name" ON "sys_module" ("name", "deleted");
CREATE UNIQUE INDEX "uk_module_code" ON "sys_module" ("code", "deleted");
COMMENT ON COLUMN "sys_module"."id"          IS 'ID';
COMMENT ON COLUMN "sys_module"."name"        IS '模块名称';
COMMENT ON COLUMN "sys_module"."code"        IS '模块编码（唯一，建议与权限/路由前缀一致）';
COMMENT ON COLUMN "sys_module"."icon"        IS '图标';
COMMENT ON COLUMN "sys_module"."platforms"   IS '所属端（JSON 数组，取值于字典 client_type；空表示不限端）';
COMMENT ON COLUMN "sys_module"."home_path"   IS '模块默认落点路由';
COMMENT ON COLUMN "sys_module"."sort"        IS '排序';
COMMENT ON COLUMN "sys_module"."status"      IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_module"."description" IS '描述';
COMMENT ON COLUMN "sys_module"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_module"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_module"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_module"."update_time" IS '修改时间';
COMMENT ON COLUMN "sys_module"."deleted"     IS '是否已删除（0：否；id：是）';
COMMENT ON TABLE  "sys_module"               IS '业务模块表';

ALTER TABLE "sys_menu" ADD COLUMN "module_id" int8 NOT NULL DEFAULT 0;
CREATE INDEX "idx_menu_module_id" ON "sys_menu" ("module_id");
COMMENT ON COLUMN "sys_menu"."module_id" IS '所属模块ID（0：未分组）';

-- changeset continew:module-2
-- comment 初始化系统模块并回填现有菜单
INSERT INTO "sys_module"
("id", "name", "code", "icon", "platforms", "home_path", "sort", "status", "description", "create_user", "create_time")
VALUES
(1, '系统', 'system', 'settings', NULL, NULL, 1, 1, '平台内置功能模块', 1, NOW());

UPDATE "sys_menu" SET "module_id" = 1 WHERE "module_id" = 0;

-- changeset continew:module-3
-- comment 初始化模块管理菜单
INSERT INTO "sys_menu"
("id", "title", "parent_id", "type", "path", "name", "component", "redirect", "icon", "is_external", "is_cache",
 "is_hidden", "permission", "sort", "status", "create_user", "create_time", "module_id")
VALUES
(1290, '模块管理', 1000, 2, '/system/module', 'SystemModule', 'system/module/index', NULL, 'apps', false, false, false,
 NULL, 9, 1, 1, NOW(), 1),
(1291, '列表', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:list', 1, 1, 1, NOW(), 1),
(1292, '详情', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:get', 2, 1, 1, NOW(), 1),
(1293, '新增', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:create', 3, 1, 1, NOW(), 1),
(1294, '修改', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:update', 4, 1, 1, NOW(), 1),
(1295, '删除', 1290, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:module:delete', 5, 1, 1, NOW(), 1);
