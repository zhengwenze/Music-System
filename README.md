# Music-System 🎵

> 一个基于 Vue 3 和 Spring Cloud 微服务架构的现代化音乐平台，提供完整的音乐播放、管理和社交功能。

[![License](https://img.shields.io/github/license/zhengwenze/Music-System)](LICENSE)

## 目录
- [简介](#简介)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [核心功能](#核心功能)
- [配置说明](#配置说明)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 简介

Music-System 是一个完整的现代化音乐平台，采用前后端分离架构，后端基于 Spring Cloud 微服务，前端基于 Vue 3。平台提供了丰富的音乐相关功能，包括音乐播放、上传、管理、歌单收藏、社交互动等，支持多角色访问控制（普通用户、歌手、管理员）。

## 功能特性

### 前端功能
- 🎨 现代化响应式界面，基于 Element Plus 组件库
- 🎵 完整的音乐播放器，支持播放、暂停、上一曲、下一曲等基本操作
- 📱 多角色访问控制，包括普通用户、歌手、管理员
- 🎧 音乐分类浏览，支持热门音乐、推荐歌手等
- 📋 歌单管理，支持创建、编辑、删除歌单
- ❤️ 音乐收藏功能，支持收藏喜欢的音乐和歌单
- 💬 消息通知系统，支持用户间的互动
- 🔧 个人中心，支持修改个人信息
- 📤 音乐上传功能（歌手角色）
- 📊 后台管理系统（管理员角色）

### 后端功能
- 🏗️ 基于 Spring Cloud 的微服务架构
- 🗄️ 使用 MySQL 存储数据
- 🚀 使用 Redis 缓存热点数据
- 🔐 完整的用户认证与授权系统
- 📡 使用 Nacos 进行服务发现
- 🌉 Spring Cloud Gateway 作为 API 网关
- 📦 MyBatis Plus 作为 ORM 框架
- 🔄 统一的异常处理和响应格式
- 📝 详细的接口文档

## 技术栈

### 后端技术
- **Java 17**：编程语言
- **Spring Boot 3.3.1**：应用框架
- **Spring Cloud 2023.0.4**：微服务框架
- **Spring Cloud Alibaba 2023.0.1.0**：微服务生态
- **MyBatis Plus 3.5.7**：ORM 框架
- **Redis**：缓存
- **MySQL 8.0.33**：数据库
- **Nacos**：服务发现
- **Spring Cloud Gateway**：API 网关
- **JWT**：身份认证

### 前端技术
- **Vue 3**：前端框架
- **Vite**：构建工具
- **Element Plus**：UI 组件库
- **Axios**：HTTP 客户端
- **ECharts**：数据可视化
- **Vue Router 4**：路由管理
- **Sass**：CSS 预处理器

## 项目结构

```
Music-System/
├── .gitignore              # Git 忽略文件
├── LICENSE                 # 许可证文件
├── README.md               # 项目说明
├── ZWZ-TEST19/             # 后端微服务代码
│   ├── Mod_admin/          # 管理模块
│   ├── Mod_like/           # 收藏模块
│   ├── Mod_login/          # 登录模块
│   ├── Mod_msg/            # 消息模块
│   ├── Mod_music/          # 音乐模块
│   ├── Mod_setting/        # 设置模块
│   ├── Mod_singer/         # 歌手模块
│   ├── Mod_songList/       # 歌单模块
│   ├── Mod_upload/         # 上传模块
│   ├── Music_gateway/       # API 网关
│   ├── MySQL_DataBase/      # 数据库脚本
│   └── pom.xml             # Maven 配置
└── ZWZ-VUE-TEST19/         # 前端 Vue 应用
    ├── public/             # 静态资源
    ├── src/                # 源代码
    │   ├── api/            # API 调用
    │   ├── assets/         # 静态资源
    │   ├── components/     # 组件
    │   ├── layout/         # 布局
    │   ├── router/         # 路由
    │   ├── utils/          # 工具类
    │   ├── views/          # 页面
    │   ├── App.vue         # 根组件
    │   └── main.js         # 入口文件
    ├── index.html          # HTML 模板
    ├── package.json        # npm 配置
    └── vite.config.js      # Vite 配置
```

## 快速开始

### 环境要求
- **后端**：JDK 17+, Maven 3.6+
- **前端**：Node.js 14+, npm 6+
- **数据库**：MySQL 8.0+
- **缓存**：Redis 6.0+
- **服务发现**：Nacos 2.0+

### 后端启动
1. **启动依赖服务**
   - 启动 MySQL 数据库
   - 启动 Redis 服务
   - 启动 Nacos 服务

2. **初始化数据库**
   - 执行 `ZWZ-TEST19/MySQL_DataBase/music_dreamer.sql` 脚本创建数据库和表结构

3. **配置修改**
   - 修改各模块的 `application.yml` 文件中的数据库连接信息和 Nacos 配置

4. **编译打包**
   ```bash
   cd ZWZ-TEST19
   mvn clean package
   ```

5. **启动服务**
   - 按顺序启动各微服务模块，建议先启动网关模块

### 前端启动
1. **安装依赖**
   ```bash
   cd ZWZ-VUE-TEST19
   npm install
   ```

2. **配置修改**
   - 修改 `vite.config.js` 中的代理配置，确保与后端服务地址匹配

3. **启动开发服务器**
   ```bash
   npm run dev
   ```

4. **构建生产版本**
   ```bash
   npm run build
   ```

## 核心功能

### 1. 用户认证与授权
- 支持用户注册、登录
- 基于 JWT 的身份认证
- 多角色权限控制（普通用户、歌手、管理员）

### 2. 音乐管理
- 音乐上传（歌手角色）
- 音乐信息编辑（歌手角色）
- 音乐审核（管理员角色）
- 音乐分类管理

### 3. 歌单管理
- 创建、编辑、删除歌单
- 向歌单添加/移除音乐
- 歌单分类
- 歌单分享

### 4. 收藏功能
- 收藏音乐
- 收藏歌单
- 查看收藏列表

### 5. 社交功能
- 消息通知系统
- 歌手关注
- 评论功能

### 6. 后台管理
- 用户管理
- 歌手管理
- 音乐管理
- 歌单管理
- 系统设置

## 配置说明

### 后端配置
每个微服务模块都有自己的 `application.yml` 配置文件，主要配置项包括：

- **服务器配置**：端口号、上下文路径等
- **数据库配置**：MySQL 连接信息
- **Redis 配置**：Redis 连接信息
- **Nacos 配置**：服务发现配置
- **JWT 配置**：密钥、过期时间等

### 前端配置
- **vite.config.js**：开发服务器配置、代理配置
- **src/api/request.js**：API 基础路径配置
- **src/router/index.js**：路由配置、权限控制

## 贡献指南

1. **Fork 本仓库**
2. **创建特性分支** (`git checkout -b feature/AmazingFeature`)
3. **提交更改** (`git commit -m 'Add some AmazingFeature'`)
4. **推送到分支** (`git push origin feature/AmazingFeature`)
5. **打开 Pull Request**

## 许可证

本项目采用 Apache License 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

---

**感谢使用 Music-System！** 🎵

如果您有任何问题或建议，欢迎提交 Issue 或 Pull Request。
