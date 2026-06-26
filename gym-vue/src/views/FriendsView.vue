<template>
  <div class="friends-view page-container">
    <div class="container">
      <div class="friends-header">
        <h1 class="page-title">我的好友</h1>
        <RouterLink to="/friends/add">
          <BaseButton type="primary">添加好友</BaseButton>
        </RouterLink>
      </div>

      <!-- 好友请求 -->
      <div v-if="friendRequests.length > 0" class="friend-requests-section">
        <h2 class="section-title">好友请求</h2>
        <div class="requests-list">
          <div
            v-for="request in pendingRequests"
            :key="request.id"
            class="request-card"
          >
            <div class="request-info">
              <div class="request-avatar">{{ request.fromUsername[0] }}</div>
              <div class="request-details">
                <div class="request-username">{{ request.fromUsername }}</div>
                <div v-if="request.message" class="request-message">
                  {{ request.message }}
                </div>
                <div class="request-time">{{ formatTime(request.createdAt) }}</div>
              </div>
            </div>
            <div class="request-actions">
              <BaseButton
                type="success"
                size="small"
                @click="handleAccept(request.id)"
              >
                接受
              </BaseButton>
              <BaseButton
                type="danger"
                size="small"
                @click="handleReject(request.id)"
              >
                拒绝
              </BaseButton>
            </div>
          </div>
        </div>
      </div>

      <!-- 好友列表 -->
      <div class="friends-section">
        <h2 class="section-title">好友列表 ({{ friends.length }})</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="friends.length === 0" class="empty-state">
          <p>暂无好友，快去添加吧！</p>
          <RouterLink to="/friends/add">
            <BaseButton type="primary">添加好友</BaseButton>
          </RouterLink>
        </div>
        <div v-else class="friends-list">
          <div
            v-for="friend in friends"
            :key="friend.id"
            class="friend-card"
            @click="handleStartChat(friend.id, friend.username)"
          >
            <div class="friend-avatar">
              <img
                v-if="getAvatarUrl(friend.avatar)"
                :src="getAvatarUrl(friend.avatar)"
                alt="头像"
                class="avatar-img"
              />
              <span v-else>{{ friend.username[0] }}</span>
              <span
                v-if="friend.isOnline"
                class="online-indicator"
                title="在线"
              ></span>
            </div>
            <div class="friend-info">
              <div class="friend-name">
              {{ getDisplayName(friend.nickname, friend.username) }}
              </div>
              <div class="friend-meta">
                <span v-if="friend.school" class="friend-school">
                  🏫 {{ friend.school }}
                </span>
                <span v-else-if="!friend.isOnline && friend.lastSeen" class="friend-status">
                  最后活跃: {{ formatTime(friend.lastSeen) }}
                </span>
                <span v-else class="friend-status">离线</span>
              </div>
            </div>
            <div class="friend-actions">
              <button
                class="action-icon"
                title="私聊"
                @click.stop="handleStartChat(friend.id, friend.username)"
              >
                💬
              </button>
              <button
                class="action-icon"
                title="删除好友"
                @click.stop="handleRemoveFriend(friend.id)"
              >
                🗑️
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFriendsStore } from '@/stores/friends'
import { useChatStore } from '@/stores/chat'
import { BaseButton } from '@/components/common'

const router = useRouter()
const friendsStore = useFriendsStore()
const chatStore = useChatStore()

const friends = computed(() => friendsStore.friends)
const friendRequests = computed(() => friendsStore.friendRequests)
const loading = computed(() => friendsStore.loading)

const pendingRequests = computed(() =>
  friendRequests.value.filter((r) => r.status === 'pending')
)

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

const handleAccept = async (requestId: number) => {
  await friendsStore.acceptFriendRequest(requestId)
  alert('已接受好友请求')
}

const handleReject = async (requestId: number) => {
  if (confirm('确定要拒绝这个好友请求吗？')) {
    await friendsStore.rejectFriendRequest(requestId)
    alert('已拒绝好友请求')
  }
}

const handleStartChat = async (userId: number, username: string) => {
  await chatStore.createChat(userId, username)
  router.push(`/chat/${userId}`)
}

const handleRemoveFriend = async (friendId: number) => {
  if (confirm('确定要删除这个好友吗？')) {
    await friendsStore.removeFriend(friendId)
    alert('已删除好友')
  }
}

const getDisplayName = (nickname?: string, username?: string) => {
  if (!username) return nickname || ''
  if (nickname && nickname !== username) return `${nickname}（${username}）`
  return username
}

const getAvatarUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  const encodedPath = path.split('/').map((segment) => encodeURIComponent(segment)).join('/')
  return `${baseURL.replace('/api', '')}/api/files/${encodedPath}`
}

onMounted(() => {
  friendsStore.fetchFriends()
  friendsStore.fetchFriendRequests()
})
</script>

<style scoped>
.friends-view {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: calc(100vh - 200px);
  padding: var(--spacing-xl) 0;
}

.friends-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xl);
  padding: 0 var(--spacing-md);
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.friend-requests-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: var(--spacing-xl);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: var(--spacing-xl);
  border: 1px solid rgba(255, 255, 255, 0.8);
  margin: 0 var(--spacing-md) var(--spacing-xl);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.requests-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.request-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.request-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.request-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.request-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
}

.request-details {
  flex: 1;
}

.request-username {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.request-message {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.request-time {
  font-size: 12px;
  color: var(--text-placeholder);
}

.request-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.friends-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: var(--spacing-xl);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
  margin: 0 var(--spacing-md);
}

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

.friends-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.friend-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin-bottom: var(--spacing-sm);
}

.friend-card:hover {
  background: rgba(64, 158, 255, 0.05);
  border-color: rgba(64, 158, 255, 0.3);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.friend-avatar {
  position: relative;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  flex-shrink: 0;
}

.friend-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: 3px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

.friend-info {
  flex: 1;
  min-width: 0;
}

.friend-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.friend-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 13px;
  color: var(--text-secondary);
}

.friend-school,
.friend-status {
  font-size: 12px;
}

.friend-actions {
  display: flex;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.action-icon {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(0, 0, 0, 0.04);
  font-size: 18px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon:hover {
  background: rgba(64, 158, 255, 0.1);
  transform: scale(1.1);
}

.action-icon:last-child:hover {
  background: rgba(255, 71, 87, 0.1);
  color: #ff4757;
}

@media (max-width: 768px) {
  .friends-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .request-card,
  .friend-card {
    flex-wrap: wrap;
  }

  .request-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>

