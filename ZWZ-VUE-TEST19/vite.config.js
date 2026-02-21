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
    // 更新代理配置，只代理 API 请求，不代理前端路由
    proxy: {
      // 将 /api/admin 代理到 http://localhost:8081
      '^/api/admin.*': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/like 代理到 http://localhost:8082
      '^/api/like.*': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/login 代理到 http://localhost:8083
      '^/api/login.*': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/msg 代理到 http://localhost:8084
      '^/api/msg.*': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/music 代理到 http://localhost:8085
      '^/api/music.*': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/setting 代理到 http://localhost:8086
      '^/api/setting.*': {
        target: 'http://localhost:8086',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/singer 代理到 http://localhost:8087
      '^/api/singer.*': {
        target: 'http://localhost:8087',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
      },
      // 将 /api/songList 代理到 http://localhost:8088
      '^/api/songList.*': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '').replace(/^\/songList\/songList/, '/songList') // 移除 /api 前缀并处理路径重复
      },
      // 将 /api/upload 代理到 http://localhost:8089
      '^/api/upload.*': {
        target: 'http://localhost:8089',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '') // 移除 /api 前缀
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
