# ContiNew Admin 多租户中后台管理框架

##  系统功能

- 仪表盘：提供工作台、分析页，工作台提供功能快捷导航入口、最新公告、动态；分析页提供全面数据可视化能力
- 个人中心：支持基础信息修改、密码修改、邮箱绑定、手机号绑定（并提供行为验证码、短信限流等安全处理）、第三方账号绑定/解绑（微信登录）、头像裁剪上传
- 消息中心：提供站内信消息统一查看、标记已读、全部已读、删除等功能（目前仅支持系统通知消息）、提供个人公告查看
- 用户管理：管理系统用户，包含新增、修改、删除、导入、导出、重置密码、分配角色等功能
- 角色管理：管理系统用户的功能权限及数据权限，包含新增、修改、删除、分配角色等功能
- 菜单管理：管理系统菜单及按钮权限，支持多级菜单，动态路由，包含新增、修改、删除等功能
- 部门管理：管理系统组织架构，包含新增、修改、删除、导出等功能，以树形列表进行展示
- 通知公告：管理系统公告，支持通知范围（所有人、指定用户）、通知方式（系统消息、登录弹窗）、定时发送、置顶设置
- 文件管理：管理系统文件及文件夹，支持回收站、上传/分片上传、下载、预览（目前支持图片、音视频、PDF、Word、Excel、PPT）、重命名、切换视图（列表、网格）等功能
- 字典管理：管理系统公用数据字典，例如：消息类型。支持字典标签背景色和排序等配置
- 系统配置：
  - 网站配置：提供修改系统标题、Logo、favicon、版权信息等基础配置功能，以方便用户系统与其自身品牌形象保持一致
  - 安全配置：提供密码策略修改，支持丰富的密码策略设定，包括但不限于 `密码有效期`、`密码重复次数`、`密码错误锁定账号次数、时间` 等
  - 登录配置：提供验证码开关等登录相关配置
  - 邮件配置：提供系统发件箱配置，也支持通过配置文件指定
  - 短信配置：提供系统短信服务配置，也支持通过配置文件指定
  - 存储配置：管理文件存储配置，支持本地存储、兼容 S3 协议对象存储
  - 客户端配置：多端（PC端、小程序端等）认证管理，可设置不同的 token 有效期
- 在线用户：管理当前登录用户，可一键踢除下线
- 日志管理：管理系统登录日志、操作日志，支持查看日志详情，包含请求头、响应头等报文信息
- 短信日志：管理系统短信发送日志，支持删除、导出 
- 应用管理：管理第三方系统应用 AK、SK，包含新增、修改、删除、查看密钥、重置密钥等功能，支持设置密钥有效期
- 租户管理：管理租户信息，包含新增、修改、删除、分配角色等功能
- 租户套餐：管理租户套餐信息，包含新增、修改、删除、查看等功能
- 任务管理：管理系统定时任务，包含新增、修改、删除、执行功能，支持 Cron（可配置式生成 Cron 表达式） 和固定频率
- 任务日志：管理定时任务执行日志，包含停止、重试指定批次等功能
- 代码生成：提供根据数据库表自动生成相应的前后端 CRUD 代码的功能，支持同步最新表结构及代码生成预览

## 项目结构

