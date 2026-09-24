# 社交登录插件（continew-plugin-social）设计方案

> 状态：**P0 ~ P3 全部落地**（模块 + 表 + CRUD + 配置页 + 菜单；登录链路切到 DB 配置、自动装配已移除；租户初始化与清理已实测通过；启停开关已实现）。
> 决策：跨模块 API 采用方案 A（接口沿用 JustAuth 类型，`JustAuth` 保留在 `continew-common`）。

## 1. 背景与目标

现状：第三方登录（JustAuth）的配置全部写在 `application-{dev,prod}.yml` 的 `justauth:` 段里，改配置要重启、无法按租户区分，且前端登录页的第三方入口是写死的。

目标：

1. 配置搬到数据库，在后台「社交登录 / 平台配置」菜单里可视化维护
2. 支持多租户：开启多租户时每个租户各配一套；未开启时自然只有默认租户一套
3. 登录页第三方入口按「当前租户已启用的平台」动态渲染
4. 顺带移除 JustAuth 相关的自动装配与 YAML 配置

## 2. 现状盘点

| 项 | 现状 |
| --- | --- |
| 依赖 | `continew-common/pom.xml` → `continew-starter-auth-justauth:2.14.0` → `JustAuth:1.16.7` + `justauth-spring-boot-starter:1.4.0` |
| 配置 | `application-{dev,prod}.yml` 的 `justauth.{enabled, type.<SOURCE>.*, cache.type}` |
| 硬依赖 `JustAuthProperties` | `AuthController`、`SocialLoginHandler`、`UserProfileController` |
| 授权 / 回调 | `GET /auth/{source}`；`SocialLoginHandler` |
| 绑定 | `sys_user_social`（已有 `tenant_id`） |
| 前端 | 登录页写死 3 个按钮、`/social/callback`、个人中心 `Social.vue` |
| 已知缺陷 | `justauth.cache.type=redis` 时 xkcoding 的 `JustAuthStateCacheConfiguration$Redis` 同时激活，注入 `RedisTemplate` 二义，Spring 6.1 下启动失败 |

## 3. 关键设计决策

| 编号 | 决策 |
| --- | --- |
| D1 | 排除 `justauth-spring-boot-starter`，仅保留 `JustAuth` core，自研 `AuthStateCache`（基于 `RedisUtils`） |
| D2 | 配置表 `sys_social_config`，租户级，**不进** `ignore-tables` |
| D3 | 跨模块走 `continew-common/api/social/` 接口 + 插件实现（方案 A） |
| D4 | 登录页用匿名接口取「当前租户已启用平台」，**只加 `@SaIgnore`，不加 `@TenantIgnore`** |
| D5 | `client_secret` 加密存储（`@FieldEncrypt`）且永不回显 |
| D6 | 租户初始化实现 `TenantDataApi`，只铺占位行、不复制凭据 |
| D7 | 平台下拉由 `AuthDefaultSource` 枚举驱动 |
| D8 | 常用字段建列 + `ext_config` 存平台特有字段 |

## 4. 后端设计

### 4.1 模块与依赖

新模块 `continew-plugin/continew-plugin-social`，包根 `top.continew.admin.social`，父 POM 为 `continew-plugin`（已统一声明 `continew-common` 依赖）。

P1 需要把 `continew-common/pom.xml` 中的 `continew-starter-auth-justauth` 换成 `me.zhyd.oauth:JustAuth`（版本 1.16.7，需在根 POM 声明）。

### 4.2 表结构

`sys_social_config`：`source / name / client_id / client_secret / agent_id / redirect_uri / scopes / union_id / ignore_check_state / ext_config / sort / description / status / tenant_id` + `BaseDO` 审计字段。

唯一索引 `(source, tenant_id, deleted)`。

### 4.3 类清单（P0 已完成）

~~~text
config/SocialConfiguration.java
controller/SocialConfigController.java      @CrudRequestMapping(/social/config)
service/SocialConfigService.java
service/impl/SocialConfigServiceImpl.java
mapper/SocialConfigMapper.java
model/entity/SocialConfigDO.java
model/query/SocialConfigQuery.java
model/req/SocialConfigReq.java
model/resp/SocialConfigResp.java
model/resp/SocialConfigDetailResp.java
~~~

P1 追加：`api/SocialAuthApiImpl.java`、`api/SocialDataApiImpl.java`（`TenantDataApi`）、`core/RedisAuthStateCache.java`。

### 4.4 接口

| 方法 | 路径 | 权限 |
| --- | --- | --- |
| CRUD | `/social/config` | `social:config:{list,get,create,update,delete,export}` |
| GET | `/social/config/source/list` | `social:config:list` |
| GET | `/social/common/platforms`（P1，匿名） | — |

### 4.5 权限与菜单

菜单挂在 **系统管理（1000）> 系统配置（1150）** 下，作为「系统配置」页面的一个页签（与网站/安全/登录/邮件/短信/存储/客户端配置同级）：

~~~text
1000  系统管理（目录）
└─ 1150  系统配置（菜单，system/config/index，页签容器）
   ├─ 1160 ~ 1250 网站/安全/登录/邮件/短信/存储/客户端配置
   └─ 1260  社交登录（/system/config?tab=social，component = system/config/social/index）
      ├─ 6011 social:config:list
      ├─ 6012 social:config:get
      ├─ 6013 social:config:create
      ├─ 6014 social:config:update
      ├─ 6015 social:config:delete
      └─ 6017 social:config:updateStatus
~~~

