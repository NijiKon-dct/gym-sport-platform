<template>
  <div class="home-view">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="hero-pattern"></div>
        <div class="hero-glow glow-one"></div>
        <div class="hero-glow glow-two"></div>
      </div>
      <div class="hero-grid">
        <div class="hero-content">
          <div class="hero-badge animate-fade-in">校园 · 场馆 · 社交</div>
          <h1 class="hero-title animate-fade-in">
            专业的体育馆预约与 <span>运动社交</span> 平台
          </h1>
          <p class="hero-subtitle animate-fade-in-delay">
            一站式打通校园资源与社会场馆，随时预约、结识同好、掌握场馆运营动态。
          </p>
          <div class="hero-actions animate-fade-in-delay-2">
            <RouterLink to="/venues">
              <BaseButton type="primary" size="large">浏览场馆</BaseButton>
            </RouterLink>
            <RouterLink v-if="!isLoggedIn" to="/register">
              <BaseButton type="secondary" size="large">立即注册</BaseButton>
            </RouterLink>
          </div>
          <ul class="hero-highlights animate-fade-in-delay-3">
            <li v-for="item in heroHighlights" :key="item.title">
              <span class="dot"></span>
              <div>
                <strong>{{ item.title }}</strong>
                <p>{{ item.desc }}</p>
              </div>
            </li>
          </ul>
        </div>

        <div class="hero-spotlight">
          <div class="spotlight-card primary">
            <div class="spotlight-label">实时预约</div>
            <h3 class="time-display">{{ averageConfirmTime }}</h3>
            <p>所有用户平均确认用时</p>
            <div class="spotlight-progress">
              <div class="progress-fill"></div>
            </div>
          </div>
          <div class="spotlight-card secondary">
            <div class="stat-pill">本周场馆热度</div>
            <div class="stat-value">+37%</div>
            <p>较上周增长</p>
          </div>
          <div class="hero-stats animate-fade-in-delay-3">
            <div class="stat-box">
              <div class="stat-number">100+</div>
              <div class="stat-text">合作场馆</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">5000+</div>
              <div class="stat-text">活跃用户</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">10000+</div>
              <div class="stat-text">成功预约</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 数据概览 -->
    <section id="stats-section" class="stats-section scroll-animate">
      <div class="container stats-grid">
        <div class="stats-card">
          <div class="stats-icon">🏟️</div>
          <div>
            <div class="stats-value">{{ animatedStats.totalVenues }}</div>
            <div class="stats-label">平台场馆</div>
          </div>
          <div class="stats-decoration"></div>
        </div>
        <div class="stats-card">
          <div class="stats-icon">🎓</div>
          <div>
            <div class="stats-value">{{ animatedStats.campusVenues }}</div>
            <div class="stats-label">校园合作</div>
          </div>
          <div class="stats-decoration"></div>
        </div>
        <div class="stats-card">
          <div class="stats-icon">💰</div>
          <div>
            <div class="stats-value">¥{{ animatedStats.averagePrice }}</div>
            <div class="stats-label">人均价格 / 小时</div>
          </div>
          <div class="stats-decoration"></div>
        </div>
        <div class="stats-card">
          <div class="stats-icon">🔥</div>
          <div>
            <div class="stats-value">{{ topTrendingSport }}</div>
            <div class="stats-label">本周热门运动</div>
          </div>
          <div class="stats-decoration"></div>
        </div>
      </div>
    </section>

    <!-- 亮点功能 -->
    <section id="feature-section" class="feature-section scroll-animate">
      <div class="container">
        <h2 class="section-title">
          <span class="title-decoration"></span>
          平台亮点
          <span class="title-decoration"></span>
        </h2>
        <div class="feature-grid">
          <div v-for="(feature, index) in features" :key="feature.title" class="feature-card scroll-animate" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="feature-icon-wrapper">
              <div class="feature-icon">{{ feature.icon }}</div>
              <div class="feature-icon-bg"></div>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.desc }}</p>
            <div class="feature-hover-effect"></div>
          </div>
        </div>
      </div>
    </section>

    <!-- Venue Section -->
    <section id="venue-section" class="venue-section scroll-animate">
      <div class="container">
        <h2 class="section-title">
          <span class="title-decoration"></span>
          {{ isStudent ? `${userInfo?.school || '学校'}相关体育馆` : '热门场馆' }}
          <span class="title-decoration"></span>
        </h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="featuredVenues.length === 0" class="empty-state">
          <p>暂无相关场馆</p>
        </div>
        <div v-else class="venue-grid">
          <VenueCard
            v-for="(venue, index) in featuredVenues"
            :key="venue.id"
            :venue="venue"
            :style="{ animationDelay: `${index * 0.1}s` }"
            class="venue-card-animate"
            @book="handleBook"
          />
        </div>
        <div class="section-actions">
          <RouterLink to="/venues">
            <BaseButton type="primary">查看更多场馆</BaseButton>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- Process Section -->
    <section id="process-section" class="process-section scroll-animate">
      <div class="container">
        <h2 class="section-title">
          <span class="title-decoration"></span>
          预约流程
          <span class="title-decoration"></span>
        </h2>
        <div class="process-steps">
          <div class="process-step scroll-animate" data-step="1" style="animation-delay: 0.1s">
            <div class="step-icon-wrapper">
              <div class="step-icon">1</div>
              <div class="step-icon-ring"></div>
            </div>
            <h3 class="step-title">选择场馆</h3>
            <p class="step-desc">浏览并选择您心仪的体育馆</p>
            <div class="step-arrow">→</div>
          </div>
          <div class="process-step scroll-animate" data-step="2" style="animation-delay: 0.2s">
            <div class="step-icon-wrapper">
              <div class="step-icon">2</div>
              <div class="step-icon-ring"></div>
            </div>
            <h3 class="step-title">选择时间</h3>
            <p class="step-desc">选择适合您的预约时间</p>
            <div class="step-arrow">→</div>
          </div>
          <div class="process-step scroll-animate" data-step="3" style="animation-delay: 0.3s">
            <div class="step-icon-wrapper">
              <div class="step-icon">3</div>
              <div class="step-icon-ring"></div>
            </div>
            <h3 class="step-title">确认预约</h3>
            <p class="step-desc">确认信息并完成预约</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <section id="testimonials-section" class="testimonials-section scroll-animate">
      <div class="container">
        <h2 class="section-title">
          <span class="title-decoration"></span>
          用户评价
          <span class="title-decoration"></span>
        </h2>
        <div class="testimonials-grid">
          <div class="testimonial-card scroll-animate" v-for="(testimonial, index) in testimonials" :key="index" :style="{ animationDelay: `${index * 0.15}s` }">
            <div class="testimonial-quote">"</div>
            <div class="testimonial-rating">⭐⭐⭐⭐⭐</div>
            <div class="testimonial-content">
              <p>{{ testimonial.content }}</p>
            </div>
            <div class="testimonial-author">
              <div class="author-avatar">{{ testimonial.name[0] }}</div>
              <div class="author-info">
                <div class="author-name">{{ testimonial.name }}</div>
                <div class="author-role">{{ testimonial.role }}</div>
              </div>
            </div>
            <div class="testimonial-decoration"></div>
          </div>
        </div>
      </div>
    </section>
    <!-- 最新动态 -->
    <section id="social-section" class="social-section scroll-animate">
      <div class="container">
        <div class="social-header">
          <h2 class="section-title">
            <span class="title-decoration"></span>
            社区热议
            <span class="title-decoration"></span>
          </h2>
          <button class="link-more" @click="handleMoreClick">
            查看更多
            <span class="link-arrow">→</span>
          </button>
        </div>
        <div v-if="socialLoading" class="loading">加载中...</div>
        <div v-else-if="topPosts.length === 0" class="empty-state">暂时没有动态，快去发布第一条吧！</div>
        <div v-else class="social-grid">
          <div v-for="(post, index) in topPosts" :key="post.id" class="social-card scroll-animate" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="social-author">
              <div class="social-avatar">{{ post.username[0] }}</div>
              <div class="social-info">
                <div class="social-name">{{ post.username }}</div>
                <div class="social-time">{{ formatTime(post.createdAt) }}</div>
              </div>
            </div>
            <p class="social-content">{{ post.content }}</p>
            <div class="social-meta">
              <span class="meta-item">❤️ {{ post.likes }}</span>
              <span class="meta-item">💬 {{ post.comments }}</span>
              <span v-if="post.venueName" class="meta-item">📍 {{ post.venueName }}</span>
            </div>
            <div class="social-card-decoration"></div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useVenueStore } from '@/stores/venue'
