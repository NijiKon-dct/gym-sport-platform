<template>
  <div class="topic-view page-container">
    <div class="container">
      <div class="topic-header">
        <h1 class="topic-title">
          <span v-if="currentTopic">#{{ currentTopic.name }}</span>
          <span v-else>话题</span>
        </h1>
        <div v-if="currentTopic" class="topic-info">
          <div class="topic-stats">
            <span class="stat-item">
              <span class="stat-value">{{ currentTopic.postCount }}</span>
              <span class="stat-label">动态</span>
            </span>
            <span class="stat-item">
              <span class="stat-value">{{ currentTopic.subscriberCount }}</span>
              <span class="stat-label">订阅</span>
            </span>
          </div>
          <div v-if="currentTopic.description" class="topic-description">
            {{ currentTopic.description }}
          </div>
          <BaseButton
            v-if="userInfo"
            :type="currentTopic.isSubscribed ? 'secondary' : 'primary'"
            @click="handleToggleSubscribe"
          >
            {{ currentTopic.isSubscribed ? '✓ 已订阅' : '+ 订阅' }}
          </BaseButton>
        </div>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="posts.length === 0" class="empty-state">
        <p>该话题下暂无动态</p>
      </div>
      <div v-else class="posts-list">
        <div v-for="post in posts" :key="post.id" class="post-card">
          <div class="post-header">
            <div class="post-author">
              <div class="author-avatar">
                <img
                  v-if="post.avatar"
                  :src="getImageUrl(post.avatar)"
                  alt="头像"
                  class="avatar-img"
                />
                <span v-else>{{ post.username[0] }}</span>
              </div>
              <div class="author-info">
                <div class="author-name">{{ post.username }}</div>
                <div class="post-time">{{ formatTime(post.createdAt) }}</div>
              </div>
            </div>
          </div>
          <div class="post-content">
            <p v-html="formatContentWithTopics(post.content)"></p>
            <div v-if="post.topics && post.topics.length > 0" class="post-topics">
              <router-link
                v-for="topic in post.topics"
                :key="topic"
                :to="`/topics/${encodeURIComponent(topic)}`"
                class="topic-tag"
              >
                #{{ topic }}
              </router-link>
            </div>
            <div v-if="post.venueName" class="post-venue">
              📍 {{ post.venueName }}
            </div>
            <div v-if="post.images && post.images.length > 0" class="post-images">
              <img
                v-for="(img, index) in post.images"
                :key="index"
                :src="getImageUrl(img)"
                alt="动态图片"
                class="post-image"
                @click="previewImage(getImageUrl(img))"
              />
            </div>
            <div v-if="post.video" class="post-video">
              <video
                :src="getVideoUrl(post.video)"
                controls
                class="post-video-player"
              ></video>
            </div>
          </div>
          <div class="post-actions">
            <button
              :class="['action-btn', { 'action-btn--active': post.isLiked }]"
              @click="handleLike(post.id)"
            >
              ❤️ {{ post.likes }}
            </button>
            <button class="action-btn" @click="toggleComments(post.id)">
              💬 {{ post.comments }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <div v-if="previewImageUrl" class="image-preview-overlay" @click="previewImageUrl = ''">
      <div class="image-preview-content" @click.stop>
        <button class="image-preview-close" @click="previewImageUrl = ''">✕</button>
        <img :src="previewImageUrl" alt="预览图片" class="image-preview-img" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSocialStore } from '@/stores/social'
import { useTopicStore } from '@/stores/topic'
import { useUserStore } from '@/stores/user'
import { BaseButton } from '@/components/common'

const route = useRoute()
const router = useRouter()
const socialStore = useSocialStore()
const topicStore = useTopicStore()
const userStore = useUserStore()

const posts = computed(() => socialStore.posts)
const loading = computed(() => socialStore.loading)
const currentTopic = computed(() => topicStore.currentTopic)
const userInfo = computed(() => userStore.userInfo)
const previewImageUrl = ref('')
const showComments = ref<Record<number, boolean>>({})

