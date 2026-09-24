# AGENTS.md — ContiNew Admin（后端）

本文件面向在本仓库工作的编码 Agent，作用域是当前仓库（continew-admin）。
外层聚合工作区的说明见 [../AGENTS.md](../AGENTS.md)。

## 一句话概览

ContiNew Admin 是一个多租户中后台管理框架：Spring Boot 3 + Maven 多模块，构建在自研脚手架 **ContiNew Starter 2.14.0**（top.continew.starter，通过父 POM 引入）之上。大量 CRUD 能力由 starter 的 CRUD 抽象直接提供。

- 版本：`revision = 4.1.0`；包根：`top.continew.admin`
- 语言/构建：Java 17、Maven 3.9.16（仓库自带 Maven Wrapper，无需系统安装 Maven；CI 用 JDK 17）
- 数据库：PostgreSQL（dev 默认）/ MySQL，版本管理用 Liquibase
- Redis：Sa-Token 会话（dao.type=REDIS）、JetCache、Redisson、CosId 机器号
- 认证授权：Sa-Token + JWT；多租户隔离由 starter 插件自动完成

## 常用命令

在仓库根目录执行。用仓库自带的 Maven Wrapper：Linux/macOS 用 `./mvnw`，Windows 用 `mvnw.cmd`；首次执行会自动下载 Maven 3.9.16，**仍需本机有 JDK 17**。

~~~bash
./mvnw -B compile                          # 编译全部模块（compile 阶段会自动跑 Spotless 格式化）
./mvnw -B -pl continew-system -am compile  # 只编译某模块及其依赖
./mvnw -B clean package                    # 打包，默认 slim_jar（可执行 JAR + lib + config）
./mvnw -B clean package -P fat_jar         # 胖包
./mvnw -B spring-boot:run -pl continew-server   # 启动后端（默认 dev profile）
./mvnw -B spotless:apply                   # 手动格式化
~~~

- 也可直接运行启动类 `top.continew.admin.ContiNewAdminApplication`（在 continew-server）。
- 启动后：`http://localhost:8000`，Knife4j 文档 `/doc.html`，Swagger UI `/swagger-ui`。
- 启动依赖 PostgreSQL（默认 `127.0.0.1:5432/continew_admin`）与 Redis（`127.0.0.1:6379`），见 `continew-server/src/main/resources/config/application-dev.yml`；`docker/docker-compose.yml` 可用于起依赖。
- 单元测试默认被跳过（根 `pom.xml` 里 surefire `skip=true`），CI 也不跑测试。不要指望用测试兜底，改动请自测。

## 模块划分与依赖方向

| 模块 | 职责 |
| --- | --- |
| continew-common | 公共底座：BaseController / BaseService / BaseServiceImpl / BaseDO / BaseResp、跨模块 API 接口、常量、枚举、上下文、全局异常 |
| continew-system | 系统管理业务：auth（登录认证）、system（用户/角色/菜单/部门/字典/文件/公告/配置/日志/消息等） |
| continew-plugin-open | 能力开放插件，包 top.continew.admin.open |
| continew-plugin-tenant | 租户插件，包 top.continew.admin.tenant |
| continew-plugin-schedule | 任务调度插件，包 top.continew.admin.schedule |
| continew-plugin-generator | 代码生成器插件，包 top.continew.admin.generator，含前后端 FreeMarker 模板 |
| continew-extension/continew-extension-schedule-server | Snail Job 调度服务端，独立 Spring Boot 应用，包 top.continew.admin.extension.scheduling |
| continew-server | 启动与打包：启动类、resources/config/*.yml、Liquibase 脚本、邮件模板 |

依赖方向：`continew-server` -> 各业务模块（system + 4 个 plugin）-> `continew-common`，common 不反向依赖业务模块。
**跨模块调用**要走 `continew-common/src/main/java/top/continew/admin/common/api/**` 里声明的接口，由业务模块中的 `*ApiImpl`（`@Service`）实现，以此来避免模块间循环依赖。

## 分层与命名（新增功能照此办理）

标准 CRUD 一组文件：

~~~text
controller/XxxController.java           @RestController + @CrudRequestMapping
service/XxxService.java                 extends BaseService<...>
service/impl/XxxServiceImpl.java        extends BaseServiceImpl<...>
mapper/XxxMapper.java                   extends BaseMapper<XxxDO> / DataPermissionMapper<XxxDO>
model/entity/XxxDO.java                 extends BaseDO
model/query/XxxQuery.java               查询条件
model/req/XxxReq.java                   创建/修改请求参数
model/resp/XxxResp.java                 列表响应（extends BaseResp）
model/resp/xxx/XxxDetailResp.java       详情响应（extends BaseDetailResp）
src/main/resources/mapper/**/XxxMapper.xml   复杂 SQL
~~~

