import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useUserStore } from '../stores/user'

// Helper function for lazy loading with better error handling
const lazyLoad = (importFn: () => Promise<{ default: unknown }>) => {
  return () =>
    importFn().catch((error) => {
      console.error('Failed to load module:', error)
      console.error('Module path:', error.message)
      throw error
    })
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      meta: { allowGuest: true },
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      meta: { allowGuest: true },
      component: lazyLoad(() => import('../views/LoginView.vue')),
    },
    {
      path: '/register',
      name: 'register',
      meta: { allowGuest: true },
      component: lazyLoad(() => import('../views/RegisterView.vue')),
    },
    {
      path: '/venues',
      name: 'venues',
      meta: { allowGuest: true },
      component: lazyLoad(() => import('../views/VenuesView.vue')),
    },
    {
      path: '/venues/:id',
      name: 'venue-detail',
      meta: { allowGuest: true },
      component: lazyLoad(() => import('../views/VenueDetailView.vue')),
    },
    {
      path: '/bookings',
      name: 'bookings',
      component: lazyLoad(() => import('../views/BookingsView.vue')),
    },
    {
      path: '/profile',
      name: 'profile',
      component: lazyLoad(() => import('../views/ProfileView.vue')),
    },
    {
      path: '/social',
      name: 'social',
      component: lazyLoad(() => import('../views/SocialView.vue')),
    },
    {
      path: '/friends',
      name: 'friends',
      component: lazyLoad(() => import('../views/FriendsChatView.vue')),
    },
    {
      path: '/chat',
      name: 'chat',
      component: lazyLoad(() => import('../views/FriendsChatView.vue')),
    },
    {
      path: '/chat/:userId',
      name: 'chat-user',
      component: lazyLoad(() => import('../views/FriendsChatView.vue')),
    },
    {
      path: '/about',
      name: 'about',
      component: lazyLoad(() => import('../views/AboutView.vue')),
    },
    {
      path: '/topics/:name',
      name: 'topic',
      component: lazyLoad(() => import('../views/TopicView.vue')),
    },
    // 管理员路由（使用嵌套路由保留侧边栏）
    {
      path: '/admin',
      component: lazyLoad(() => import('../views/AdminDashboardView.vue')),
      meta: { requiresAdmin: true },
      children: [
        {
          path: '',
          redirect: { name: 'admin-statistics' },
        },
        {
          path: 'statistics',
          name: 'admin-statistics',
          component: lazyLoad(() => import('../views/AdminStatisticsView.vue')),
          meta: { requiresAdmin: true },
        },
        {
          path: 'venues',
          name: 'admin-venues',
          component: lazyLoad(() => import('../views/AdminVenuesView.vue')),
          meta: { requiresAdmin: true },
        },
        {
          path: 'users',
          name: 'admin-users',
          component: lazyLoad(() => import('../views/AdminUsersView.vue')),
          meta: { requiresAdmin: true },
        },
        {
          path: 'social',
          name: 'admin-social',
          component: lazyLoad(() => import('../views/AdminSocialView.vue')),
          meta: { requiresAdmin: true },
        },
        {
          path: 'schools',
          name: 'admin-schools',
          component: lazyLoad(() => import('../views/AdminSchoolsView.vue')),
          meta: { requiresAdmin: true },
        },
      ],
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      // 浏览器前进/后退时，保持原滚动位置
      return savedPosition
    }
    // 普通路由跳转时，滚动到页面顶部
    return { left: 0, top: 0 }
  },
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 获取用户状态
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const isAdmin = userStore.userInfo?.isAdmin

  // 已登录用户访问登录/注册页时直接跳转到对应主页
  if (isLoggedIn && (to.name === 'login' || to.name === 'register')) {
    if (isAdmin) {
      return next({ name: 'admin-statistics' })
    }
    return next({ name: 'home' })
  }

  // 未登录且目标路由未标记 allowGuest，则统一要求登录
  if (!isLoggedIn && !to.meta.allowGuest) {
    return next({ name: 'login' as const, query: { redirect: to.fullPath } })
  }

  // 如果路由需要管理员权限
  if (to.meta.requiresAdmin) {
    // 检查是否登录
    if (!isAdmin) {
      // 已登录但不是管理员，重定向到首页
      next({ name: 'home' as const })
    } else {
      // 是管理员，允许访问
      next()
    }
  } else {
    // 不需要管理员权限的路由，直接允许访问
    next()
  }
})

export default router
