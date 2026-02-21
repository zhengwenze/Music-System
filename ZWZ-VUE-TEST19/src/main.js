import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import './assets/css/global.css'
import { debugAuthInfo } from './utils/auth'
import tokenDebug from './utils/token-debug'
import { registerCorsInterceptor } from './utils/cors-interceptor' // 导入跨域请求拦截器
// main是一个Vue.js的入口文件，它创建并配置了一个Vue应用。
// 创建 Vue 应用实例，传入根组件 App
const app = createApp(App)
// 遍历 Element Plus 的所有图标组件，将它们注册为全局组件
// 这样在应用的任何地方都可以直接使用这些图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  // 将每个图标组件注册为全局组件，key 是图标名称，component 是图标组件
  app.component(key, component)
}
// 调用调试函数，检查当前的认证状态信息（主要用于开发环境）
debugAuthInfo();
// 调用 token 调试工具，修复可能的 token 问题（如过期、格式错误等）
tokenDebug.fixTokenIssues();
// 注册跨域请求拦截器，该拦截器会修改请求头等信息以解决跨域访问问题
// 通常在开发环境中用于处理前端与不同域后端 API 的通信
registerCorsInterceptor();
// 将路由插件安装到 Vue 应用中，启用路由功能
app.use(router)
// 将 Element Plus UI 组件库安装到 Vue 应用中
// 同时传入配置对象，设置语言为中文，组件尺寸为默认大小
app.use(ElementPlus, {
  locale: zhCn, // 设置 Element Plus 组件显示语言为中文
  size: 'default' // 设置组件默认尺寸
})
// 将 Vue 应用挂载到 HTML 页面中 id 为 'app' 的 DOM 元素上
// 这是启动 Vue 应用的最后一个步骤，之后应用将开始渲染
app.mount('#app')