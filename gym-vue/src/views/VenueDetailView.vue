<template>
  <div class="venue-detail-view page-container">
    <div class="container">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!venue" class="empty-state">
        <p>场馆不存在</p>
        <RouterLink to="/venues">
          <BaseButton type="primary">返回场馆列表</BaseButton>
        </RouterLink>
      </div>
      <div v-else class="venue-detail">
        <!-- 场馆信息 -->
        <div class="venue-info">
          <div class="venue-image-gallery">
            <div class="main-image">
              <img
                v-if="getImageUrl(venue.image)"
                :src="getImageUrl(venue.image)"
                :alt="venue.name"
                @click="showImageModal = true"
              />
              <div v-else class="venue-image-placeholder">暂无图片</div>
              <button class="view-more-btn" @click="showImageModal = true">📷 查看全部图片</button>
            </div>
            <div v-if="venueImages.length > 1" class="thumbnail-list">
              <div
                v-for="(img, index) in venueImages.slice(0, 4)"
                :key="index"
                class="thumbnail"
                :class="{ active: index === 0 }"
                @click="currentImageIndex = index"
              >
                <img :src="img" :alt="`${venue.name} ${index + 1}`" />
              </div>
            </div>
          </div>
          <div class="venue-content">
            <div class="venue-header">
              <div>
                <h1 class="venue-name">{{ venue.name }}</h1>
                <p class="venue-address">📍 {{ venue.address }}</p>
              </div>
              <div v-if="userStore.isLoggedIn" class="venue-actions">
                <button
                  class="action-btn"
                  :class="{ active: venue.isFavorite }"
                  @click="handleToggleFavorite"
                  :title="venue.isFavorite ? '取消收藏' : '收藏'"
                >
                  <span v-if="venue.isFavorite">❤️</span>
                  <span v-else>🤍</span>
                  <span>{{ venue.isFavorite ? '已收藏' : '收藏' }}</span>
                </button>
              </div>
            </div>
            <div class="venue-meta">
              <div class="meta-item">
                <span class="meta-label">类型:</span>
                <span class="meta-value">{{ venue.type }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">容纳人数:</span>
                <span class="meta-value">{{ venue.capacity }} 人</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">开放时间:</span>
                <span class="meta-value">{{ venue.openTime }} - {{ venue.closeTime }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">价格:</span>
                <span class="meta-value price">¥{{ venue.price }}/小时</span>
              </div>
            </div>
            <div v-if="venue.description" class="venue-description">
              <h3>场馆介绍</h3>
              <p>{{ venue.description }}</p>
            </div>

            <!-- 预约表单 -->
            <div class="booking-section">
              <BookingForm
                :venue-id="venue.id"
                :open-time="venue.openTime"
                :close-time="venue.closeTime"
                @submit="handleBooking"
                @cancel="handleCancel"
              />
            </div>

            <!-- 评价区域 -->
            <div class="venue-reviews">
              <div class="reviews-header">
                <h3>用户评价</h3>
                <div v-if="reviewStats" class="reviews-summary">
                  <div class="rating-score">
                    <span class="score">{{ reviewStats.averageRating.toFixed(1) }}</span>
                    <div class="stars">
                      <span v-for="i in 5" :key="i" :class="{ filled: i <= Math.round(reviewStats.averageRating) }">
                        ★
                      </span>
                    </div>
                    <span class="review-count">({{ reviewStats.reviewCount }}条评价)</span>
                  </div>
                </div>
              </div>

              <!-- 发表评价按钮 -->
              <div v-if="userStore.isLoggedIn" class="review-action-section">
                <BaseButton type="primary" @click="showReviewModal = true">
                  ✍️ 发表评价
                </BaseButton>
              </div>

              <!-- 评价列表 -->
              <div v-if="reviews.length === 0" class="empty-reviews">暂无评价</div>
              <div v-else class="reviews-list">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <div class="reviewer-avatar">
                      <img v-if="review.userAvatar" :src="getImageUrl(review.userAvatar)" :alt="review.username" />
                      <span v-else>{{ review.username?.[0] || '访' }}</span>
                    </div>
                    <div class="reviewer-info">
                      <div class="reviewer-name">{{ review.username || '访客' }}</div>
                      <div class="review-rating">
                        <span
                          v-for="i in 5"
                          :key="i"
                          class="star"
                          :class="{ filled: i <= review.rating }"
                        >
                          ★
                        </span>
                      </div>
                      <div class="review-time">{{ formatTime(review.createdAt) }}</div>
                    </div>
                    <div v-if="userStore.userInfo?.id === review.userId" class="review-actions">
                      <button class="action-link" @click="handleEditReview(review)">编辑</button>
                      <button class="action-link" @click="handleDeleteReview(review.id)">删除</button>
                    </div>
                  </div>
                  <div v-if="review.content" class="review-content">{{ review.content }}</div>
                  <div v-if="review.imagePaths && review.imagePaths.length > 0" class="review-images">
                    <div
                      v-for="(imgPath, index) in review.imagePaths"
                      :key="index"
                      class="review-image-item"
                      @click="showImagePreview(getImageUrl(imgPath))"
                    >
                      <img :src="getImageUrl(imgPath)" :alt="`评价图片${index + 1}`" />
                    </div>
                  </div>
                  <div v-if="review.videoPaths && review.videoPaths.length > 0" class="review-videos">
                    <div v-for="(videoPath, index) in review.videoPaths" :key="index" class="review-video-item">
                      <video :src="getImageUrl(videoPath)" controls></video>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片查看弹窗 -->
    <div v-if="showImageModal" class="image-modal-overlay" @click.self="showImageModal = false">
      <div class="image-modal-content">
        <button class="modal-close-btn" @click="showImageModal = false">✕</button>
        <img
          v-if="venue"
          :src="venueImages[currentImageIndex]"
          :alt="venue.name"
          class="modal-image"
        />
        <div v-if="venueImages.length > 1" class="image-nav">
          <button class="nav-btn" :disabled="currentImageIndex === 0" @click="currentImageIndex--">
            ‹
          </button>
          <span class="image-counter">{{ currentImageIndex + 1 }} / {{ venueImages.length }}</span>
          <button
            class="nav-btn"
            :disabled="currentImageIndex === venueImages.length - 1"
            @click="currentImageIndex++"
          >
            ›
          </button>
        </div>
      </div>
    </div>

    <!-- 发表评价弹窗 -->
    <BaseModal
      :visible="showReviewModal"
      :title="editingReview ? '编辑评价' : '发表评价'"
      @close="handleCloseReviewModal"
    >
      <div class="review-form">
        <div class="rating-input">
          <label>评分：</label>
          <div class="star-rating">
            <span
              v-for="i in 5"
              :key="i"
              class="star"
              :class="{ active: i <= newReview.rating }"
              @click="newReview.rating = i"
            >
              ★
            </span>
          </div>
        </div>
        <div class="content-input">
          <textarea
            v-model="newReview.content"
            placeholder="写下您的评价..."
            rows="4"
            maxlength="1000"
          ></textarea>
        </div>
        <div class="media-upload">
          <div class="upload-section">
            <label>上传图片（最多9张）</label>
            <div class="upload-area">
              <input
                ref="imageInput"
                type="file"
                accept="image/*"
                multiple
                @change="handleImageSelect"
                style="display: none"
              />
              <button type="button" class="upload-btn" @click="imageInput?.click()">
                📷 选择图片
              </button>
              <div v-if="newReview.imageFiles.length > 0" class="preview-images">
                <div v-for="(file, index) in newReview.imageFiles" :key="index" class="preview-item">
                  <img :src="getPreviewUrl(file)" alt="预览" />
                  <button class="remove-btn" @click="removeImage(index)">×</button>
                </div>
              </div>
            </div>
          </div>
          <div class="upload-section">
            <label>上传视频（最多1个）</label>
            <div class="upload-area">
              <input
                ref="videoInput"
                type="file"
                accept="video/*"
                @change="handleVideoSelect"
                style="display: none"
              />
              <button type="button" class="upload-btn" @click="videoInput?.click()">
                🎥 选择视频
              </button>
              <div v-if="newReview.videoFile" class="preview-video">
                <video :src="getPreviewUrl(newReview.videoFile)" controls></video>
                <button class="remove-btn" @click="removeVideo">×</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <BaseButton @click="handleCloseReviewModal">取消</BaseButton>
        <BaseButton type="primary" @click="handleSubmitReview" :disabled="submittingReview">
          {{ submittingReview ? '提交中...' : editingReview ? '更新评价' : '提交评价' }}
        </BaseButton>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useVenueStore, type VenueReview } from '@/stores/venue'
import { useUserStore } from '@/stores/user'
import { BaseButton, BaseModal } from '@/components/common'
import BookingForm from '@/components/BookingForm.vue'

const route = useRoute()
const router = useRouter()
const venueStore = useVenueStore()

const loading = computed(() => venueStore.loading)
const venue = computed(() => venueStore.currentVenue)
const showImageModal = ref(false)
const showReviewModal = ref(false)
const currentImageIndex = ref(0)
const reviews = computed(() => venueStore.reviews)
const reviewStats = computed(() => venueStore.reviewStats)
const submittingReview = ref(false)

const newReview = ref({
  rating: 0,
  content: '',
  imageFiles: [] as File[],
  videoFile: null as File | null,
  imagePaths: [] as string[],
  videoPaths: [] as string[],
})

const editingReview = ref<VenueReview | null>(null)
const imageInput = ref<HTMLInputElement | null>(null)
const videoInput = ref<HTMLInputElement | null>(null)

// 获取图片URL（用于显示）
const getImageUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

// 场馆图片列表
const venueImages = computed(() => {
  if (!venue.value) return []
  const imageUrl = getImageUrl(venue.value.image)
  if (imageUrl) {
    return [imageUrl]
  }
  return []
})

const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / 86400000)

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

