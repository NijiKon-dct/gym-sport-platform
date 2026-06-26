<template>
  <header class="app-header">
    <div class="container">
      <div class="app-header__content">
        <div class="app-header__logo">
          <RouterLink to="/" class="logo-link">
            <span class="logo-text">🏋️ 体育馆预约与社交</span>
          </RouterLink>
        </div>
        <nav class="app-header__nav">
          <template v-if="isAdmin">
            <RouterLink to="/" class="nav-link">首页</RouterLink>
            <RouterLink to="/venues" class="nav-link">场馆列表</RouterLink>
            <RouterLink to="/admin" class="nav-link admin-nav-link">🔧 管理员面板</RouterLink>
            <RouterLink to="/profile" class="nav-link">个人中心</RouterLink>
          </template>
          <template v-else>
            <RouterLink to="/" class="nav-link">首页</RouterLink>
            <RouterLink to="/venues" class="nav-link">场馆列表</RouterLink>
            <RouterLink v-if="isLoggedIn" to="/social" class="nav-link">社交</RouterLink>
            <RouterLink v-if="isLoggedIn" to="/friends" class="nav-link">好友与聊天</RouterLink>
            <RouterLink v-if="isLoggedIn" to="/bookings" class="nav-link">我的预约</RouterLink>
            <RouterLink v-if="isLoggedIn" to="/profile" class="nav-link">个人中心</RouterLink>
          </template>
        </nav>
        <div class="app-header__actions">
          <template v-if="!isLoggedIn">
            <RouterLink to="/login" class="action-link">登录</RouterLink>
            <RouterLink to="/register" class="action-link action-link--primary">注册</RouterLink>
          </template>
          <template v-else>
            <div class="user-info">
              <img
                v-if="userInfo?.avatar"
                :src="getAvatarUrl(userInfo.avatar)"
                alt="用户头像"
                class="user-avatar"
              >
              <span v-else class="user-avatar-placeholder">👤</span>
              <span class="user-name">{{ userInfo?.username || '用户' }}</span>
            </div>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </template>
        </div>
      </div>
    </div>
  </header>
  <button
    v-if="showScrollTop"
    class="scroll-top-btn"
    type="button"
    @click="scrollToTop"
    aria-label="回到顶部"
  >
    ▲
  </button>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => Boolean(userInfo.value?.isAdmin))

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

// 获取头像URL，处理不同格式的路径
const getAvatarUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  // 对路径进行URL编码，处理空格等特殊字符
  const encodedPath = path.split('/').map(segment => encodeURIComponent(segment)).join('/')
  return `${baseURL.replace('/api', '')}/api/files/${encodedPath}`
}

const showScrollTop = ref(false)
const SCROLL_THRESHOLD = 180

const handleScroll = () => {
  showScrollTop.value = window.scrollY > SCROLL_THRESHOLD
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.app-header {
  background-color: var(--bg-color);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.app-header__content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.app-header__logo {
  flex-shrink: 0;
}

.logo-link {
  text-decoration: none;
  color: var(--text-primary);
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

.app-header__nav {
  display: flex;
  gap: var(--spacing-lg);
  flex: 1;
  justify-content: center;
}

.nav-link {
  padding: 8px 16px;
  text-decoration: none;
  color: var(--text-primary);
  font-size: 14px;
  border-radius: var(--border-radius-md);
  transition: all 0.3s;
}

.nav-link:hover {
  background-color: var(--border-extra-light);
  color: var(--primary-color);
}

.nav-link.router-link-active {
  color: var(--primary-color);
  font-weight: 500;
}

/* 管理员导航链接样式 */
.admin-nav-link {
  background-color: var(--primary-color);
  color: #ffffff;
  font-weight: 500;
}

.admin-nav-link:hover {
  background-color: #409eff;
  color: #ffffff;
}

.admin-nav-link.router-link-active {
  background-color: #ff4d4f;
  color: #ffffff;
}

.app-header__actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.action-link {
  padding: 8px 16px;
  text-decoration: none;
  color: var(--text-primary);
  font-size: 14px;
  border-radius: var(--border-radius-md);
  transition: all 0.3s;
}

.action-link:hover {
  background-color: var(--border-extra-light);
}

.action-link--primary {
  background-color: var(--primary-color);
  color: #fff;
}

.action-link--primary:hover {
  background-color: #66b1ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-light);
}

.user-avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--border-extra-light);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.user-name {
  font-size: 14px;
  color: var(--text-primary);
}

.logout-btn {
  padding: 8px 16px;
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  background-color: transparent;
  color: var(--text-primary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .app-header__nav {
    display: none;
  }
}

.scroll-top-btn {
  position: fixed;
  top: 78px;
  left: 50%;
  transform: translateX(-50%);
  width: 46px;
  height: 46px;
  border-radius: 50%;
  border: 1px solid var(--border-light);
  background: #ffffff;
  color: #000000;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.12);
  cursor: pointer;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.scroll-top-btn:hover {
  transform: translate(-50%, -2px);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.16);
}

.scroll-top-btn:active {
  transform: translateX(-50%);
}
</style>
