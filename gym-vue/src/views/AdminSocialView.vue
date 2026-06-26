<template>
  <div class="admin-social page-container">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">社交动态管理</h1>
      </div>

      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon">📝</div>
          <div>
            <div class="stat-value">{{ totalPosts }}</div>
            <div class="stat-label">总动态数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">❤️</div>
          <div>
            <div class="stat-value">{{ totalLikes }}</div>
            <div class="stat-label">累计点赞</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💬</div>
          <div>
            <div class="stat-value">{{ totalComments }}</div>
            <div class="stat-label">累计评论</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📷</div>
          <div>
            <div class="stat-value">{{ imagePostsCount }}</div>
            <div class="stat-label">图片视频</div>
          </div>
        </div>
      </div>

      <div class="insights-grid">
        <div class="insight-card">
          <div class="section-header">
            <h2>互动组成</h2>
            <span class="section-tip">平均点赞 {{ averageLikes }} · 评论 {{ averageComments }}</span>
          </div>
          <div class="engagement-chart">
            <div class="pie-chart" :style="engagementPieStyle">
              <div class="pie-center">
                <div class="pie-number">{{ totalEngagement }}</div>
                <div class="pie-label">总互动</div>
              </div>
            </div>
            <ul class="engagement-legend">
              <li v-for="item in engagementStats" :key="item.label">
                <span class="legend-dot" :style="{ backgroundColor: item.color }"></span>
                <span class="legend-name">{{ item.label }}</span>
                <span class="legend-count">{{ item.count }}</span>
                <div class="legend-bar">
                  <div class="legend-fill" :style="{ width: item.percentage + '%', backgroundColor: item.color }"></div>
                </div>
                <span class="legend-percentage">{{ item.percentage }}%</span>
              </li>
            </ul>
          </div>
        </div>

        <div class="insight-card">
          <div class="section-header">
            <h2>热门场馆 / 话题</h2>
            <span class="section-tip">关联场馆 {{ venuePostsCount }} 条</span>
          </div>
          <ul class="venue-list">
            <li v-for="(venue, index) in topVenues" :key="venue.name">
              <div class="venue-info">
                <span class="venue-rank">#{{ index + 1 }}</span>
                <div>
                  <div class="venue-name">{{ venue.name }}</div>
                  <div class="venue-count">{{ venue.count }} 条动态 · {{ venue.percentage }}%</div>
                </div>
              </div>
              <div class="venue-bar">
                <div class="venue-fill" :style="{ width: venue.percentage + '%'}"></div>
              </div>
            </li>
            <li v-if="topVenues.length === 0" class="empty-legend">暂无关联场馆数据</li>
          </ul>
        </div>

        <div class="insight-card">
          <div class="section-header">
            <h2>活跃用户</h2>
            <span class="section-tip">按发布数量排序</span>
          </div>
          <ul class="user-list">
            <li v-for="user in activeUsers" :key="user.username">
              <div>
                <div class="user-name">{{ user.username }}</div>
                <div class="user-meta">{{ user.postCount }} 条动态 · {{ user.totalLikes }} 赞</div>
              </div>
              <span class="user-badge">🔥 {{ user.score }}</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-filters">
        <BaseInput
          v-model="searchQuery"
          placeholder="搜索动态内容或用户名称"
        />
        <BaseSelect v-model="filterBy">
          <option value="">全部动态</option>
          <option value="has_images">含图片</option>
          <option value="has_comments">有评论</option>
          <option value="has_likes">有点赞</option>
        </BaseSelect>
        <BaseButton type="secondary" @click="refreshPosts">刷新</BaseButton>
      </div>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedPosts.length > 0">
        <span>已选择 {{ selectedPosts.length }} 条动态</span>
        <BaseButton type="danger" @click="batchDeletePosts">批量删除</BaseButton>
        <BaseButton type="secondary" @click="selectedPosts = []">取消选择</BaseButton>
      </div>

      <!-- 动态列表 -->
      <div class="posts-list-container">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="filteredPosts.length === 0" class="empty-state">暂无符合条件的动态</div>
        <div v-else class="posts-grid">
          <div v-for="post in paginatedPosts" :key="post.id" class="post-card">
            <!-- 选择框 -->
            <div class="post-selector">
              <input
                type="checkbox"
                :checked="selectedPosts.includes(post.id)"
                @change="toggleSelectPost(post.id)"
                class="post-checkbox"
              />
            </div>

            <!-- 动态头部 -->
            <div class="post-header">
              <div class="post-author">
                <div class="author-avatar">
                  <img
                    v-if="post.avatar"
                    :src="getImageUrl(post.avatar)"
                    alt="用户头像"
                    class="avatar-img"
                  />
                  <span v-else class="avatar-placeholder">
                    {{ post.username[0] || '👤' }}
                  </span>
                </div>
                <div class="author-info">
                  <div class="author-name">{{ post.username }}</div>
                  <div class="post-time">{{ formatTime(post.createdAt) }}</div>
                  <div v-if="post.venueName" class="post-venue">📍 {{ post.venueName }}</div>
                </div>
              </div>
              <div class="post-actions">
                <div class="post-badges-admin">
                  <span v-if="post.isPinned" class="badge badge-pinned-admin">📌 置顶</span>
                  <span v-if="post.isFeatured" class="badge badge-featured-admin">⭐ 精华</span>
                  <span v-if="post.isHot" class="badge badge-hot-admin">🔥 热门</span>
                </div>
                <div class="action-buttons">
                  <BaseButton
                    :type="post.isPinned ? 'warning' : 'secondary'"
                    size="small"
                    @click="togglePin(post.id, !post.isPinned)"
                  >
                    {{ post.isPinned ? '取消置顶' : '设为置顶' }}
                  </BaseButton>
                  <BaseButton
                    :type="post.isFeatured ? 'warning' : 'secondary'"
                    size="small"
                    @click="toggleFeatured(post.id, !post.isFeatured)"
                  >
                    {{ post.isFeatured ? '取消精华' : '设为精华' }}
                  </BaseButton>
                  <BaseButton type="secondary" size="small" @click="viewComments(post)">查看评论</BaseButton>
                  <BaseButton type="danger" size="small" @click="deletePost(post.id)">删除</BaseButton>
                </div>
              </div>
            </div>

            <!-- 动态内容 -->
            <div class="post-content">
              <p class="post-text">{{ post.content }}</p>
              <div v-if="post.images && post.images.length > 0" class="post-images">
                <img
                  v-for="(img, index) in post.images.slice(0, 4)"
                  :key="index"
                  :src="getImageUrl(img)"
                  alt="动态图片"
                  class="post-image"
                  @click="previewImage(getImageUrl(img))"
                />
                <div v-if="post.images.length > 4" class="more-images">
                  +{{ post.images.length - 4 }}
                </div>
              </div>
              <div v-if="post.video" class="post-video">
                <video
                  :src="getVideoUrl(post.video)"
                  controls
                  class="post-video-player"
                ></video>
              </div>
            </div>

            <!-- 动态统计 -->
            <div class="post-stats">
              <span class="stat-item">❤️ {{ post.likes }} 赞</span>
              <span class="stat-item">💬 {{ post.comments }} 评论</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="!loading && filteredPosts.length > 0">
        <BaseButton type="secondary" @click="currentPage = 1" :disabled="currentPage === 1">
          首页
        </BaseButton>
        <BaseButton type="secondary" @click="currentPage--" :disabled="currentPage === 1">
          上一页
        </BaseButton>
        <span class="page-info"> 第 {{ currentPage }} 页，共 {{ totalPages }} 页 </span>
        <BaseButton type="secondary" @click="currentPage++" :disabled="currentPage === totalPages">
          下一页
        </BaseButton>
        <BaseButton
          type="secondary"
          @click="currentPage = totalPages"
          :disabled="currentPage === totalPages"
        >
          末页
        </BaseButton>
      </div>

      <!-- 评论弹窗 -->
      <BaseModal
        :visible="showCommentsModal"
        :title="`评论 - ${currentPost?.username || ''}`"
        @close="closeCommentsModal"
      >
        <div v-if="currentPostComments.length === 0" class="no-comments">暂无评论</div>
        <div v-else class="comments-list">
          <div v-for="comment in currentPostComments" :key="comment.id" class="comment-item">
            <div class="comment-avatar">
              <span>{{ comment.username[0] || '👤' }}</span>
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.username }}</span>
                <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
            </div>
            <BaseButton type="danger" size="small" @click="deleteComment(comment.id)">
              删除
            </BaseButton>
          </div>
        </div>
      </BaseModal>

      <!-- 图片预览 -->
      <div v-if="previewImageUrl" class="image-preview-overlay" @click="previewImageUrl = ''">
        <div class="image-preview-content" @click.stop>
          <button class="close-btn" @click="previewImageUrl = ''">✕</button>
          <img :src="previewImageUrl" alt="预览图片" class="preview-image" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useSocialStore } from '@/stores/social'