import { useSocialStore } from '@/stores/social'
import { BaseButton } from '@/components/common'
import VenueCard from '@/components/VenueCard.vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const venueStore = useVenueStore()
const socialStore = useSocialStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const isStudent = computed(() => userInfo.value?.isStudent === true)
const loading = computed(() => venueStore.loading)
const socialLoading = computed(() => socialStore.loading)

// 滚动动画相关
const visibleSections = ref<Set<string>>(new Set())
const observerRef = ref<IntersectionObserver | null>(null)

// 数字计数动画
const animatedStats = ref({
  totalVenues: 0,
  campusVenues: 0,
  averagePrice: 0,
})

const animateNumber = (target: number, key: keyof typeof animatedStats.value, duration = 2000) => {
  const start = animatedStats.value[key]
  const startTime = Date.now()

  const animate = () => {
    const elapsed = Date.now() - startTime
    const progress = Math.min(elapsed / duration, 1)
    const easeOutQuart = 1 - Math.pow(1 - progress, 4)
    animatedStats.value[key] = Math.floor(start + (target - start) * easeOutQuart)

    if (progress < 1) {
      requestAnimationFrame(animate)
    } else {
      animatedStats.value[key] = target
    }
  }

  animate()
}

// Intersection Observer 用于滚动动画
const setupScrollObserver = () => {
  observerRef.value = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          visibleSections.value.add(entry.target.id)
          entry.target.classList.add('visible')
        }
      })
    },
    {
      threshold: 0.1,
      rootMargin: '0px 0px -50px 0px',
    }
  )

  // 观察所有需要动画的元素
  const elementsToObserve = document.querySelectorAll('.scroll-animate')
  elementsToObserve.forEach((el) => observerRef.value?.observe(el))
}

