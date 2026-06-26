import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// 引入 TDesign Vue 3
import TDesign from 'tdesign-vue-next'
import 'tdesign-vue-next/es/style/index.css'

import App from './App.vue'
import router from './router'

// 抑制浏览器扩展相关的运行时错误
if (typeof window !== 'undefined') {
  // 过滤控制台错误
  const originalError = console.error
  console.error = (...args: any[]) => {
    // 过滤掉浏览器扩展相关的错误
    if (
      args.some(
        (arg) =>
          typeof arg === 'string' &&
          (arg.includes('runtime.lastError') ||
            arg.includes('message port closed') ||
            arg.includes('Extension context invalidated') ||
            arg.includes('Unchecked runtime.lastError'))
      )
    ) {
      return // 忽略这些错误
    }
    originalError.apply(console, args)
  }
}

// 全局错误处理
window.addEventListener('error', (event) => {
  // 忽略浏览器扩展相关的错误
  if (
    event.message?.includes('runtime.lastError') ||
    event.message?.includes('message port closed') ||
    event.message?.includes('Extension context invalidated')
  ) {
    event.preventDefault()
    return false
  }
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(TDesign)

app.mount('#app')