import { BaseButton, BaseInput, BaseModal, BaseSelect } from '@/components/common'

const socialStore = useSocialStore()
const chartColors = ['#f56c6c', '#409eff', '#67c23a', '#e6a23c']

// 状态管理
const loading = ref(false)
const searchQuery = ref('')
const filterBy = ref('')
const selectedPosts = ref<number[]>([])
const currentPage = ref(1)
const pageSize = 10

// 弹窗状态
const showCommentsModal = ref(false)
const currentPost = ref<any | null>(null)
const currentPostComments = ref<any[]>([])
const previewImageUrl = ref('')

// 获取动态列表
const posts = computed(() => socialStore.posts)

const totalPosts = computed(() => posts.value.length)

// 计算总评论数
const totalComments = computed(() => {
  return posts.value.reduce((sum, post) => sum + post.comments, 0)
})

// 计算总点赞数
const totalLikes = computed(() => {
  return posts.value.reduce((sum, post) => sum + post.likes, 0)
})

// 统计包含图片或视频的动态数量（有任意一种即计数）
const imagePostsCount = computed(() =>
  posts.value.filter((post) => (post.images && post.images.length > 0) || post.video).length,
)
const venuePostsCount = computed(() => posts.value.filter((post) => post.venueName).length)

const averageLikes = computed(() => {
  if (!totalPosts.value) return 0
  return Math.round(totalLikes.value / totalPosts.value)
})