// 平均确认用时（秒）
const averageConfirmSeconds = ref(0)

// 将秒格式化为 HH:mm:ss
const averageConfirmTime = computed(() => {
  const total = averageConfirmSeconds.value
  if (!total || total <= 0) return '00:00:00'
  const hours = Math.floor(total / 3600)
  const minutes = Math.floor((total % 3600) / 60)
  const seconds = total % 60
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`
})

const fetchAverageConfirmTime = async () => {
  try {
    const seconds = await request.get<number>('/bookings/stats/average-confirm-time')
    averageConfirmSeconds.value = seconds || 0
  } catch (error) {
    // 忽略错误，保持默认值
    console.error('获取平均确认用时失败', error)
  }
}

// 如果是学生，显示学校相关的体育馆；否则显示热门场馆
const featuredVenues = computed(() => {
  if (isStudent.value && userInfo.value?.school) {
    const schoolVenues = venueStore.getVenuesBySchool(userInfo.value.school)
    return schoolVenues.slice(0, 4)
  }
  return venueStore.venues.slice(0, 4)
})

const totalVenues = computed(() => venueStore.venues.length)
const campusVenues = computed(() => venueStore.venues.filter((v) => v.school).length)
const averagePrice = computed(() => {
  if (!venueStore.venues.length) return 0
  const totalPrice = venueStore.venues.reduce((sum, venue) => sum + (venue.price || 0), 0)
  return Math.round(totalPrice / venueStore.venues.length)
})

// 监听数据变化，触发动画
const watchStats = () => {
  if (totalVenues.value > 0) {
    setTimeout(() => {
      animateNumber(totalVenues.value, 'totalVenues')
      animateNumber(campusVenues.value, 'campusVenues')
      animateNumber(averagePrice.value, 'averagePrice')
    }, 500)
  }
}

const features = [
  { icon: '⚡', title: '极速预约', desc: '一键预约喜欢的场馆，实时查看空闲时间段。' },
  { icon: '🤝', title: '运动社交', desc: '找到志同道合的运动伙伴，支持私聊与约局。' },
  { icon: '🔐', title: '校园认证', desc: '学生认证后可查看专属校园场馆与优惠。' },
  { icon: '📊', title: '智能统计', desc: '管理员可实时掌握预约、收入与使用率。' },
]

const heroHighlights = [
  { title: '跨校资源联动', desc: '接入多所高校+社会体育中心' },
  { title: '统一好友体系', desc: '一键添加、聊天、组队训练' },
  { title: '可视化报表', desc: '管理员实时查看场馆利用率' },
]

const posts = computed(() => socialStore.posts)
const topPosts = computed(() => posts.value.slice(0, 3))

const sportPopularity = computed(() => {
  const counts: Record<string, number> = {}
  posts.value.forEach((post) => {
    if (post.venueName) {
      const type = venueStore.venues.find((v) => v.name === post.venueName)?.type || '综合运动'
      counts[type] = (counts[type] || 0) + 1
    }
  })
  return counts
})

const topTrendingSport = computed(() => {
  const entries = Object.entries(sportPopularity.value)
  if (!entries.length) return '综合运动'
  return entries.sort((a, b) => b[1] - a[1])[0][0]
})

const testimonials = [
  {
    name: '张先生',
    role: '运动爱好者',
    content: '非常方便的平台，预约流程简单快捷，场馆环境也很好！',
  },
  {
    name: '李女士',
    role: '羽毛球爱好者',
    content: '价格合理，服务周到，是我用过最好的预约平台！',
  },
  {
    name: '王先生',
    role: '篮球爱好者',
    content: '界面美观，操作流畅，推荐给所有运动爱好者！',
  },
]

const handleBook = (venueId: number) => {
  router.push(`/venues/${venueId}`)
}

const handleMoreClick = () => {
  if (!isLoggedIn.value) {
    const confirmLogin = window.confirm('请先登录后再查看社区动态，是否前往登录？')
    if (confirmLogin) {
      router.push('/login')
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    return
  }
  router.push('/social')
}

const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

onMounted(() => {
  venueStore.fetchVenues().then(() => {
    watchStats()
  })
  if (socialStore.posts.length === 0) {
    socialStore.fetchPosts()
  }

  // 设置滚动观察器
  setTimeout(() => {
    setupScrollObserver()
  }, 100)

  // 获取平均确认用时
  fetchAverageConfirmTime()
})

onUnmounted(() => {
  observerRef.value?.disconnect()
})
</script>

<style scoped>
.home-view {
  width: 100%;
}

.hero-section {
  position: relative;
  color: #101631;
  padding: clamp(48px, 6vw, 96px) 0;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.95) 0%, #eef3ff 45%),
    linear-gradient(135deg, #dfe8ff 0%, #f7f9ff 100%);
}

.hero-pattern {
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(96, 183, 255, 0.18) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
  opacity: 0.7;
}

.hero-glow {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.4;
  mix-blend-mode: multiply;
}

.glow-one {
  top: -80px;
  right: 10%;
  background: #e0e7ff;
}

.glow-two {
  bottom: -120px;
  left: 5%;
  background: #c7f1ff;
}

.hero-grid {
  position: relative;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: clamp(32px, 4vw, 60px);
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-md);
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

.animate-fade-in {
  animation: fadeInUp 0.8s ease-out;
}

.animate-fade-in-delay {
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.animate-fade-in-delay-2 {
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.animate-fade-in-delay-3 {
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-content {
  max-width: 560px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(76, 116, 255, 0.15);
  color: #4c74ff;
  border-radius: 999px;
  padding: 8px 18px;
  font-size: 14px;
  letter-spacing: 0.2em;
  margin-bottom: var(--spacing-md);
}

.hero-title {
  font-size: clamp(36px, 5vw, 60px);
  font-weight: 700;
  line-height: 1.15;
  margin-bottom: var(--spacing-md);
}

.hero-title span {
  color: #4c74ff;
}

.hero-subtitle {
  font-size: clamp(16px, 2vw, 20px);
  margin-bottom: var(--spacing-xl);
  opacity: 0.9;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-xl);
}

.hero-highlights {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-highlights li {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.hero-highlights .dot {
  width: 8px;
  height: 8px;
  background: #4c74ff;
  border-radius: 50%;
  margin-top: 9px;
}

.hero-highlights strong {
  display: block;
  font-size: 16px;
}

.hero-highlights p {
  margin: 2px 0 0;
  opacity: 0.8;
  font-size: 14px;
}

.hero-spotlight {
  display: grid;
  gap: var(--spacing-md);
}

.spotlight-card {
  border-radius: 24px;
  padding: var(--spacing-lg);
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 25px 60px rgba(8, 18, 69, 0.22);
  backdrop-filter: blur(10px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.spotlight-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.4s;
}

.spotlight-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 30px 70px rgba(8, 18, 69, 0.3);
  border-color: rgba(255, 255, 255, 0.25);
}

.spotlight-card:hover::before {
  opacity: 1;
}

.spotlight-card.primary h3 {
  font-size: 42px;
  margin: 12px 0 4px;
}

.time-display {
  font-family: 'Courier New', monospace;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

.spotlight-label,
.stat-pill {
  font-size: 12px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  opacity: 0.8;
}

.spotlight-progress {
  margin-top: 18px;
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

.progress-fill {
  width: 78%;
  height: 100%;
  background: linear-gradient(90deg, #f6d365 0%, #fda085 100%);
  border-radius: 999px;
  animation: progressFill 2s ease-out;
  position: relative;
  overflow: hidden;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: shimmer 2s infinite;
}

@keyframes progressFill {
  from {
    width: 0%;
  }
  to {
    width: 78%;
  }
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

.spotlight-card.secondary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.stat-box {
  text-align: center;
  padding: var(--spacing-md);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.stat-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.stat-box:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.stat-box:hover::before {
  left: 100%;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: var(--spacing-xs);
  color: #0b1228;
}

.stat-text {
  font-size: 14px;
  opacity: 0.9;
}

.stats-section,
.feature-section,
.social-section,
.venue-section,
.process-section,
.testimonials-section {
  padding: var(--spacing-xl) 0;
}

.stats-section {
  background: linear-gradient(180deg, var(--bg-color) 0%, var(--bg-color-page) 100%);
  position: relative;
  overflow: hidden;
}

.stats-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--primary-color), transparent);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--spacing-lg);
}

.stats-card {
  background-color: var(--bg-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-light);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.1), transparent);
  transition: left 0.5s;
}

.stats-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.15);
  border-color: var(--primary-color);
}

.stats-card:hover::before {
  left: 100%;
}

.stats-card.visible {
  animation: slideInUp 0.6s ease-out both;
}

.stats-icon {
  font-size: 40px;
  transition: transform 0.3s;
  position: relative;
  z-index: 1;
}

.stats-card:hover .stats-icon {
  transform: scale(1.15) rotate(5deg);
}

.stats-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  background: linear-gradient(135deg, var(--primary-color), var(--success-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s;
}

.stats-label {
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
}

.stats-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--success-color));
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s;
}

.stats-card:hover .stats-decoration {
  transform: scaleX(1);
}

.feature-section {
  background: linear-gradient(180deg, var(--bg-color-page) 0%, var(--bg-color) 100%);
  position: relative;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
}

.feature-card {
  background-color: var(--bg-color);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  text-align: left;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-lighter);
}

.feature-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.05) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.4s;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.12);
  border-color: var(--primary-color);
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-card.visible {
  animation: fadeInUp 0.8s ease-out both;
}

.feature-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: var(--spacing-md);
}

.feature-icon {
  font-size: 48px;
  position: relative;
  z-index: 2;
  transition: transform 0.4s;
  display: inline-block;
}

.feature-icon-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(103, 194, 58, 0.1));
  opacity: 0;
  transition: all 0.4s;
}

.feature-card:hover .feature-icon {
  transform: scale(1.2) rotate(10deg);
}

.feature-card:hover .feature-icon-bg {
  opacity: 1;
  width: 100px;
  height: 100px;
}

.feature-card h3 {
  margin-bottom: var(--spacing-sm);
  color: var(--text-primary);
  font-size: 20px;
  font-weight: 600;
  transition: color 0.3s;
}

.feature-card:hover h3 {
  color: var(--primary-color);
}

.feature-card p {
  color: var(--text-secondary);
  font-size: 15px;
  line-height: 1.7;
}

.feature-hover-effect {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--success-color));
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s;
}

.feature-card:hover .feature-hover-effect {
  transform: scaleX(1);
}

.social-section {
  background-color: var(--bg-color);
}

.social-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.link-more {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: var(--border-radius-md);
  transition: all 0.3s;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.link-more:hover {
  background: rgba(64, 158, 255, 0.1);
  transform: translateX(4px);
}

.link-arrow {
  transition: transform 0.3s;
  display: inline-block;
}

.link-more:hover .link-arrow {
  transform: translateX(4px);
}

.social-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.social-card {
  background-color: var(--bg-color-page);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-lighter);
}

.social-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: var(--primary-color);
}

.social-card.visible {
  animation: slideInUp 0.6s ease-out both;
}

.social-card-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, var(--primary-color), var(--success-color));
  transform: scaleY(0);
  transform-origin: top;
  transition: transform 0.4s;
}

.social-card:hover .social-card-decoration {
  transform: scaleY(1);
}

.social-author {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.social-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: var(--border-lighter);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.social-info {
  display: flex;
  flex-direction: column;
}

.social-name {
  font-weight: 600;
}

.social-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.social-content {
  color: var(--text-regular);
  line-height: 1.6;
}

.social-meta {
  display: flex;
  gap: var(--spacing-md);
  font-size: 14px;
  color: var(--text-secondary);
  flex-wrap: wrap;
}

.meta-item {
  padding: 4px 10px;
  background: rgba(64, 158, 255, 0.08);
  border-radius: var(--border-radius-sm);
  transition: all 0.3s;
  cursor: default;
}

.meta-item:hover {
  background: rgba(64, 158, 255, 0.15);
  transform: scale(1.05);
}

.venue-section {
  background: linear-gradient(180deg, var(--bg-color) 0%, var(--bg-color-page) 100%);
}

.venue-section.visible {
  animation: fadeIn 0.6s ease-out;
}

.section-title {
  font-size: clamp(28px, 4vw, 36px);
  font-weight: 700;
  text-align: center;
  margin-bottom: var(--spacing-xl);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  position: relative;
}

.title-decoration {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--primary-color), transparent);
  max-width: 100px;
  opacity: 0.6;
}

.scroll-animate {
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.scroll-animate.visible {
  opacity: 1;
  transform: translateY(0);
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.loading {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.loading::before {
  content: '';
  width: 40px;
  height: 40px;
  border: 4px solid var(--border-light);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  font-size: 16px;
  background: var(--bg-color-page);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-light);
}

.venue-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.venue-card-animate {
  animation: slideInUp 0.6s ease-out both;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-actions {
  text-align: center;
}

.process-steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
}

.process-step {
  position: relative;
  text-align: center;
  padding: var(--spacing-lg);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.process-step.visible {
  animation: fadeInUp 0.8s ease-out both;
}

.process-step:hover {
  transform: translateY(-8px);
}

.step-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: var(--spacing-md);
}

.step-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  margin: 0 auto;
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3);
  transition: all 0.4s;
  position: relative;
  z-index: 2;
}

.step-icon-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  border-radius: 50%;
  border: 2px solid var(--primary-color);
  opacity: 0;
  transition: all 0.4s;
}

.process-step:hover .step-icon {
  transform: scale(1.15) rotate(5deg);
  box-shadow: 0 8px 28px rgba(64, 158, 255, 0.4);
}

.process-step:hover .step-icon-ring {
  width: 90px;
  height: 90px;
  opacity: 0.3;
}

.step-arrow {
  position: absolute;
  top: 50%;
  right: -20px;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--primary-color);
  opacity: 0.5;
}

.process-step:last-child .step-arrow {
  display: none;
}

@media (max-width: 968px) {
  .step-arrow {
    display: none;
  }
}

.step-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: var(--spacing-sm);
  color: var(--text-primary);
}

.step-desc {
  color: var(--text-secondary);
  font-size: 14px;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.testimonial-card {
  background-color: var(--bg-color);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-lighter);
  position: relative;
  overflow: hidden;
}

.testimonial-card.visible {
  animation: fadeInUp 0.8s ease-out both;
}

.testimonial-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.15);
  border-color: var(--primary-color);
}

.testimonial-quote {
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 80px;
  color: rgba(64, 158, 255, 0.1);
  font-family: Georgia, serif;
  line-height: 1;
  z-index: 0;
}

.testimonial-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--success-color));
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s;
}

.testimonial-card:hover .testimonial-decoration {
  transform: scaleX(1);
}

.testimonial-rating {
  color: #ffc107;
  font-size: 18px;
  margin-bottom: var(--spacing-md);
  text-align: center;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 2px 4px rgba(255, 193, 7, 0.3));
}

.testimonial-content {
  margin-bottom: var(--spacing-md);
  color: var(--text-regular);
  font-style: italic;
  line-height: 1.8;
  position: relative;
  z-index: 1;
  font-size: 15px;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--success-color));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: transform 0.3s;
  position: relative;
  z-index: 1;
}

.testimonial-card:hover .author-avatar {
  transform: scale(1.1);
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
}

.author-role {
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .section-title {
    font-size: 24px;
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .title-decoration {
    max-width: 60px;
  }

  .stats-grid,
  .feature-grid,
  .venue-grid,
  .social-grid,
  .testimonials-grid,
  .process-steps {
    grid-template-columns: 1fr;
  }

  .social-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }

  .feature-card,
  .testimonial-card {
    padding: var(--spacing-lg);
  }

  .stats-card {
    padding: var(--spacing-md);
  }

  .step-icon {
    width: 60px;
    height: 60px;
    font-size: 24px;
  }
}
</style>