interface BookingFormData {
  date: string
  startTime: string
  endTime: string
  remark: string
}

const userStore = useUserStore()

const handleBooking = async (bookingData: BookingFormData) => {
  if (!venue.value) return

  try {
    await venueStore.makeBooking({
      venueId: venue.value.id,
      date: bookingData.date,
      startTime: bookingData.startTime,
      endTime: bookingData.endTime,
      remark: bookingData.remark,
      userId: userStore.userInfo?.id || 1,
    })
    alert('预约成功！')
    router.push('/bookings')
  } catch (error) {
    console.error('Booking failed:', error)
    const msg = error instanceof Error ? error.message : '预约失败，请重试'
    alert(msg)
  }
}

const handleCancel = () => {
  router.back()
}

const handleToggleFavorite = async () => {
  if (!venue.value || !userStore.userInfo) return
  try {
    await venueStore.toggleFavorite(venue.value.id, userStore.userInfo.id)
    // 重新获取场馆信息以更新状态
    await venueStore.fetchVenueById(venue.value.id, userStore.userInfo.id)
  } catch (error) {
    console.error('Toggle favorite failed:', error)
    alert('操作失败，请重试')
  }
}

const handleImageSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files) {
    const files = Array.from(target.files).slice(0, 9 - newReview.value.imageFiles.length)
    newReview.value.imageFiles.push(...files)
  }
}

const handleVideoSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    newReview.value.videoFile = target.files[0]
  }
}

const removeImage = (index: number) => {
  newReview.value.imageFiles.splice(index, 1)
}

const removeVideo = () => {
  newReview.value.videoFile = null
}

const getPreviewUrl = (file: File) => {
  return URL.createObjectURL(file)
}

const resetReviewForm = () => {
  newReview.value = {
    rating: 0,
    content: '',
    imageFiles: [],
    videoFile: null,
    imagePaths: [],
    videoPaths: [],
  }
  editingReview.value = null
}

const handleCloseReviewModal = () => {
  showReviewModal.value = false
  resetReviewForm()
}

const handleSubmitReview = async () => {
  if (!venue.value || !userStore.userInfo) return
  if (newReview.value.rating === 0) {
    alert('请选择评分')
    return
  }

  submittingReview.value = true
  try {
    // 上传图片
    const imagePaths: string[] = []
    if (newReview.value.imageFiles.length > 0) {
      const imageResults = await venueStore.uploadBookingPictures(newReview.value.imageFiles)
      imagePaths.push(...imageResults.paths)
    }

    // 上传视频
    const videoPaths: string[] = []
    if (newReview.value.videoFile) {
      const videoResult = await venueStore.uploadBookingVideo(newReview.value.videoFile)
      videoPaths.push(videoResult.path)
    }

    const reviewData = {
      rating: newReview.value.rating,
      content: newReview.value.content || undefined,
      imagePaths: imagePaths.length > 0 ? imagePaths : undefined,
      videoPaths: videoPaths.length > 0 ? videoPaths : undefined,
    }

    if (editingReview.value) {
      // 更新评论
      await venueStore.updateReview(
        venue.value.id,
        editingReview.value.id,
        userStore.userInfo.id,
        reviewData
      )
    } else {
      // 创建评论
      await venueStore.createReview(venue.value.id, userStore.userInfo.id, reviewData)
    }

    resetReviewForm()
    showReviewModal.value = false
    alert(editingReview.value ? '评价更新成功！' : '评价提交成功！')
    // 重新加载评论列表
    await venueStore.fetchReviews(venue.value.id)
    await venueStore.fetchReviewStats(venue.value.id)
  } catch (error: any) {
    console.error('提交评价失败:', error)
    alert(error.message || '提交失败，请重试')
  } finally {
    submittingReview.value = false
  }
}