const averageComments = computed(() => {
  if (!totalPosts.value) return 0
  return Math.round(totalComments.value / totalPosts.value)
})

const totalEngagement = computed(() => totalLikes.value + totalComments.value)

const engagementStats = computed(() => {
  const data = [
    { label: '点赞', count: totalLikes.value, color: chartColors[0] },
    { label: '评论', count: totalComments.value, color: chartColors[1] },
  ]
  return data.map((item) => ({
    ...item,
    percentage: totalEngagement.value ? Math.round((item.count / totalEngagement.value) * 100) : 0,
  }))
})

const engagementPieStyle = computed(() => {
  if (!engagementStats.value.length) {
    return { background: '#f0f2f5' }
  }
  let currentAngle = 0
  const segments = engagementStats.value.map((item) => {
    const angle = (item.percentage / 100) * 360
    const start = currentAngle
    const end = currentAngle + angle
    currentAngle = end
    return `${item.color} ${start}deg ${end}deg`
  })
  return {
    background: `conic-gradient(${segments.join(',')})`,
  }
})

const topVenues = computed(() => {
  const counts: Record<string, number> = {}
  posts.value.forEach((post) => {
    if (post.venueName) {
      counts[post.venueName] = (counts[post.venueName] || 0) + 1
    }
  })
  return Object.entries(counts)
    .map(([name, count]) => ({
      name,
      count,
      percentage: totalPosts.value ? Math.round((count / totalPosts.value) * 100) : 0,
    }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)
})

const activeUsers = computed(() => {
  const userMap: Record<
    string,
    {
      username: string
      postCount: number
      totalLikes: number
      score: number
    }
  > = {}

  posts.value.forEach((post) => {
    if (!userMap[post.username]) {
      userMap[post.username] = {
        username: post.username,
        postCount: 0,
        totalLikes: 0,
        score: 0,
      }
    }
    const user = userMap[post.username]
    user.postCount += 1
    user.totalLikes += post.likes
    user.score = user.postCount * 2 + Math.round(user.totalLikes / 5)
  })

  return Object.values(userMap)
    .sort((a, b) => b.score - a.score)
    .slice(0, 4)
})

// 过滤后的动态列表
const filteredPosts = computed(() => {
  const filtered = posts.value.filter((post) => {
    // 搜索条件
    const matchesSearch =
      !searchQuery.value ||
      post.content.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      post.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (post.venueName && post.venueName.toLowerCase().includes(searchQuery.value.toLowerCase()))

    // 筛选条件
    let matchesFilter = true
    if (filterBy.value) {
      switch (filterBy.value) {
        case 'has_images':
          matchesFilter = post.images && post.images.length > 0
          break
        case 'has_comments':
          matchesFilter = post.comments > 0
          break
        case 'has_likes':
          matchesFilter = post.likes > 0
          break
      }
    }

    return matchesSearch && matchesFilter
  })

  // 按创建时间倒序排序
  filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

  return filtered
})

// 分页相关
const paginatedPosts = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize
  const endIndex = startIndex + pageSize
  return filteredPosts.value.slice(startIndex, endIndex)
})