const topicName = computed(() => {
  const name = route.params.name as string
  return name ? decodeURIComponent(name) : null
})

// 格式化内容，将话题标签转换为可点击的链接
const formatContentWithTopics = (content: string) => {
  if (!content) return ''
  return content.replace(/#([^\s#]+)/g, (match, topicName) => {
    const encodedTopic = encodeURIComponent(topicName)
    return `<a href="/topics/${encodedTopic}" class="topic-link">${match}</a>`
  })
}

const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const getImageUrl = (path: string) => {
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

const getVideoUrl = (path: string) => {
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

const previewImage = (url: string) => {
  previewImageUrl.value = url
}

const handleLike = async (postId: number) => {
  await socialStore.toggleLike(postId)
}

const toggleComments = (postId: number) => {
  showComments.value[postId] = !showComments.value[postId]
}

const handleToggleSubscribe = async () => {
  if (!userInfo.value?.id) {
    alert('请先登录')
    router.push('/login')
    return
  }
  if (!currentTopic.value) return

  try {
    if (currentTopic.value.isSubscribed) {
      await topicStore.unsubscribeTopic(currentTopic.value.id, userInfo.value.id)
    } else {
      await topicStore.subscribeTopic(currentTopic.value.id, userInfo.value.id)
    }
  } catch (error) {
    alert('操作失败，请稍后重试')
  }
}

const loadTopic = async () => {
  if (!topicName.value) return
  await topicStore.fetchTopicByName(topicName.value)
  if (currentTopic.value) {
    await socialStore.fetchPosts(currentTopic.value.id)
  }
}

watch(
  () => route.params.name,
  () => {
    loadTopic()
  },
  { immediate: true },
)

onMounted(() => {
  loadTopic()
})
</script>

<style scoped>
.topic-view {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: calc(100vh - 200px);
  padding: var(--spacing-xl) 0;
}

.topic-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: var(--spacing-xl);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: var(--spacing-xl);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.topic-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.topic-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.topic-stats {
  display: flex;
  gap: var(--spacing-lg);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.topic-description {
  color: var(--text-regular);
  line-height: 1.6;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.post-card {
  padding: var(--spacing-xl);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.98);
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.post-card:hover {
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}

.post-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  overflow: hidden;
  flex-shrink: 0;
}

.author-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
}

.post-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.post-content {
  margin-bottom: var(--spacing-md);
}

.post-content p {
  color: var(--text-regular);
  line-height: 1.6;
  margin-bottom: var(--spacing-sm);
}

.post-content .topic-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
}

.post-content .topic-link:hover {
  text-decoration: underline;
  opacity: 0.8;
}

.post-topics {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.topic-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(64, 158, 255, 0.15) 100%);
  color: var(--primary-color);
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.topic-tag:hover {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2) 0%, rgba(64, 158, 255, 0.25) 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.post-venue {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: var(--spacing-sm);
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
}

.post-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: var(--border-radius-md);
  cursor: pointer;
}

.post-video {
  margin-top: var(--spacing-md);
}

.post-video-player {
  width: 100%;
  max-height: 500px;
  border-radius: var(--border-radius-md);
  background-color: #000;
}

.post-actions {
  display: flex;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.action-btn {
  padding: 10px 18px;
  border: none;
  background: rgba(0, 0, 0, 0.03);
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(64, 158, 255, 0.1);
  color: var(--primary-color);
}

.action-btn--active {
  color: #ff4757;
  background: rgba(255, 71, 87, 0.1);
}

.loading,
.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

.image-preview-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  cursor: zoom-out;
}

.image-preview-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  cursor: default;
}

.image-preview-img {
  max-width: 100%;
  max-height: 80vh;
  border-radius: var(--border-radius-md);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.4);
}

.image-preview-close {
  position: absolute;
  top: -40px;
  right: 0;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #ffffff;
  font-size: 22px;
  cursor: pointer;
}
</style>