const handleEditReview = (review: VenueReview) => {
  editingReview.value = review
  newReview.value = {
    rating: review.rating,
    content: review.content || '',
    imageFiles: [],
    videoFile: null,
    imagePaths: review.imagePaths || [],
    videoPaths: review.videoPaths || [],
  }
  showReviewModal.value = true
}

const handleDeleteReview = async (reviewId: number) => {
  if (!venue.value || !userStore.userInfo) return
  if (!confirm('确定要删除这条评价吗？')) return

  try {
    await venueStore.deleteReview(venue.value.id, reviewId, userStore.userInfo.id)
    alert('删除成功！')
  } catch (error: any) {
    console.error('删除评价失败:', error)
    alert(error.message || '删除失败，请重试')
  }
}

const showImagePreview = (imageUrl: string) => {
  // 可以在这里实现图片预览功能
  window.open(imageUrl, '_blank')
}

onMounted(async () => {
  const venueId = Number(route.params.id)
  if (venueId) {
    const userId = userStore.userInfo?.id
    await venueStore.fetchVenueById(venueId, userId)
    await venueStore.fetchReviews(venueId)
    await venueStore.fetchReviewStats(venueId)
  }
})
</script>

<style scoped>
.loading,
.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.venue-detail {
  display: grid;
  gap: var(--spacing-xl);
}

