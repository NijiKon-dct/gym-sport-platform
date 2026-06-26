<template>
  <div class="venue-card" @click="handleCardClick">
    <div class="venue-card__image">
      <img v-if="venue.image" :src="getImageUrl(venue.image)" :alt="venue.name" />
      <div v-else class="venue-image-placeholder">暂无图片</div>
      <div v-if="venue.isFavorite" class="venue-card__favorite" @click.stop="handleFavoriteClick">❤️</div>
      <div v-if="venue.isInUse" class="venue-card__status in-use">使用中</div>
      <div v-else class="venue-card__status available">空闲</div>
    </div>
    <div class="venue-card__content">
      <h3 class="venue-card__title">{{ venue.name }}</h3>
      <p class="venue-card__address">📍 {{ venue.address }}</p>
      
      <!-- 评分显示 -->
      <div v-if="venue.averageRating !== undefined && venue.averageRating > 0" class="venue-card__rating">
        <div class="rating-stars">
          <span
            v-for="i in 5"
            :key="i"
            class="star"
            :class="{ filled: i <= Math.round(venue.averageRating || 0) }"
          >
            ★
          </span>
        </div>
        <span class="rating-score">{{ (venue.averageRating || 0).toFixed(1) }}</span>
        <span class="rating-count">({{ venue.reviewCount || 0 }}条评价)</span>
      </div>
      <div v-else class="venue-card__rating no-rating">
        <span class="no-rating-text">暂无评分</span>
      </div>

      <div class="venue-card__info">
        <span class="info-item">🏋️ {{ venue.type }}</span>
        <span class="info-item">👥 可容纳 {{ venue.capacity }} 人</span>
        <span v-if="venue.school" class="info-item school-badge">🏫 {{ venue.school }}</span>
        <span v-else class="info-item social-badge">🌍 社会场馆</span>
      </div>
      
      <div v-if="venue.openTime && venue.closeTime" class="venue-card__time">
        <span class="time-label">⏰ 开放时间:</span>
        <span class="time-value">{{ venue.openTime }} - {{ venue.closeTime }}</span>
      </div>

      <div class="venue-card__footer">
        <div class="venue-card__price">
          <span class="price-label">价格:</span>
          <span class="price-value">¥{{ venue.price }}/小时</span>
        </div>
        <BaseButton type="primary" size="small" @click.stop="handleBook"> 立即预约 </BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { BaseButton } from './common'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

interface Venue {
  id: number
  name: string
  address: string
  type: string
  capacity: number
  price: number
  image?: string
  isFavorite?: boolean
  school?: string
  isInUse?: boolean
  averageRating?: number
  reviewCount?: number
  openTime?: string
  closeTime?: string
}

interface Props {
  venue: Venue
}

const props = defineProps<Props>()

const emit = defineEmits<{
  book: [venueId: number]
}>()

const router = useRouter()
const userStore = useUserStore()

// 获取图片URL（用于显示）
const getImageUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  const encodedPath = path.split('/').map(segment => encodeURIComponent(segment)).join('/')
  return `${baseURL.replace('/api', '')}/api/files/${encodedPath}`
}

const handleCardClick = () => {
  router.push(`/venues/${props.venue.id}`)
}

const handleBook = () => {
  if (!userStore.isLoggedIn) {
    // 未登录时跳转到登录页面
    router.push({
      path: '/login',
      query: { redirect: `/venues/${props.venue.id}` },
    })
  } else {
    // 已登录时跳转到详情页
    router.push(`/venues/${props.venue.id}`)
  }
}

const handleFavoriteClick = () => {
  // 阻止事件冒泡，避免触发卡片点击
  // 这里可以添加收藏/取消收藏的逻辑
}
</script>

<style scoped>
.venue-card {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid var(--border-light);
  position: relative;
}

.venue-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--success-color));
  opacity: 0;
  transition: opacity 0.3s;
}

.venue-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-6px);
  border-color: var(--primary-color);
}

.venue-card:hover::before {
  opacity: 1;
}

.venue-card__image {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-color-page) 100%);
}

.venue-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.venue-card:hover .venue-card__image img {
  transform: scale(1.08);
}

.venue-card__favorite {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 24px;
  cursor: pointer;
  z-index: 10;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  transition: transform 0.2s;
}

.venue-card__favorite:hover {
  transform: scale(1.1);
}

.venue-card__content {
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  flex-grow: 1;
}

.venue-card__rating {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin: var(--spacing-xs) 0;
  padding: 6px 0;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.rating-stars .star {
  font-size: 14px;
  color: #ddd;
  line-height: 1;
}

.rating-stars .star.filled {
  color: #ffc107;
}

.rating-score {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.rating-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.no-rating {
  padding: 4px 0;
}

.no-rating-text {
  font-size: 12px;
  color: var(--text-secondary);
  font-style: italic;
}

.venue-card__time {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: var(--spacing-xs);
}

.time-label {
  font-weight: 500;
}

.time-value {
  color: var(--text-primary);
}

.venue-card__title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.4;
  height: 2.8em;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: color 0.3s;
}

.venue-card:hover .venue-card__title {
  color: var(--primary-color);
}

.venue-card__address {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.4;
  flex-shrink: 0;
}

.venue-card__info {
  display: flex;
  gap: var(--spacing-sm);
  margin: var(--spacing-sm) 0;
  flex-wrap: wrap;
  flex-shrink: 0;
  min-height: 20px;
}

.info-item {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
  padding: 4px 8px;
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-sm);
  border: 1px solid var(--border-light);
  transition: all 0.2s;
}

.info-item:hover {
  background-color: var(--bg-secondary);
  border-color: var(--primary-color);
  color: var(--text-primary);
}

.venue-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-light);
  gap: var(--spacing-sm);
}

.venue-card__price {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
  flex-shrink: 0;
}

.price-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.price-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
  background: linear-gradient(135deg, var(--primary-color), var(--success-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.school-badge {
  background-color: var(--primary-color);
  color: #fff;
  padding: 2px 8px;
  border-radius: var(--border-radius-sm);
  font-size: 11px;
}

.social-badge {
  background-color: var(--success-color);
  color: #fff;
  padding: 2px 8px;
  border-radius: var(--border-radius-sm);
  font-size: 11px;
}

.venue-card__status {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.venue-card__status.in-use {
  background-color: var(--danger-color);
}

.venue-card__status.available {
  background-color: var(--success-color);
}

.venue-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  font-size: 14px;
  min-height: 200px;
}
</style>
