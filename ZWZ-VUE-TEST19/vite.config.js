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
    // 配置处理 history 模式的刷新问题，使用自定义配置
    historyApiFallback: {
      rewrites: [
        // 静态资源直接返回
        { from: /^\/.*\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$/, to: '/' },
        // /api 开头的请求不重写，由代理处理
        { from: /^\/api/, to: context => context.parsedUrl.pathname }
      ]
    },
    // 更新代理配置，只代理 /api 请求
    proxy: {
      // 将 /api/* 代理到网关
      '^/api/': {
        target: 'http://localhost:8080',
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