.venue-info {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.venue-image-gallery {
  position: relative;
}

.main-image {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: hidden;
  cursor: pointer;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.main-image:hover img {
  transform: scale(1.05);
}

.view-more-btn {
  position: absolute;
  bottom: var(--spacing-md);
  right: var(--spacing-md);
  padding: 8px 16px;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.view-more-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.thumbnail-list {
  display: flex;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
  opacity: 0.7;
}

.thumbnail:hover,
.thumbnail.active {
  opacity: 1;
  border-color: var(--primary-color);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.venue-content {
  padding: var(--spacing-lg);
}

.venue-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.venue-actions {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  background-color: var(--bg-color);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.action-btn:hover {
  background-color: var(--bg-color-page);
  border-color: var(--primary-color);
}

.action-btn.active {
  background-color: var(--primary-color);
  color: #fff;
  border-color: var(--primary-color);
}

.action-btn span:first-child {
  font-size: 16px;
}

.venue-name {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.venue-address {
  font-size: 16px;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-lg);
}

.venue-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.meta-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.meta-value {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.meta-value.price {
  font-size: 18px;
  color: var(--primary-color);
  font-weight: 600;
}

.venue-description {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-lighter);
}

.venue-description h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.venue-description p {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.8;
}

.booking-section {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--border-lighter);
}

.venue-reviews {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--border-lighter);
}

.venue-reviews h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.reviews-summary {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
}

.rating-score {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.score {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-color);
}

.stars {
  color: #ffc107;
  font-size: 18px;
}

.review-count {
  font-size: 14px;
  color: var(--text-secondary);
  margin-left: auto;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.empty-reviews {
  color: var(--text-secondary);
  background-color: var(--bg-color-page);
  border: 1px solid var(--border-lighter);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  text-align: center;
}

.review-item {
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
}

.review-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.reviewer-info {
  flex: 1;
}

.reviewer-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.review-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.review-rating {
  color: #ffc107;
  font-size: 14px;
}

.review-rating .star {
  color: #ddd;
}

.review-rating .star.filled {
  color: #ffc107;
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-lg);
}

.reviews-summary {
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
}

.rating-score {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.score {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-color);
}

.stars {
  display: flex;
  gap: 2px;
}

.stars .filled {
  color: #ffc107;
}

.stars span {
  color: #ddd;
  font-size: 18px;
}

.review-count {
  font-size: 14px;
  color: var(--text-secondary);
  margin-left: var(--spacing-sm);
}

.review-action-section {
  margin-bottom: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

.review-form-section {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
}

.review-form-section h4 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.rating-input {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.rating-input label {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.star-rating {
  display: flex;
  gap: 4px;
}

.star-rating .star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.star-rating .star.active {
  color: #ffc107;
}

.star-rating .star:hover {
  color: #ffc107;
}

.content-input textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
}

.content-input textarea:focus {
  outline: none;
  border-color: var(--primary-color);
}

.media-upload {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.upload-section label {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.upload-area {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.upload-btn {
  padding: 8px 16px;
  border: 1px dashed var(--border-color);
  border-radius: var(--border-radius-md);
  background-color: var(--bg-color);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.upload-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.preview-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: var(--spacing-sm);
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

.remove-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.preview-video {
  position: relative;
  max-width: 300px;
}

.preview-video video {
  width: 100%;
  border-radius: var(--border-radius-md);
}

.form-actions {
  display: flex;
  gap: var(--spacing-md);
}

.review-item {
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
  margin-bottom: var(--spacing-md);
}

.review-header {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
  overflow: hidden;
}

.reviewer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reviewer-info {
  flex: 1;
}

.review-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.action-link {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 12px;
  padding: 0;
  text-decoration: underline;
}

.action-link:hover {
  color: var(--primary-color);
}

.review-content {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
  margin-top: var(--spacing-sm);
}

.review-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
}

.review-image-item {
  width: 100%;
  aspect-ratio: 1;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
}

.review-image-item:hover {
  transform: scale(1.05);
}

.review-image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.review-videos {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
}

.review-video-item {
  max-width: 500px;
}

.review-video-item video {
  width: 100%;
  border-radius: var(--border-radius-md);
}

.review-content {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
}

/* 图片查看弹窗 */
.image-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3000;
  padding: var(--spacing-lg);
}

.image-modal-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.modal-close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  width: 36px;
  height: 36px;
  border: none;
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 1;
}

.modal-close-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.modal-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border-radius: var(--border-radius-md);
}

.image-nav {
  position: absolute;
  bottom: -50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.nav-btn {
  width: 40px;
  height: 40px;
  border: none;
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-btn:hover:not(:disabled) {
  background-color: rgba(255, 255, 255, 0.3);
}

.nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.image-counter {
  color: #fff;
  font-size: 14px;
  padding: 0 var(--spacing-md);
}


@media (max-width: 768px) {
  .main-image {
    height: 250px;
  }

  .thumbnail-list {
    overflow-x: auto;
  }

  .thumbnail {
    flex-shrink: 0;
  }

  .venue-header {
    flex-direction: column;
  }

  .venue-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .action-btn {
    flex: 1;
    justify-content: center;
  }

  .venue-name {
    font-size: 24px;
  }

  .venue-meta {
    grid-template-columns: 1fr;
  }

  .venue-image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--bg-secondary);
    color: var(--text-secondary);
    font-size: 16px;
    min-height: 250px;
  }
}
</style>
