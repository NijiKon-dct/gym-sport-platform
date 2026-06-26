<template>
  <div class="venues-view page-container">
    <div class="container">
      <h1 class="page-title">场馆列表</h1>

      <!-- 搜索和筛选 -->
      <div class="filter-panel">
        <div class="filter-panel-header">
          <div>
            <h3>筛选场馆</h3>
            <p>支持按场馆类型、性质、价格进行组合筛选</p>
          </div>
          <button class="reset-btn" @click="resetFilters">重置筛选</button>
        </div>
        <div class="venues-filters">
          <div class="search-box">
            <BaseInput v-model="searchKeyword" placeholder="搜索场馆名称或地址…" show-clear>
              <template #prefix>🔍</template>
            </BaseInput>
          </div>
          <div class="filter-box">
            <label>场馆类型</label>
            <select v-model="selectedType" class="filter-select">
              <option value="">全部类型</option>
              <option value="综合体育馆">综合体育馆</option>
              <option value="篮球场">篮球场</option>
              <option value="羽毛球场">羽毛球场</option>
              <option value="游泳池">游泳池</option>
              <option value="健身房">健身房</option>
              <option value="网球场">网球场</option>
              <option value="乒乓球馆">乒乓球馆</option>
              <option value="舞蹈室">舞蹈室</option>
              <option value="足球场">足球场</option>
            </select>
          </div>
          <div class="filter-box">
            <label>场馆性质</label>
            <select v-model="selectedVenueType" class="filter-select">
              <option value="">全部场馆</option>
              <option value="social">社会场馆</option>
              <option value="school">学校场馆</option>
            </select>
          </div>
          <div class="filter-box">
            <label>使用状态</label>
            <select v-model="selectedStatus" class="filter-select">
              <option value="">全部状态</option>
              <option value="free">空闲</option>
              <option value="busy">使用中</option>
            </select>
          </div>
          <div v-if="isLoggedIn" class="filter-box">
            <label>收藏筛选</label>
            <select v-model="selectedFavorite" class="filter-select">
              <option value="">全部场馆</option>
              <option value="favorite">我的收藏</option>
            </select>
          </div>
          <div class="sort-box">
            <label>排序方式</label>
            <select v-model="sortBy" class="filter-select">
              <option value="default">默认排序</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
              <option value="rating-desc">评分从高到低</option>
              <option value="rating-asc">评分从低到高</option>
              <option value="name">按名称排序</option>
            </select>
          </div>
        </div>

        <div class="quick-stats">
          <div class="quick-card">
            <span>全部场馆</span>
            <strong>{{ venueStore.venues.length }}</strong>
          </div>
          <div class="quick-card">
            <span>学校场馆</span>
            <strong>{{ schoolVenueCount }}</strong>
          </div>
          <div class="quick-card">
            <span>社会场馆</span>
            <strong>{{ socialVenueCount }}</strong>
          </div>
          <div v-if="isLoggedIn" class="quick-card">
            <span>我的收藏</span>
            <strong>{{ favoriteCount }}</strong>
          </div>
        </div>
      </div>

      <!-- 结果统计 -->
      <div v-if="!loading && filteredVenues.length > 0" class="results-info">
        <div class="results-stats">
          <span class="stat-item">
            <strong>{{ filteredVenues.length }}</strong> 个场馆
          </span>
          <span class="stat-divider">|</span>
          <span class="stat-item">
            平均评分 <strong>{{ averageRating.toFixed(1) }}</strong>
          </span>
          <span class="stat-divider">|</span>
          <span class="stat-item">
            总评价数 <strong>{{ totalReviews }}</strong>
          </span>
        </div>
      </div>

      <!-- 场馆列表 -->
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filteredVenues.length === 0" class="empty-state">
        <p>暂无场馆数据</p>
      </div>
      <div v-else class="venue-grid">
        <VenueCard
          v-for="(venue, index) in filteredVenues"
          :key="venue.id"
          :venue="venue"
          :style="{ animationDelay: `${index * 0.05}s` }"
          class="venue-item-animate"
          @book="handleBook"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useVenueStore } from '@/stores/venue'
import { useUserStore } from '@/stores/user'
import { BaseInput } from '@/components/common'
import VenueCard from '@/components/VenueCard.vue'

const router = useRouter()
const venueStore = useVenueStore()
const userStore = useUserStore()

const searchKeyword = ref('')
const selectedType = ref('')
const selectedVenueType = ref('')
const selectedStatus = ref('')
const selectedFavorite = ref('')
const sortBy = ref('default')

const loading = computed(() => venueStore.loading)
const isLoggedIn = computed(() => userStore.isLoggedIn)
// 教职工：非学生，且有学校和编号，并且不是管理员
const isStaff = computed(() => {
  const info = userStore.userInfo
  if (!info) return false
  return !info.isStudent && !!info.school && !!info.studentNumber && !info.isAdmin
})
const schoolVenueCount = computed(
  () => venueStore.venues.filter((venue) => venue.school && venue.school !== '').length,
)
const socialVenueCount = computed(
  () => venueStore.venues.filter((venue) => !venue.school || venue.school === '').length,
)
const favoriteCount = computed(
  () => venueStore.venues.filter((venue) => venue.isFavorite === true).length,
)

