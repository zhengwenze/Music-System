# MusicDreamerFrontEnd - 悦享音乐系统

一个基于 Vue 3 和 SpringCloud 微服务架构的现代化音乐平台前端应用，提供完整的音乐播放、管理和社交功能。

## 📁 项目概览

MusicDreamerFrontEnd 是一个全功能的音乐系统前端，采用现代化的 Vue 3 框架开发，与后端 SpringCloud 微服务架构无缝对接。系统支持多种用户角色（普通用户、歌手、管理员），提供丰富的音乐播放、上传、管理和社交功能。

## 🚀 主要特性

### 🎵 核心功能
- **多角色系统**：普通用户、歌手和管理员三个独立权限模块
- **音乐播放**：完整的音频播放功能，支持歌单管理
- **内容上传**：歌手可以上传和管理自己的音乐作品
- **社交功能**：用户间的互动和消息通知系统
- **数据统计**：使用 Echarts 展示音乐数据统计信息

### 🛠 技术栈
- **前端框架**: Vue 3.2+ (Composition API)
- **构建工具**: Vite 2.5
- **UI 组件库**: Element Plus 2.9
- **路由管理**: Vue Router 4.5
- **HTTP 客户端**: Axios 1.9
- **数据可视化**: Echarts 5.6
- **日期处理**: Moment.js
- **富文本解析**: Marked 和 DOMPurify
- **样式预处理器**: Sass

## 📦 项目结构

```
├── dist/              # 生产构建产物
├── public/            # 静态资源（图标、图片）
├── src/               # 源代码
│   ├── api/           # API 请求模块
│   ├── assets/        # 样式和静态资源
│   ├── layout/        # 布局组件
│   ├── router/        # 路由配置
│   ├── utils/         # 工具函数
│   └── views/         # 页面组件
│       ├── admin/     # 管理员端界面
│       ├── singer/    # 歌手端界面
│       └── user/      # 用户端界面
├── temp/              # 临时文件
├── index.html         # 入口 HTML
├── package.json       # 项目配置
├── vite.config.js     # Vite 配置
└── README.md         # 项目文档
```

## 🎮 快速开始

### 环境要求
- Node.js 14+ 和 npm/yarn
- 后端服务（SpringCloud 微服务）

### 安装和运行

1. **安装依赖**
```bash
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

3. **构建生产版本**
```bash
npm run build
```

4. **预览生产构建**
```bash
npm run serve
```

## 🔧 核心模块

### 用户端功能
- 热门推荐和音乐排行
- 个人歌单和收藏管理
- 精选歌手浏览
- 消息通知系统

### 歌手端功能
- 音乐上传和管理
- 个人作品维护
- 歌手详情展示
- 歌单和收藏功能

### 管理员端功能
- 用户和歌手管理
- 音乐库管理
- 系统数据统计
- 消息中心

## 🌐 代理配置

系统通过 Vite 代理配置与后端微服务通信，配置位于 `vite.config.js`：
- `/admin`: 管理员服务 (http://localhost:8081)
- `/mylike`: 用户收藏服务 (http://localhost:8082)
- `/login`: 登录服务 (http://localhost:8083)
- `/msg`: 消息服务 (http://localhost:8084)
- `/music`: 音乐服务 (http://localhost:8085)
- `/singer`: 歌手服务 (http://localhost:8087)
- `/songList`: 歌单服务 (http://localhost:8088)
- `/upload`: 文件上传服务 (http://localhost:8089)

## 🎨 特色功能

1. **响应式设计**：完美适配各种设备屏幕
2. **多语言支持**：内置中文国际化
3. **安全认证**：完善的用户登录和权限验证
4. **开发调试**：内置调试工具和跨域处理
5. **平滑过渡**：优雅的页面切换动画

## 📝 许可证

MIT License - 详见 LICENSE 文件

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

*Made with ❤️ using Vue 3 and Element Plus*