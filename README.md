# 校园个人物品租赁与交易系统

## 项目简介

基于微信小程序的校园个人物品租赁与交易平台。

## 技术栈

- **客户端**：微信小程序
- **后台管理**：Vue 3 + TypeScript + TailwindCSS
- **后端**：Java 25 + Spring Boot 3.x + MyBatis-Plus
- **数据库**：MySQL 8.0+
- **缓存**：Redis

## 项目结构

```
LeaseTransaction/
├── backend/          # 后端项目
│   └── lease-backend/
├── admin/            # 后台管理系统
│   └── lease-admin/
├── miniprogram/      # 微信小程序
└── .trae/
    └── specs/        # 规格文档
```

## 快速开始

### 后端启动

```bash
cd backend/lease-backend
mvn spring-boot:run
```

### 后台管理启动

```bash
cd admin/lease-admin
npm install
npm run dev
```

### 小程序启动

使用微信开发者工具打开 `miniprogram` 目录

## 功能特性

- 用户认证与校园身份核验
- 物品发布与管理
- 租赁与交易双模式
- 订单管理与跟踪
- 实时消息通讯
- 信用评价体系
- 数据统计分析

## 开发进度

- [x] 项目初始化
- [x] 规格文档
- [x] 后端基础架构
- [x] 后台管理基础架构
- [x] 小程序基础架构
- [ ] 核心业务功能开发
- [ ] 系统集成测试