- 命名固定：实体 `XxxDO`、列表 `XxxResp`、详情 `XxxDetailResp`、查询 `XxxQuery`、请求 `XxxReq`；实体用 `@TableName("表名")`。
- CRUD 由 starter 提供：Controller 继承 `BaseController` 并声明 `@CrudRequestMapping(value = "/system/user", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.BATCH_DELETE, Api.EXPORT, Api.DICT})`，给出 S/L/D/Q/C 五个泛型即可，方法体可以为空（参考 UserController）。
- 复杂查询写 Mapper XML（`mapper-locations: classpath*:/mapper/**/*Mapper.xml`），简单查询可用 `@Select` 注解。
- 权限：`@CrudRequestMapping` 会按接口路径自动校验「模块:资源:动作」；自定义接口显式加 `@SaCheckPermission("system:user:import")`。
- 参数校验：Jakarta Validation（`@Valid` + `@NotBlank`/`@NotNull` 等）；分组校验用 `CrudValidationGroup` 或本仓库的 `validation/ValidationGroup`；业务前置校验用 `ValidationUtils.throwIfXxx(...)`。
- 接口文档：Controller 加 `@Tag`，方法加 `@Operation`，DTO 字段加 `@Schema(description = ..., example = ...)`。
- 通用实体字段：`BaseDO` 提供 `id/createUser/createTime/updateUser/updateTime/deleted`（逻辑删除），由 `MyBatisPlusMetaObjectHandler` 自动填充。
- 敏感字段：入库加密用 `@FieldEncrypt`，响应脱敏用 `@JsonMask(MaskType.XXX)`。
- 数据权限：Mapper 方法加 `@DataPermission(tableAlias = "t1")`。
- 关联数据回填：Crane4j（`@Assemble` + `ContainerConstants`，如 createUserString / roleNames）。
- 缓存：JetCache `@Cached`。
- Lombok：`@Data`/`@RequiredArgsConstructor`/`@Slf4j`；`lombok.config` 禁用了 `val` 与 accessors。
- 每个 Java 文件保留 Apache License 头（模板在 `.style/license-header`）。
- 格式化：Spotless 使用 P3C 规范（`.style/p3c-codestyle.xml`），绑定在 `compile` 阶段自动 `apply`。
  ⚠️ 注意：当前仓库里几乎所有 Java 文件的 license 头缩进都与 Spotless 的期望不一致（`spotless:check` 会报 260+ 个文件违规），所以一次普通的 `compile` 会把大批文件的 license 头改写、把工作区弄脏。只是验证编译时请加 `-Dspotless.apply.skip=true`；确实要统一格式时再单独跑 `./mvnw spotless:apply` 并用 git 确认改动范围。

## 统一响应与异常

- 响应体统一是 `top.continew.starter.web.model.R`（`code/success/msg/data/timestamp`）。Controller 直接返回业务对象或 void，不要自己包 `R`。
- 成功码 `0`、失败码 `1`，失败默认 HTTP 200（见 `application.yml` 的 `continew-starter.web.response`）。
- 业务异常抛 `BusinessException` / `BadRequestException`（`top.continew.starter.core.exception`），由 `common/config/exception/GlobalExceptionHandler` 统一处理。

## 数据库变更（务必走 Liquibase）

- 不要手改数据库；表结构与初始数据都通过 Liquibase 变更：
  - 脚本目录：`continew-server/src/main/resources/db/changelog/{postgresql,mysql}/`（含 `plugin/` 子目录）
  - 变更清单：`db.changelog-master.yaml`，新增 changeset 后要在其中 include 对应文件
- dev 默认启用 PostgreSQL（`application.yml` 中 `mybatis-plus.configuration.database-id: pgsql`），master 里 MySQL 的 include 段是注释状态。改脚本时通常需要同时维护 postgresql 与 mysql 两套。
- 主键策略 `ASSIGN_ID`（CosId）；逻辑删除字段 `deleted`（未删除为 0，已删除存 id）。
- 租户判别列 `tenant_id` 的加列与索引同样写在 changelog 里，做法见下方「多租户」。

## 多租户

采用 **共享库共享表 + `tenant_id` 判别列**（`isolation-level: LINE`）。底座在 starter（`continew-starter-extension-tenant-core` 提供上下文/拦截器/注解，`-tenant-mp` 提供 MyBatis-Plus 改写），业务侧在 `continew-plugin-tenant`。

**租户上下文怎么来**

- Web 请求：`TenantInterceptor` 读请求头 `X-Tenant-Id` -> `TenantProvider.getByTenantId(id, verify=true)` -> 写入 `TenantContextHolder`，请求结束清理。
- 本仓库的 Provider 是 `DefaultTenantProvider`：ID 为默认租户 `0` 直接放行；请求头为空时回退读 `X-Tenant-Code`（登录前场景），查不到报错；指定 ID 时 `TenantService.checkStatus()` 校验租户未禁用/未过期/套餐未禁用。
- 非 Web 线程（定时任务、线程池）没有 Web 上下文：用 `@TenantIgnore`（AOP `TenantIgnoreAspect` 识别方法注解）或 `TenantUtils.execute(tenantId, runnable)` 手工压入。`AbstractLoginHandler` 在线程池里就是显式 `TenantUtils.execute`。
- Controller 上的 `@TenantIgnore` 由 Web 拦截器识别，普通 Bean 方法上的由 AOP 识别，两条路径不要混。

