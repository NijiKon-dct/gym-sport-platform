<template>
  <div class="admin-dashboard">
    <div class="dashboard-layout">
      <aside class="sidebar">
        <div class="sidebar-header">
          <div class="sidebar-icon">⚙️</div>
          <h2 class="sidebar-title">管理员控制面板</h2>
        </div>
        <nav class="nav-list">
          <button
            class="nav-item"
            :class="{ active: isActive('admin-statistics') }"
            @click="go('/admin/statistics')"
          >
            <span class="nav-icon">📊</span>
            <span class="nav-text">预约统计</span>
          </button>
          <button
            class="nav-item"
            :class="{ active: isActive('admin-venues') }"
            @click="go('/admin/venues')"
          >
            <span class="nav-icon">🏟️</span>
            <span class="nav-text">场馆管理</span>
          </button>
          <button
            class="nav-item"
            :class="{ active: isActive('admin-users') }"
            @click="go('/admin/users')"
          >
            <span class="nav-icon">👥</span>
            <span class="nav-text">用户管理</span>
          </button>
          <button
            class="nav-item"
            :class="{ active: isActive('admin-social') }"
            @click="go('/admin/social')"
          >
            <span class="nav-icon">💬</span>
            <span class="nav-text">社交动态管理</span>
          </button>
          <button
            class="nav-item"
            :class="{ active: isActive('admin-schools') }"
            @click="go('/admin/schools')"
          >
            <span class="nav-icon">🎓</span>
            <span class="nav-text">学校选项管理</span>
          </button>
        </nav>
      </aside>

      <main class="content">
        <RouterView v-slot="{ Component }">
          <Transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </Transition>
        </RouterView>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter, RouterView } from 'vue-router'

const router = useRouter()
const route = useRoute()

const go = (path: string) => router.push(path)
const isActive = (name: string) => route.name === name
</script>

<style scoped>
.admin-dashboard {
  padding: var(--spacing-xl) 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 160px);
}

.dashboard-layout {
  display: flex;
  gap: var(--spacing-xl);
  min-height: calc(100vh - 160px);
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
}

.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
  position: sticky;
  top: var(--spacing-lg);
  height: fit-content;
  backdrop-filter: blur(10px);
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding-bottom: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-lighter);
}

.sidebar-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  border-radius: var(--border-radius-md);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.nav-item {
  width: 100%;
  text-align: left;
  padding: var(--spacing-md) var(--spacing-md);
  border-radius: var(--border-radius-md);
  border: 1px solid transparent;
  background-color: transparent;
  color: var(--text-primary);
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: var(--primary-color);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.nav-item:hover {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.1), rgba(102, 177, 255, 0.05));
  border-color: var(--border-light);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.nav-item:hover::before {
  transform: scaleY(1);
}

.nav-item.active {
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  color: #fff;
  border-color: var(--primary-color);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.35);
  transform: translateX(4px);
}

.nav-item.active::before {
  transform: scaleY(1);
  background: rgba(255, 255, 255, 0.3);
}

.nav-icon {
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  transition: transform 0.3s ease;
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);
}

.nav-item.active .nav-icon {
  transform: scale(1.15);
}

.nav-text {
  font-weight: 500;
  flex: 1;
}

.content {
  flex: 1;
  min-width: 0;
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: var(--spacing-xl);
  animation: fadeInUp 0.4s ease;
}

/* 页面过渡动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 900px) {
  .dashboard-layout {
    flex-direction: column;
    padding: 0 var(--spacing-md);
  }

  .sidebar {
    width: 100%;
    position: relative;
    top: 0;
  }
  
  .nav-item {
    justify-content: center;
  }

  .nav-text {
    text-align: center;
  }
}
</style>