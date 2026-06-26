<template>
  <div class="add-friend-view page-container">
    <div class="container">
      <div class="add-friend-header">
        <RouterLink to="/friends" class="back-link">← 返回</RouterLink>
        <h1 class="page-title">添加好友</h1>
      </div>

      <!-- 搜索框 -->
      <div class="search-section">
        <div class="search-box">
          <BaseInput
            v-model="searchKeyword"
            placeholder="搜索用户名或昵称..."
            show-clear
            @keyup.enter="handleSearch"
          >
            <template #prefix>🔍</template>
          </BaseInput>
          <BaseButton type="primary" @click="handleSearch">搜索</BaseButton>
        </div>
      </div>

      <!-- 搜索结果 -->
      <div v-if="loading" class="loading">搜索中...</div>
      <div v-else-if="searchResults.length > 0" class="search-results">
        <h2 class="section-title">搜索结果</h2>
        <div class="results-list">
          <div
            v-for="user in searchResults"
            :key="user.id"
            class="user-card"
          >
            <div class="user-avatar">{{ user.username[0] }}</div>
            <div class="user-info">
              <div class="user-name">
                {{ user.nickname || user.username }}
              </div>
              <div class="user-meta">
                <span v-if="user.school" class="user-school">
                  🏫 {{ user.school }}
                </span>
                <span
                  v-if="user.isOnline"
                  class="online-badge"
                >
                  在线
                </span>
              </div>
            </div>
            <div class="user-actions">
              <BaseButton
                v-if="!isFriend(user.id)"
                type="primary"
                size="small"
                @click="handleAddFriend(user)"
              >
                添加好友
              </BaseButton>
              <span v-else class="friend-badge">已是好友</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="hasSearched && !loading" class="empty-state">
        <p>未找到相关用户</p>
      </div>
      <div v-else class="empty-state">
        <p>请输入关键词搜索用户</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useFriendsStore } from '@/stores/friends'
import { BaseInput, BaseButton } from '@/components/common'

const router = useRouter()
const friendsStore = useFriendsStore()

const searchKeyword = ref('')
const searchResults = ref<any[]>([])
const loading = ref(false)
const hasSearched = ref(false)

const isFriend = (userId: number) => {
  return friendsStore.friends.some((f) => f.id === userId)
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    alert('请输入搜索关键词')
    return
  }

  loading.value = true
  hasSearched.value = true

  try {
    const results = await friendsStore.searchUsers(searchKeyword.value)
    searchResults.value = results.filter(
      (user) =>
        user.username.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
        (user.nickname &&
          user.nickname.toLowerCase().includes(searchKeyword.value.toLowerCase())),
    )
  } catch (error) {
    alert('搜索失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleAddFriend = async (user: any) => {
  if (confirm('确定要添加这个用户为好友吗？')) {
    await friendsStore.sendFriendRequest(
      user.id,
      '你好，我想添加你为好友',
      user.nickname || user.username,
    )
    alert('好友请求已发送')
    // 更新搜索结果，标记为已发送
    const target = searchResults.value.find((u) => u.id === user.id)
    if (target) {
      // 可以添加一个标记表示已发送请求
    }
  }
}
</script>

<style scoped>
.add-friend-view {
  background-color: var(--bg-color-page);
}

.add-friend-header {
  margin-bottom: var(--spacing-xl);
}

.back-link {
  display: inline-block;
  margin-bottom: var(--spacing-md);
  color: var(--primary-color);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.back-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.page-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
}

.search-section {
  background-color: var(--bg-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  margin-bottom: var(--spacing-lg);
}

.search-box {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.search-box .base-input {
  flex: 1;
}

.loading,
.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

.search-results {
  background-color: var(--bg-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.user-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
}

.user-avatar {
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

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 13px;
  color: var(--text-secondary);
}

.user-school {
  font-size: 12px;
}

.online-badge {
  padding: 2px 8px;
  background-color: var(--success-color);
  color: #fff;
  border-radius: var(--border-radius-sm);
  font-size: 11px;
}

.user-actions {
  flex-shrink: 0;
}

.friend-badge {
  padding: 6px 12px;
  background-color: var(--border-lighter);
  color: var(--text-secondary);
  border-radius: var(--border-radius-md);
  font-size: 13px;
}

@media (max-width: 768px) {
  .search-box {
    flex-direction: column;
  }

  .search-box .base-input {
    width: 100%;
  }

  .user-card {
    flex-wrap: wrap;
  }
}
</style>

