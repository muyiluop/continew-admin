# AGENTS.md — ContiNew Admin（后端）

本文件面向在本仓库工作的编码 Agent，作用域是当前仓库（continew-admin）。
外层聚合工作区的说明见 [../AGENTS.md](../AGENTS.md)。

## 一句话概览

ContiNew Admin 是一个多租户中后台管理框架：Spring Boot 3 + Maven 多模块，构建在自研脚手架 **ContiNew Starter 2.14.0**（top.continew.starter，通过父 POM 引入）之上。大量 CRUD 能力由 starter 的 CRUD 抽象直接提供。

- 版本：`revision = 4.1.0`；包根：`top.continew.admin`
- 语言/构建：Java 17、Maven（CI 用 JDK 17，仅执行 `mvn -B compile`）
- 数据库：PostgreSQL（dev 默认）/ MySQL，版本管理用 Liquibase
- Redis：Sa-Token 会话（dao.type=REDIS）、JetCache、Redisson、CosId 机器号
- 认证授权：Sa-Token + JWT；多租户隔离由 starter 插件自动完成

## 常用命令

在仓库根目录执行：

~~~bash
mvn -B compile                          # 编译全部模块（compile 阶段会自动跑 Spotless 格式化）
mvn -B -pl continew-system -am compile  # 只编译某模块及其依赖
mvn -B clean package                    # 打包，默认 slim_jar（可执行 JAR + lib + config）
mvn -B clean package -P fat_jar         # 胖包
mvn -B spring-boot:run -pl continew-server   # 启动后端（默认 dev profile）
mvn -B spotless:apply                   # 手动格式化
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
- 格式化：Spotless 使用 P3C 规范（`.style/p3c-codestyle.xml`），绑定在 `compile` 阶段自动 `apply`，一般不用手动执行。

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
- 多租户：starter 会默认给表拼租户条件。需要豁免的表/菜单，登记到 `application.yml` 的 `continew-starter.tenant.ignore-tables` / `ignore-menus`。

## 新增业务的推荐路径

1. 先看 `continew-plugin-generator/src/main/resources/templates/{backend,frontend}` 里的 FreeMarker 模板——那就是本项目的标准写法。
2. 再对照 `continew-system` 中已有的同类功能（用户 / 角色 / 字典等）。
3. 也可以直接用系统里的「代码生成」功能按数据库表生成前后端 CRUD 骨架，再补业务逻辑。

## 提交前自检

- `mvn -B compile` 通过（编译即会格式化）。
- 新增 Java 文件带 License 头，Controller/DTO 带 `@Tag`/`@Operation`/`@Schema`。
- 涉及表结构的改动已补 Liquibase changeset（两套数据库）。
- 接口行为请启动服务用 Knife4j 自测，不要依赖默认被跳过的单元测试。