```
continew-admin
├─ continew-server（打包部署模块）
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java/top/continew/admin
│  │  │  │  ├─ config （配置）
│  │  │  │  │  ├─ log（操作日志配置）
│  │  │  │  │  └─ satoken（SaToken 认证配置）
│  │  │  │  ├─ controller（通用 API）
│  │  │  │  ├─ job （定时任务）
│  │  │  │  └─ ContiNewAdminApplication.java（ContiNew Admin 启动程序）
│  │  │  └─ resources
│  │  │     ├─ config（核心配置目录）
│  │  │     │  ├─ application-dev.yml（开发环境配置文件）
│  │  │     │  ├─ application-prod.yml（生产环境配置文件）
│  │  │     │  └─ application.yml（通用配置文件）
│  │  │     ├─ db/changelog（Liquibase 数据脚本配置目录）
│  │  │     │  ├─ mysql（MySQL 数据库初始 SQL 脚本目录）
│  │  │     │  ├─ postgresql（PostgreSQL 数据库初始 SQL 脚本目录）
│  │  │     │  └─ db.changelog-master.yaml（Liquibase 变更记录文件）
│  │  │     ├─ templates（模板配置目录，例如：邮件模板）
│  │  │     ├─ banner.txt（Banner 配置文件）
│  │  │     └─ logback-spring.xml（日志配置文件）
│  │  └─ test（测试相关代码目录）
│  └─ pom.xml（包含打包相关配置）
├─ continew-system（系统管理模块，存放系统管理相关业务功能，例如：部门管理、角色管理、用户管理等）
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java/top/continew/admin
│  │  │  │  ├─ auth（系统认证相关业务）
│  │  │  │  │  ├─ controller（系统认证相关 API）
│  │  │  │  │  ├─ service（系统认证相关业务接口及实现类）
│  │  │  │  │  ├─ model（系统认证相关模型）
│  │  │  │  │  │  ├─ query（系统认证相关查询条件）
│  │  │  │  │  │  ├─ req（系统认证相关请求参数（Request））
│  │  │  │  │  │  └─ resp（系统认证相关响应参数（Response））
│  │  │  │  │  ├─ enums（系统认证相关枚举）
│  │  │  │  │  ├─ constant（系统认证相关常量）
│  │  │  │  │  ├─ handler（系统认证相关处理器）
│  │  │  │  │  └─ config（系统认证相关配置）
│  │  │  │  └─ system（系统管理相关业务）
│  │  │  │     ├─ api（系统管理相关公共业务 API 实现）
│  │  │  │     ├─ controller（系统管理相关 API）
│  │  │  │     ├─ service（系统管理相关业务接口及实现类）
│  │  │  │     ├─ mapper（系统管理相关 Mapper）
│  │  │  │     ├─ model（系统管理相关模型）
│  │  │  │     │  ├─ entity（系统管理相关实体）
│  │  │  │     │  ├─ query（系统管理相关查询条件）
│  │  │  │     │  ├─ req（系统管理相关请求参数（Request））
│  │  │  │     │  └─ resp（系统管理相关响应参数（Response））
│  │  │  │     ├─ enums（系统管理相关枚举）
│  │  │  │     ├─ constant（系统管理相关常量）
│  │  │  │     ├─ util（系统管理相关工具类）
│  │  │  │     ├─ validation（系统管理相关参数校验工具类）
│  │  │  │     ├─ container（系统管理相关 Crane4j 数据填充容器配置）
│  │  │  │     └─ config（系统管理相关配置）
│  │  │  └─ resources
│  │  │     └─ mapper（系统管理相关 Mapper XML 文件目录）
│  │  └─ test（测试相关代码目录）
│  └─ pom.xml
├─ continew-plugin（插件模块，存放能力开放、租户等扩展模块，后续会进行插件化改造）
│  ├─ continew-plugin-open（能力开放插件模块）
│  │  ├─ src
│  │  │  ├─ main/java/top/continew/admin/open
│  │  │  │  ├─ controller（能力开放相关 API）
│  │  │  │  ├─ service（能力开放相关业务接口及实现类）
│  │  │  │  ├─ mapper（能力开放相关 Mapper）
│  │  │  │  ├─ model（能力开放相关模型）
│  │  │  │  │  ├─ entity（能力开放相关实体）
│  │  │  │  │  ├─ query（能力开放相关查询条件）
│  │  │  │  │  ├─ req（能力开放相关请求参数（Request））
│  │  │  │  │  └─ resp（能力开放相关响应参数（Response））
│  │  │  │  ├─ util（能力开放相关工具类）
│  │  │  │  ├─ handler（能力开放相关处理器）
│  │  │  │  ├─ sign（能力开放相关 API 参数签名算法）
│  │  │  │  └─ config（能力开放相关配置）
│  │  │  └─ test（测试相关代码目录）
│  │  └─ pom.xml
│  ├─ continew-plugin-tenant（租户插件模块）
│  │  ├─ src
│  │  │  ├─ main/java/top/continew/admin/tenant
│  │  │  │  ├─ api（租户相关公共业务 API 实现）
│  │  │  │  ├─ controller（租户相关 API）
│  │  │  │  ├─ service（租户相关业务接口及实现类）
│  │  │  │  ├─ mapper（租户相关 Mapper）
│  │  │  │  ├─ model（租户相关模型）
│  │  │  │  │  ├─ entity（租户相关实体）
│  │  │  │  │  ├─ query（租户相关查询条件）
│  │  │  │  │  ├─ req（租户相关请求参数（Request））
│  │  │  │  │  └─ resp（租户相关响应参数（Response））
│  │  │  │  ├─ enums（租户相关枚举）
│  │  │  │  ├─ constant（租户相关常量类）
│  │  │  │  ├─ util（租户相关工具类）
│  │  │  │  └─ config（租户相关配置）
│  │  │  └─ test（测试相关代码目录）
│  │  └─ pom.xml
│  ├─ continew-plugin-schedule（任务调度插件模块）
│  │  ├─ src
│  │  │  ├─ main/java/top/continew/admin/schedule
│  │  │  │  ├─ controller（任务调度相关 API）
│  │  │  │  ├─ service（代码生成器相关业务接口及实现类）
│  │  │  │  ├─ api（任务调度中心相关 Feign API）
│  │  │  │  ├─ model（任务调度相关模型）
│  │  │  │  │  ├─ query（任务调度相关查询条件）
│  │  │  │  │  ├─ req（任务调度相关请求参数（Request））
│  │  │  │  │  └─ resp（任务调度相关响应参数（Response））
│  │  │  │  ├─ enums（任务调度相关枚举）
│  │  │  │  ├─ constant（任务调度相关常量类）
│  │  │  │  ├─ exception（任务调度相关异常）
│  │  │  │  ├─ annotation（任务调度相关注解）
│  │  │  │  └─ config（任务调度相关配置）
│  │  │  └─ test（测试相关代码目录）
│  │  └─ pom.xml
│  ├─ continew-plugin-generator（代码生成器插件模块）
│  │  ├─ src
│  │  │  ├─ main
│  │  │  │  ├─ java/top/continew/admin/generator
│  │  │  │  │  ├─ controller（代码生成器相关 API）
│  │  │  │  │  ├─ service（代码生成器相关业务接口及实现类）
│  │  │  │  │  ├─ mapper（代码生成器相关 Mapper）
│  │  │  │  │  ├─ model（代码生成器相关模型）
│  │  │  │  │  │  ├─ entity（代码生成器相关实体）
│  │  │  │  │  │  ├─ query（代码生成器相关查询条件）
│  │  │  │  │  │  ├─ req（代码生成器相关请求参数（Request））
│  │  │  │  │  │  └─ resp（代码生成器相关响应参数（Response））
│  │  │  │  │  ├─ enums（代码生成器相关枚举）
│  │  │  │  │  └─ config（代码生成器相关配置）
│  │  │  │  └─ resources
│  │  │  │     └─ templates（代码生成相关模板目录）
│  │  │  │       ├─ backend（后端模板目录）
│  │  │  │       └─ frontend（前端模板目录）
│  │  │  └─ test（测试相关代码目录）
│  │  └─ pom.xml
│  └─ pom.xml
├─ continew-common（公共模块，存放公共工具类，公共配置等）
│  ├─ src
│  │  ├─ main/java/top/continew/admin/common
│  │  │  ├─ api（公共业务 API）
│  │  │  ├─ base（公共基类）
│  │  │  │  ├─ controller（控制器基类）
│  │  │  │  ├─ mapper（Mapper 接口基类）
│  │  │  │  ├─ model（公共模型）
│  │  │  │  │  ├─ entity（实体基类）
│  │  │  │  │  └─ resp（列表、详情响应基类）
│  │  │  │  └─ service（业务接口及实现基类）
│  │  │  ├─ model（公共模型）
│  │  │  │  ├─ dto（公共数据传输对象（DTO））
│  │  │  │  └─ req（公共请求参数（Request））
│  │  │  ├─ context（公共上下文）
│  │  │  ├─ enums（公共枚举）
│  │  │  ├─ constant（公共常量类）
│  │  │  ├─ util（公共工具类）
│  │  │  └─ config（公共配置）
│  │  │    ├─ crud（CRUD 配置）
│  │  │    ├─ mybatis（MyBatis Plus 配置）
│  │  │    ├─ websocket（Websocket 配置）
│  │  │    ├─ doc（接口文档配置）
│  │  │    ├─ excel（Excel 配置）
│  │  │    └─ exception（全局异常处理）
│  │  └─ test（测试相关代码目录）
│  └─ pom.xml
├─ continew-extension（扩展模块）
│  ├─ continew-extension-schedule-server（任务调度服务端模块，实际开发时如果是公司统一提供环境，可直接删除本模块）
│  │  ├─ src
│  │  │  ├─ main
│  │  │  │  ├─ java/top/continew/admin/extension/schedule
│  │  │  │  │  └─ ScheduleServerApplication.java（任务调度服务端启动程序）
│  │  │  │  └─ resources
│  │  │  │     ├─ config（核心配置目录）
│  │  │  │     │  ├─ application-dev.yml（开发环境配置文件）
│  │  │  │     │  ├─ application-prod.yml（生产环境配置文件）
│  │  │  │     │  └─ application.yml（通用配置文件）
│  │  │  │     ├─ db/changelog（Liquibase 数据脚本配置目录）
│  │  │  │     │  ├─ mysql（MySQL 数据库初始 SQL 脚本目录）
│  │  │  │     │  ├─ postgresql（PostgreSQL 数据库初始 SQL 脚本目录）
│  │  │  │     │  └─ db.changelog-master.yaml（Liquibase 变更记录文件）
│  │  │  │     └─ logback-spring.xml（日志配置文件）
│  │  │  └─ test（测试相关代码目录）
│  │  └─ pom.xml
│  └─ pom.xml
├─ .github（GitHub 相关配置目录，实际开发时直接删除）
├─ .idea
│  └─ icon.png（IDEA 项目图标，实际开发时直接删除）
├─ .image（截图目录，实际开发时直接删除）
├─ .style（代码格式、License文件头相关配置目录，实际开发时根据需要取舍，删除时注意删除 /pom.xml 中的 spotless 插件配置）
├─ .gitignore（Git 忽略文件相关配置文件）
├─ docker（项目部署相关配置目录，实际开发时可备份后直接删除）
├─ LICENSE（开源协议文件）
├─ CHANGELOG.md（更新日志文件，实际开发时直接删除）
├─ README.md（项目 README 文件，实际开发时替换为真实内容）
├─ lombok.config（Lombok 全局配置文件）
└─ pom.xml（包含版本锁定及全局插件相关配置）
```