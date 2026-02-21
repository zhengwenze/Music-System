import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  // 构建优化配置
  build: {
    // 启用 gzip 压缩
    compress: true,
    // 代码分割
    rollupOptions: {
      output: {
        manualChunks: {
          // 第三方库单独打包
          'vendor': ['vue', 'vue-router'],
          'element-plus': ['element-plus'],
          'echarts': ['echarts'],
          'axios': ['axios'],
          'utils': ['dompurify', 'marked', 'moment']
        }
      }
    },
    // 禁用生产环境的 source map
    sourcemap: false,
    // 输出目录
    outDir: 'dist',
    // 静态资源目录
    assetsDir: 'assets',
    // 生成的静态资源文件名，默认包含 hash
    assetsInlineLimit: 4096, // 小于 4kb 的资源内联
    // 最小化混淆
    minify: 'terser',
    //  terser 配置
    terserOptions: {
      compress: {
        drop_console: true, // 移除 console
        drop_debugger: true // 移除 debugger
      }
    }
  },
  server: {
    port: 3000,
    open: true,
    // 配置处理 history 模式的刷新问题
    historyApiFallback: true,
    // 更新代理配置以匹配相对路径
    proxy: {
      // 将 /admin 代理到 http://localhost:8081
      '/admin': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/admin'前缀
      },
      // 将 /like 代理到 http://localhost:8082
      '/mylike': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/like'前缀
      },
      // 将 /login 代理到 http://localhost:8083
      '/login': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/login'前缀
      },
      // 将 /msg 代理到 http://localhost:8084
      '/msg': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/msg'前缀
      },
      // 将 /music 代理到 http://localhost:8085
      '/music': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/music'前缀
      },
      // 将 /setting 代理到 http://localhost:8086
      '/setting': {
        target: 'http://localhost:8086',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/setting'前缀
      },
      // 将 /singer 代理到 http://localhost:8087
      '/singer': {
        target: 'http://localhost:8087',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/singer'前缀
      },
      // 将 /songList 代理到 http://localhost:8088
      '/songList': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/songList\/songList/, '/songList') // 处理路径重复问题
      },
      // 将 /upload 代理到 http://localhost:8089
      '/upload': {
        target: 'http://localhost:8089',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/upload'前缀
      },
      // 保留API代理配置
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // 保留路径，不再移除'/api'前缀
      }
    },
    // 添加CORS配置
    cors: {
      origin: true, // 允许所有来源
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
      allowedHeaders: ['Content-Type', 'Authorization', 'id'],
      credentials: true
    }
  }
})