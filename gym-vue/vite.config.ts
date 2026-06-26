import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools({
      // 仅在开发环境启用
      enabled: process.env.NODE_ENV === 'development',
      // 禁用组件检查器以减少通信错误
      componentInspector: false,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    hmr: {
      clientPort: 5173,
      protocol: 'ws',
    },
    proxy: {
      '/api': {
        target: process.env.VITE_API_PROXY_TARGET || 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: false, // API 请求不需要 WebSocket
      },
      '/ws-chat': {
        target: process.env.VITE_API_PROXY_TARGET || 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
        secure: false,
        // 添加错误处理
        onError(err, req, res) {
          console.log('WebSocket proxy error:', err.message)
        },
        // 添加超时设置
        timeout: 10000,
      },
    },
    // 添加 WebSocket 相关配置
    watch: {
      usePolling: false,
    },
  },
  define: {
    global: 'window'
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia']
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: undefined
      }
    }
  }
})