- 页签容器 `system/config/index.vue` 的 `data` 数组新增一项（key=`social`、权限 `social:config:list`），页签按权限过滤显示
- 子菜单 `is_hidden = true`（与其它配置页签一致），仅通过 `?tab=social` 进入
- changeset 5 负责迁移：插入 1260、把 6011~6017 的 `parent_id` 改为 1260、删除旧的 6000/6010

### 4.6 多租户

- 关闭多租户：拦截器不注册，INSERT 走 DB 默认 `tenant_id=0`、SELECT 不过滤 → 天然只有一套「默认租户」配置
- 开启多租户：拦截器自动按 `tenant_id` 过滤/填充，租户之间互不可见
- **新建租户**：`SocialDataApiImpl.init()` 以默认租户的平台清单为模板，在 `TenantUtils.execute(tenantId, ...)` 中铺一批**禁用状态**的占位行，`client_id` / `client_secret` 一律为空字符串（不复制任何凭据，避免凭据跨租户串用）
- **删除租户**：`clear()` 清理当前租户的全部配置（走 MyBatis-Plus 逻辑删除，`deleted = id`）
- **缓存**：当前**刻意不加缓存**。平台配置实体含解密后的 `clientSecret`，缓存它等于把密钥明文写进 Redis；而 `listEnabled()` 只是一次小表索引查询、调用频率很低，加缓存的收益不抵风险。将来若确需缓存，只能缓存 `source/name` 这类无密钥投影，且 KEY **必须拼接租户 ID**
- **套餐菜单授权**：`tenant_package_menu` 没有任何种子数据，菜单授权完全由「租户套餐管理」勾选决定。
- ⚠️ **租户可见性的两个独立机制**（容易混淆）：
  1. `ignore-menus`（yml）**只在「租户套餐管理」的菜单树里生效**（`PackageController.listMenuTree` 是唯一使用点），决定「平台管理员**能勾选**哪些菜单授予租户」；
  2. 租户实际可见的菜单 = **授予其角色（租户管理员 `admin`）的菜单集合**（`AuthServiceImpl.buildRouteTree` 按 `listByRoleId` 构建路由），来源是套餐的 `tenant_package_menu` 授权。
- **`ignore-menus` 必须按「叶子」粒度忽略，不能忽略父菜单**：实测 Hutool `TreeUtil.build` 会把「父节点不在列表中」的子节点**整棵丢弃**（不是提升为顶级）。因此原先 `- 1150 系统配置` 会让 1150 的**全部子菜单**（含社交登录）从套餐菜单树里消失，导致社交登录根本无法被勾选。
- 现在的做法：**放行 1150「系统配置」，只忽略它与租户无关的 7 个子菜单**（1160 网站 / 1170 安全 / 1180 登录 / 1190 邮件 / 1210 短信 / 1230 存储 / 1250 客户端），社交登录（1260）保持可勾选。
- ⚠️ 配置改完只是**让勾选成为可能**；**已有套餐仍需平台管理员到「租户套餐管理」补勾「系统配置 > 社交登录」，租户才能真正看到**

## 5. 前端设计

| 文件 | 说明 |
| --- | --- |
| `src/apis/social/{type,config,index}.ts` | 接口封装 |
| `src/views/system/config/social/index.vue` | 列表页（作为「系统配置」页签挂载） |
| `src/views/system/config/social/AddDrawer.vue` | 新增 / 修改（无回调地址输入项） |
| `src/views/system/config/social/DetailDrawer.vue` | 详情（回调地址标注「自动生成」） |
| 登录页 `src/views/login/index.vue`（P1） | 写死按钮 → 动态渲染 |

## 6. 分期计划

| 阶段 | 内容 | 状态 |
| --- | --- | --- |
| P0 | 模块 + 表 + CRUD + 配置页 + 菜单 | 已完成 |
| P1 | 换依赖 + 自研 state cache + 删 YAML + 改 3 个类走 `SocialAuthApi` + 登录页动态按钮 | 已完成 |
| P2 | `TenantDataApi` 初始化 + 缓存租户化 + 套餐菜单授权 | 已完成 |
| P3 | 密钥脱敏、启停开关、OpenAPI 分组（**导出不做**） | 已完成 |
| — | 新增文件：`api/SocialDataApiImpl.java`、`core/RedisAuthStateCache.java`、`controller/SocialCommonController.java`、`constant/SocialConstants.java` | |

## 7. 风险与注意

1. P1 排除 xkcoding starter 后，需实测启动无异常（其 `AutoConfiguration.imports` 中的缺失类应被 Boot 跳过）
2. `client_secret` 用 `continew-starter.encrypt.field.password`（AES）加密，换环境必须同 key，否则历史密文解不开
3. 新增菜单需要给租户套餐补授权，现有租户才能在菜单里看到
7. `ignore-menus` 只影响「套餐可勾选范围」，不影响已授权的租户菜单；两者都到位租户才可见
5. **不提供导出**：平台配置不导出（`Api.EXPORT` 未启用，`SocialConfigResp` 无 Excel 注解）。changeset 2 里历史遗留的 `6016` 导出菜单由 changeset 4 删除（**不修改已执行过的 changeset**，避免 Liquibase 校验和不匹配）
6. 启停开关走 `PUT /social/config/{id}/status`，权限点 `social:config:updateStatus`（菜单 `6017`），前端用 `a-switch` 且失败回滚
4. 若先开多租户建了多套配置、再关闭多租户，唯一索引会让同 source 多行同时可见，`getBySource` 需要显式处理