const totalPages = computed(() => {
  return Math.ceil(filteredPosts.value.length / pageSize)
})

// 获取图片URL（用于显示）
const getImageUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

// 获取视频URL（用于显示）
const getVideoUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

// 格式化时间
const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }

  // 小于1小时
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return `${minutes}分钟前`
  }

  // 小于24小时
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时前`
  }

  // 小于7天
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    return `${days}天前`
  }

  // 其他情况显示具体日期
  return date.toLocaleDateString('zh-CN')
}

// 切换选择动态
const toggleSelectPost = (postId: number) => {
  const index = selectedPosts.value.indexOf(postId)
  if (index > -1) {
    selectedPosts.value.splice(index, 1)
  } else {
    selectedPosts.value.push(postId)
  }
}

// 设置/取消置顶
const togglePin = async (postId: number, pinned: boolean) => {
  try {
    await socialStore.setPinned(postId, pinned)
    alert(pinned ? '已设为置顶' : '已取消置顶')
  } catch (error: any) {
    console.error('设置置顶失败:', error)
    alert(error?.response?.data?.message || '操作失败，请重试')
  }
}

// 设置/取消精华
const toggleFeatured = async (postId: number, featured: boolean) => {
  try {
    await socialStore.setFeatured(postId, featured)
    alert(featured ? '已设为精华' : '已取消精华')
  } catch (error: any) {
    console.error('设置精华失败:', error)
    alert(error?.response?.data?.message || '操作失败，请重试')
  }
}

// 删除单个动态
const deletePost = async (postId: number) => {
  if (confirm('确定要删除这条动态吗？这将同时删除相关的评论。')) {
    try {
      await socialStore.deletePost(postId)
      // 如果当前动态被选中，从选中列表中移除
      const index = selectedPosts.value.indexOf(postId)
      if (index > -1) {
        selectedPosts.value.splice(index, 1)
      }
      alert('动态删除成功！')
    } catch (error) {
      console.error('删除动态失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 批量删除动态
const batchDeletePosts = async () => {
  if (
    confirm(`确定要删除选中的 ${selectedPosts.value.length} 条动态吗？这将同时删除相关的评论。`)
  ) {
    try {
      // 批量删除每条动态
      for (const postId of selectedPosts.value) {
        await socialStore.deletePost(postId)
      }
      selectedPosts.value = []
      const deletedCount = selectedPosts.value.length
      selectedPosts.value = []
      alert(`成功删除 ${deletedCount} 条动态！`)
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('批量删除失败，请重试')
    }
  }
}

// 刷新动态列表
const refreshPosts = async () => {
  loading.value = true
  try {
    await socialStore.fetchPosts()
    currentPage.value = 1
    selectedPosts.value = []
  } catch (error) {
    console.error('刷新失败:', error)
  } finally {
    loading.value = false
  }
}

// 查看评论
const viewComments = async (post: any) => {
  currentPost.value = post
  const data = await socialStore.fetchComments(post.id)
  currentPostComments.value = data
  showCommentsModal.value = true
}

// 关闭评论弹窗
const closeCommentsModal = () => {
  showCommentsModal.value = false
  currentPost.value = null
  currentPostComments.value = []
}

// 删除评论
const deleteComment = async (commentId: number) => {
  if (confirm('确定要删除这条评论吗？')) {
    try {
      if (!currentPost.value) return
      await socialStore.deleteComment(currentPost.value.id, commentId)
      currentPostComments.value = currentPostComments.value.filter((c) => c.id !== commentId)
      alert('评论删除成功')
    } catch (error) {
      console.error('删除评论失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 预览图片
const previewImage = (imageUrl: string) => {
  previewImageUrl.value = imageUrl
}

// 监听过滤条件变化，重置页码
const handleFilterChange = () => {
  currentPage.value = 1
  selectedPosts.value = []
}

// 监听搜索和筛选变化
watch([searchQuery, filterBy], handleFilterChange)

onMounted(() => {
  // 确保加载动态数据
  if (posts.value.length === 0) {
    socialStore.fetchPosts()
  }
})
</script>

<style scoped>
.admin-social {
  padding: var(--spacing-xl) 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  padding-bottom: var(--spacing-sm);
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  border-radius: 2px;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--border-lighter);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-icon {
  font-size: 2.5rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(102, 177, 255, 0.05));
  border-radius: var(--border-radius-md);
  transition: transform 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
  background: linear-gradient(135deg, var(--text-primary), var(--text-regular));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.insights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.insight-card {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-md);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: var(--text-primary);
  font-weight: 700;
  background: linear-gradient(135deg, var(--text-primary), var(--text-regular));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-tip {
  font-size: 14px;
  color: var(--text-secondary);
}

.engagement-chart {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
  align-items: center;
}

.pie-chart {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  position: relative;
  box-shadow: inset 0 0 30px rgba(0, 0, 0, 0.05);
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background-color: var(--bg-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
}

.pie-number {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
}

.pie-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.engagement-legend {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.engagement-legend li {
  display: grid;
  grid-template-columns: auto auto auto 1fr auto;
  align-items: center;
  gap: var(--spacing-sm);
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.legend-name {
  font-weight: 500;
  color: var(--text-primary);
}

.legend-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.legend-bar {
  position: relative;
  width: 100%;
  height: 8px;
  background-color: var(--border-extra-light);
  border-radius: 999px;
  overflow: hidden;
}

.legend-fill {
  height: 100%;
  border-radius: inherit;
  transition: width 0.3s ease;
}

.legend-percentage {
  min-width: 40px;
  text-align: right;
  font-weight: 600;
  color: var(--text-primary);
}

.venue-list,
.user-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.venue-info {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  margin-bottom: var(--spacing-xs);
}

.venue-rank {
  font-weight: 700;
  color: var(--primary-color);
}

.venue-name {
  font-weight: 600;
  color: var(--text-primary);
}

.venue-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.venue-bar {
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background-color: var(--border-extra-light);
  overflow: hidden;
}

.venue-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  transition: width 0.3s ease;
}

.empty-legend {
  color: var(--text-secondary);
  font-size: 14px;
}

.user-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--border-light);
}

.user-list li:last-child {
  border-bottom: none;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
}

.user-meta {
  font-size: 12px;
  color: var(--text-secondary);
}

.user-badge {
  font-weight: 600;
  color: var(--warning-color);
}

.header-stats {
  display: flex;
  gap: var(--spacing-lg);
  font-size: 16px;
  color: var(--text-secondary);
}

.stat-item {
  display: flex;
  align-items: center;
}

.search-filters {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  flex-wrap: wrap;
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.8), rgba(233, 236, 239, 0.5));
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-filters .base-input {
  flex: 1;
  min-width: 300px;
}

.search-filters .base-select {
  width: 200px;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(102, 177, 255, 0.05));
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
  animation: slideIn 0.3s ease;
}

.posts-list-container {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--spacing-lg);
}

.loading {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
  font-size: 16px;
  position: relative;
}

.loading::after {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid var(--border-light);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-left: var(--spacing-md);
  vertical-align: middle;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
  font-size: 16px;
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.5), rgba(233, 236, 239, 0.3));
  border-radius: var(--border-radius-md);
  border: 2px dashed var(--border-light);
}

.posts-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-lg);
}

.post-card {
  border: 1px solid var(--border-lighter);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  position: relative;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.post-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
  border-color: var(--primary-color);
}

.post-selector {
  position: absolute;
  top: var(--spacing-md);
  right: var(--spacing-md);
  z-index: 10;
}

.post-checkbox {
  width: 18px;
  height: 18px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-md);
  gap: var(--spacing-md);
}

.post-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
}

.author-info {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.post-time {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 2px;
}

.post-venue {
  font-size: 14px;
  color: var(--text-secondary);
}

.post-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  align-items: flex-end;
  flex-shrink: 0;
}

.post-badges-admin {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-xs);
}

.badge-pinned-admin,
.badge-featured-admin,
.badge-hot-admin {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

.badge-pinned-admin {
  background: rgba(255, 193, 7, 0.15);
  color: #f57c00;
}

.badge-featured-admin {
  background: rgba(255, 152, 0, 0.15);
  color: #e65100;
}

.badge-hot-admin {
  background: rgba(255, 87, 34, 0.15);
  color: #bf360c;
}

.action-buttons {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
}

.post-content {
  margin-bottom: var(--spacing-md);
}

.post-text {
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  word-break: break-word;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: var(--spacing-sm);
  position: relative;
}

.post-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: transform 0.3s;
}

.post-image:hover {
  transform: scale(1.05);
}

.post-video {
  margin-top: var(--spacing-md);
}

.post-video-player {
  width: 100%;
  max-height: 400px;
  border-radius: var(--border-radius-sm);
  background-color: #000;
}

.more-images {
  position: absolute;
  bottom: var(--spacing-sm);
  right: var(--spacing-sm);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: var(--border-radius-sm);
  font-size: 12px;
}

.post-stats {
  display: flex;
  gap: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-light);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  margin-top: var(--spacing-xl);
}

.page-info {
  font-size: 16px;
  color: var(--text-secondary);
}

/* 评论弹窗样式 */
.comments-list {
  max-height: 400px;
  overflow-y: auto;
  padding-right: var(--spacing-md);
}

.comment-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--border-light);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-primary);
  word-break: break-word;
}

.no-comments {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

/* 图片预览样式 */
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  cursor: pointer;
}

.image-preview-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  cursor: default;
}

.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: var(--spacing-sm);
}

.preview-image {
  max-width: 100%;
  max-height: 80vh;
  border-radius: var(--border-radius-md);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  .stats-overview {
    grid-template-columns: 1fr;
  }
  .insights-grid {
    grid-template-columns: 1fr;
  }
  .engagement-chart {
    flex-direction: column;
  }

  .header-stats {
    flex-direction: column;
    gap: var(--spacing-sm);
    width: 100%;
  }

  .search-filters {
    flex-direction: column;
  }

  .search-filters .base-input,
  .search-filters .base-select {
    width: 100%;
  }

  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .post-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .pagination {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .post-images {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }

  .post-image {
    height: 80px;
  }
}
</style>