const averageRating = computed(() => {
  const venuesWithRating = filteredVenues.value.filter((v) => v.averageRating && v.averageRating > 0)
  if (venuesWithRating.length === 0) return 0
  const sum = venuesWithRating.reduce((acc, v) => acc + (v.averageRating || 0), 0)
  return sum / venuesWithRating.length
})

const totalReviews = computed(() => {
  return filteredVenues.value.reduce((acc, v) => acc + (v.reviewCount || 0), 0)
})

const filteredVenues = computed(() => {
  let venues = venueStore.venues
  const isStudent = userStore.userInfo?.isStudent || false
  const isAdmin = userStore.userInfo?.isAdmin || false

  // 登录后，只有「普通用户」才只能查看非学校体育馆；
  // 学生和教职工都可以查看学校场馆
  if (isLoggedIn.value && !isAdmin && !isStudent && !isStaff.value) {
    venues = venues.filter((venue) => !venue.school || venue.school === '')
  }

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    venues = venues.filter(
      (venue) =>
        venue.name.toLowerCase().includes(keyword) ||
        venue.address.toLowerCase().includes(keyword) ||
        (venue.school && venue.school.toLowerCase().includes(keyword)),
    )
  }

  // 类型过滤
  if (selectedType.value) {
    venues = venues.filter((venue) => venue.type === selectedType.value)
  }

  // 场馆性质过滤：社会场馆或学校场馆
  if (selectedVenueType.value === 'social') {
    venues = venues.filter((venue) => !venue.school || venue.school === '')
  } else if (selectedVenueType.value === 'school') {
    venues = venues.filter((venue) => venue.school && venue.school !== '')
  }

  // 使用状态过滤：空闲/使用中（依赖后端提供的 isInUse 字段）
  if (selectedStatus.value === 'free') {
    venues = venues.filter((venue) => !venue.isInUse)
  } else if (selectedStatus.value === 'busy') {
    venues = venues.filter((venue) => venue.isInUse)
  }

  // 收藏筛选：只显示收藏的场馆
  if (selectedFavorite.value === 'favorite' && isLoggedIn.value) {
    venues = venues.filter((venue) => venue.isFavorite === true)
  }

  // 排序
  const sortedVenues = [...venues]
  switch (sortBy.value) {
    case 'price-asc':
      sortedVenues.sort((a, b) => a.price - b.price)
      break
    case 'price-desc':
      sortedVenues.sort((a, b) => b.price - a.price)
      break
    case 'rating-desc':
      sortedVenues.sort((a, b) => (b.averageRating || 0) - (a.averageRating || 0))
      break
    case 'rating-asc':
      sortedVenues.sort((a, b) => (a.averageRating || 0) - (b.averageRating || 0))
      break
    case 'name':
      sortedVenues.sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
      break
    default:
      break
  }

  return sortedVenues
})

const handleBook = (venueId: number) => {
  router.push(`/venues/${venueId}`)
}

const resetFilters = () => {
  searchKeyword.value = ''
  selectedType.value = ''
  selectedVenueType.value = ''
  selectedStatus.value = ''
  selectedFavorite.value = ''
  sortBy.value = 'default'
}

onMounted(() => {
  const userId = userStore.userInfo?.id
  venueStore.fetchVenues(userId)
})
</script>

<style scoped>
.page-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.page-title::before {
  content: '🏟️';
  font-size: 40px;
}

.venues-filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.search-box {
  grid-column: 1 / -1;
}

.results-info {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md) var(--spacing-lg);
  background: linear-gradient(135deg, var(--bg-color-page) 0%, var(--bg-color) 100%);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.results-stats {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.stat-item {
  font-size: 14px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item strong {
  color: var(--primary-color);
  font-weight: 600;
  font-size: 16px;
}

.stat-divider {
  color: var(--border-color);
  font-size: 12px;
}

@media (max-width: 768px) {
  .results-stats {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }

  .stat-divider {
    display: none;
  }
}

.filter-select {
  padding: 10px 15px;
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-color);
  cursor: pointer;
  outline: none;
  transition: border-color 0.3s;
  margin-top: 6px;
}

.filter-select:focus {
  border-color: var(--primary-color);
}

.filter-panel {
  background: #fff;
  border-radius: 20px;
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
  margin-bottom: var(--spacing-xl);
}

.filter-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.filter-panel-header h3 {
  margin: 0;
  font-size: 20px;
}

.filter-panel-header p {
  margin: 4px 0 0;
  color: var(--text-secondary);
}

.reset-btn {
  border: none;
  background: rgba(64, 158, 255, 0.1);
  color: var(--primary-color);
  padding: 8px 16px;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.reset-btn:hover {
  background: rgba(64, 158, 255, 0.2);
}

.filter-box label,
.sort-box label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  display: block;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
}

.quick-card {
  padding: var(--spacing-md);
  border-radius: 16px;
  background: linear-gradient(135deg, var(--bg-color-page) 0%, var(--bg-color) 100%);
  border: 1px solid var(--border-light);
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-sm);
}

.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.quick-card span {
  display: block;
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 6px;
  font-weight: 500;
}

.quick-card strong {
  font-size: 24px;
  color: var(--primary-color);
  font-weight: 700;
}

.loading,
.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

.venue-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-xl);
  padding: var(--spacing-md) 0;
}

@media (max-width: 768px) {
  .venue-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }
}

.venue-item-animate {
  animation: fadeInScale 0.5s ease-out both;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .venues-filters {
    flex-direction: column;
  }

  .search-box {
    min-width: 100%;
  }
}
</style>
