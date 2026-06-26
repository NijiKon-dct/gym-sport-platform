<script setup lang="ts">
import { RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
</script>

<template>
  <div class="app">
    <Header />
    <main class="app-main">
      <RouterView v-slot="{ Component, route }">
        <Transition name="page" mode="out-in">
          <!--
            使用顶级匹配路由作为过渡 key：
            - 像 /admin/statistics 和 /admin/users 这类同一父路由(/admin)下的子路由，
              不会重新销毁整个 AdminDashboard 布局，只在内部切换内容区域，
              避免给人“整页刷新”的感觉。
          -->
          <component
            :is="Component"
            :key="route.matched[0]?.path ?? route.path"
          />
        </Transition>
      </RouterView>
    </main>
    <Footer />
  </div>
</template>

<style>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
  width: 100%;
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
