# 校园个人物品租赁与交易系统

基于微信小程序、Vue 3 管理后台和 Spring Boot 的校园个人物品租赁与交易平台。

## 技术栈

- 客户端：微信小程序
- 后台管理：Vue 3 + TypeScript + Element Plus + TailwindCSS
- 后端：Java 21 + Spring Boot 3.1 + MyBatis-Plus + Flyway
- 数据库与缓存：MySQL 8.0 + Redis 7
- 支付：微信支付 API v3 Java SDK
- 部署：Docker Compose + Nginx

## 项目结构

```text
LeaseTransaction/
├── backend/          # Spring Boot 后端
├── admin/            # Vue 管理后台
├── miniprogram/      # 微信小程序
├── docker/           # 数据库等辅助资源
├── docker-compose.yml
└── .env.example
```

## 本地启动

先准备 JDK 21、Maven、Node.js 22、Docker Desktop。

```powershell
copy .env.example .env
powershell -ExecutionPolicy Bypass -File .\start-dev.ps1
```

也可以分开启动：

```powershell
docker compose up -d mysql redis
.\run-backend.ps1
cd admin
npm install
npm run dev
```

默认访问：

- 后端 API: http://127.0.0.1:8081/api
- Swagger: http://127.0.0.1:8081/swagger-ui.html
- 管理端 dev: http://127.0.0.1:5173
- Docker 管理端: http://127.0.0.1:8080

## Docker Compose

```powershell
copy .env.example .env
docker compose up --build
```

Compose 服务名：

- `lease-mysql`
- `lease-redis`
- `lease-backend`
- `lease-admin-nginx`

MySQL 表结构由后端启动时的 Flyway migration 初始化，不再依赖 Docker init SQL。

## 演示模式

本地和测试 profile 使用本地支付网关。小程序拿到 `LOCAL_PAY_SIGN_*` 后会弹出本地支付确认框，并调用 `POST /api/payment/local/confirm/{paymentNo}` 完成订单支付状态流转；该接口在 `prod` profile 下不会注册。

小程序首页和消息页的演示数据仅在非 release 环境启用。release 环境需要配置真实 `apiBaseUrl` 和 `wsUrl`，并确保微信支付商户配置完整。

统一验收脚本：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-dev.ps1
```

如果只验证后端测试和管理端构建、暂不构建镜像：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-dev.ps1 -SkipDockerBuild
```

## 必要环境变量

`.env.example` 列出了完整配置。生产环境至少要替换：

- `SPRING_PROFILES_ACTIVE=prod`
- `LEASE_DB_PASSWORD`
- `JWT_SECRET`
- `ADMIN_BOOTSTRAP_PASSWORD`
- `FILE_PUBLIC_BASE_URL`
- `WECHAT_PAY_APP_ID`
- `WECHAT_PAY_MCH_ID`
- `WECHAT_PAY_MCH_SERIAL_NO`
- `WECHAT_PAY_PRIVATE_KEY_PATH`
- `WECHAT_PAY_API_V3_KEY`
- `WECHAT_PAY_NOTIFY_URL`

微信支付私钥/证书放在本机或服务器 `secrets/` 目录，不提交仓库。

## 鉴权说明

- 用户端登录：`POST /api/user/login`
- 后台登录：`POST /api/admin/auth/login`
- 公开接口：物品列表/详情/附近推荐、公开用户主页、用户评价、支付回调、Swagger
- 用户接口需要 `CLIENT` token
- 后台接口需要 `ADMIN` token
- 系统用户管理需要 `SUPER_ADMIN`

未登录返回业务码 `401`，权限不足返回业务码 `403`。

## 测试与构建

```powershell
cd backend
mvn clean test

cd ../admin
npm install
npm run build
```

后端必须使用 JDK 21；JDK 17 会被 Maven Enforcer 拦截。
本机 JDK 不是 21 时，优先使用 `scripts/verify-dev.ps1` 通过 Docker Maven 镜像验证。