**SQL 怎么改写**

- starter 注册 MyBatis-Plus `TenantLineInnerInterceptor` + `DefaultTenantLineHandler`：列名 `tenant_id`，SELECT 自动加 `WHERE tenant_id = ?`，INSERT 自动补列。
- `ignore-tables` 里的表是全局共享表，不拼租户条件：`tenant`、`tenant_package`、`tenant_package_menu`、`gen_config`、`gen_field_config`、`sys_menu`、`sys_dict`、`sys_dict_item`、`sys_option`、`sys_storage`、`sys_sms_config`、`sys_sms_log`、`sys_client`、`sys_app`。
- 租户私有表在 `plugin_tenant.sql` 里逐个 `ADD COLUMN tenant_id int8 NOT NULL DEFAULT 0` 并建索引（部门/角色/用户及关联表、日志、消息、通知、文件等）。
- **取不到租户上下文时 handler 返回 `NullValue`，SQL 变成 `tenant_id = NULL`（查不到数据）而不是查全部**——异步场景漏了租户上下文会静默查空。
- 业务实体全部继承 `BaseDO`，**没有任何实体继承 `TenantBaseDO`**（该类目前是死代码；`tenant_id` 完全由拦截器读写，实体不需要声明字段）。

**登录与越权**

- 登录前：`/tenant/common/info`（匿名）一次返回「是否开启租户 + 可用租户列表（仅 id/name，且仅在开启租户时返回）」；`/tenant/common/id?domain=` 按域名查租户 ID。
- 登录时把 `TenantContextHolder.getTenantId()` 写进 `UserContext.tenantId`，`LoginResp.tenantId` 回传前端。
- 登录后：`SaExtensionInterceptor` 比对会话里的 `userContext.tenantId` 与请求解析出的租户 ID，不一致返回 403「您当前没有访问该租户的权限」。

**租户生命周期**

- 注册表 `tenant`（name/code/domain/expire_time/status/admin_user/admin_username/package_id），code 由 CosId 的 `tenant-code` 生成器生成。
- 套餐 `tenant_package` + `tenant_package_menu` 定义该租户可用菜单；套餐禁用会导致租户校验失败。
- 创建租户：`TenantServiceImpl.create()` 遍历容器里所有 `TenantDataApi` 实现逐个 `init()`；系统模块的实现是 `TenantDataApiForSystemImpl`（在 `TenantUtils.execute(tenantId, ...)` 里初始化部门、`admin` 租户管理员角色、按套餐绑定菜单、管理员用户，并回填 `tenant.admin_user`）。**新增模块要初始化租户数据，实现 `TenantDataApi` 即可被自动编排。**
- 删除租户：逐租户在 `TenantUtils.execute(id, ...)` 下调用各模块 `clear()`。
- 菜单隔离：`ignore-menus` 里的菜单租户不可见，`MenuServiceImpl.tree()` 会注入排除列表。
- 角色：默认租户只认 `super_admin`，普通租户认 `super_admin` + `admin`（`RoleCodeEnum.getSuperRoleCodes()`）。

**改动注意事项**

- 新增租户私有表：加 `tenant_id int8 NOT NULL DEFAULT 0` + 索引，且**不要**加入 `ignore-tables`；全局共享表相反。
- 异步/定时任务必须显式处理租户（`@TenantIgnore` 或 `TenantUtils.execute`），否则拼出 `tenant_id = NULL` 查空。
- 前端所有请求由 http 拦截器带 `X-Tenant-Id`；租户 ID 最终以 token 里的用户上下文为准，改请求头越不过 `SaExtensionInterceptor`。
- 社交登录插件（`continew-plugin-social`）用 `SocialDataApiImpl` 实现租户初始化：新建租户时以默认租户的平台清单为模板铺禁用占位行、不复制凭据；删除租户时 `clear()` 清理。它的配置实体含解密后的 `clientSecret`，**不要为它加缓存**（会把密钥明文写进 Redis）；如确需缓存，只能缓存无密钥投影且 KEY 必须带租户 ID。

## 新增业务的推荐路径

1. 先看 `continew-plugin-generator/src/main/resources/templates/{backend,frontend}` 里的 FreeMarker 模板——那就是本项目的标准写法。
2. 再对照 `continew-system` 中已有的同类功能（用户 / 角色 / 字典等）。
3. 也可以直接用系统里的「代码生成」功能按数据库表生成前后端 CRUD 骨架，再补业务逻辑。

## 提交前自检

- `./mvnw -B compile` 通过（编译即会格式化）。
- 新增 Java 文件带 License 头，Controller/DTO 带 `@Tag`/`@Operation`/`@Schema`。
- 涉及表结构的改动已补 Liquibase changeset（两套数据库）。
- 接口行为请启动服务用 Knife4j 自测，不要依赖默认被跳过的单元测试。
