<template>
  <div class="friends-chat-view page-container">
    <div class="container">
      <div class="friends-chat-layout">
        <!-- 左侧：好友列表和聊天列表 -->
        <div class="left-sidebar">
          <!-- 标签切换 -->
          <div class="tabs">
            <button
              :class="['tab', { 'tab--active': activeTab === 'friends' }]"
              @click="activeTab = 'friends'"
            >
              好友
            </button>
            <button
              :class="['tab', { 'tab--active': activeTab === 'chats' }]"
              @click="activeTab = 'chats'"
            >
              聊天
              <span
                v-if="hasUnreadChats"
                class="red-dot tab-red-dot"
                title="有未读消息"
              ></span>
            </button>
            <button
              :class="['tab', { 'tab--active': activeTab === 'add' }]"
              @click="activeTab = 'add'"
            >
              添加好友
            </button>
          </div>

          <!-- 好友列表 -->
          <div v-if="activeTab === 'friends'" class="tab-content">
            <div class="section-header">
              <h3 class="section-title">我的好友 ({{ friends.length }})</h3>
            </div>

            <!-- 好友请求 -->
            <div v-if="pendingRequests.length > 0" class="friend-requests">
              <h4 class="subsection-title">好友请求</h4>
              <div class="requests-list">
                <div
                  v-for="request in pendingRequests"
                  :key="request.id"
                  class="request-card"
                >
                  <div class="request-info">
                    <div class="request-avatar">
                      <img
                        v-if="getAvatarUrl(request.fromAvatar)"
                        :src="getAvatarUrl(request.fromAvatar)"
                        alt="头像"
                        class="avatar-img"
                      />
                      <span v-else>{{ request.fromUsername[0] }}</span>
                    </div>
                    <div class="request-details">
                      <div class="request-username">{{ request.fromUsername }}</div>
                      <div v-if="request.message" class="request-message">
                        {{ request.message }}
                      </div>
                    </div>
                  </div>
                  <div class="request-actions">
                    <button
                      class="action-btn action-btn--accept"
                      @click="handleAccept(request.id)"
                    >
                      接受
                    </button>
                    <button
                      class="action-btn action-btn--reject"
                      @click="handleReject(request.id)"
                    >
                      拒绝
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 好友列表 -->
            <div v-if="loading" class="loading">加载中...</div>
            <div v-else-if="friends.length === 0" class="empty-state">
              <p>暂无好友</p>
            </div>
            <div v-else class="friends-list">
              <div
                v-for="friend in friends"
                :key="friend.id"
                :class="['friend-item', { 'friend-item--active': selectedFriendId === friend.id }]"
                @click="handleSelectFriend(friend.id, friend.username)"
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
                <button
                  class="remove-btn"
                  title="删除好友"
                  @click.stop="handleRemoveFriend(friend.id)"
                >
                  🗑️
                </button>
              </div>
            </div>
          </div>

          <!-- 聊天列表 -->
          <div v-if="activeTab === 'chats'" class="tab-content">
            <div class="section-header">
              <h3 class="section-title">
                聊天列表
                <span v-if="hasUnreadChats" class="red-dot" title="有未读消息"></span>
              </h3>
            </div>
            <div v-if="loading" class="loading">加载中...</div>
            <div v-else-if="chats.length === 0" class="empty-state">
              <p>暂无聊天记录</p>
            </div>
            <div v-else class="chat-list">
              <div
                v-for="chat in chats"
                :key="chat.id"
                :class="['chat-item', { 'chat-item--active': currentChatId === chat.id }]"
                @click="handleSelectChat(chat.id)"
              >
                <div class="chat-avatar">
                  <img
                    v-if="getAvatarUrl(chat.avatar)"
                    :src="getAvatarUrl(chat.avatar)"
                    alt="头像"
                    class="avatar-img"
                  />
                  <span v-else>{{ chat.username[0] }}</span>
                  <span
                    v-if="chat.isOnline"
                    class="online-indicator"
                    title="在线"
                  ></span>
                </div>
                <div class="chat-info">
                  <div class="chat-name-row">
                    <span class="chat-name">{{ getDisplayName(undefined, chat.username) }}</span>
                    <span v-if="chat.unreadCount > 0" class="unread-badge">
                      {{ chat.unreadCount }}
                    </span>
                  </div>
                  <div class="chat-last-message">
                    {{ chat.lastMessage || '暂无消息' }}
                  </div>
                  <div v-if="chat.lastMessageTime" class="chat-time">
                    {{ formatTime(chat.lastMessageTime) }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 添加好友 -->
          <div v-if="activeTab === 'add'" class="tab-content">
            <div class="section-header">
              <h3 class="section-title">添加好友</h3>
            </div>
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
                <BaseButton type="primary" size="small" @click="handleSearch">
                  搜索
                </BaseButton>
              </div>
            </div>
            <div v-if="searchLoading" class="loading">搜索中...</div>
            <div v-else-if="searchResults.length > 0" class="search-results">
              <div
                v-for="user in searchResults"
                :key="user.id"
                class="user-card"
              >
                <div class="user-avatar">
                  <img
                    v-if="getAvatarUrl(user.avatar)"
                    :src="getAvatarUrl(user.avatar)"
                    alt="头像"
                    class="avatar-img"
                  />
                  <span v-else>{{ user.username[0] }}</span>
                </div>
                <div class="user-info">
                  <div class="user-name">
                    {{ getDisplayName(user.nickname, user.username) }}
                  </div>
                  <div class="user-meta">
                    <span v-if="user.school" class="user-school">
                      🏫 {{ user.school }}
                    </span>
                    <span v-if="user.isOnline" class="online-badge">在线</span>
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
            <div v-else-if="hasSearched && !searchLoading" class="empty-state">
              <p>未找到相关用户</p>
            </div>
            <div v-else class="empty-state">
              <p>请输入关键词搜索用户</p>
            </div>
          </div>
        </div>

        <!-- 右侧：聊天窗口 -->
        <div class="right-main">
          <div v-if="!currentChatId && !selectedFriendId" class="chat-empty">
            <div class="empty-content">
              <p>选择一个好友或聊天开始对话</p>
            </div>
          </div>
          <div v-else class="chat-window">
            <!-- 聊天头部 -->
            <div class="chat-header">
              <div class="chat-header-info">
                <div class="chat-header-avatar">
                  <img
                    v-if="chatHeaderAvatar"
                    :src="chatHeaderAvatar"
                    alt="头像"
                    class="avatar-img"
                  />
                  <span v-else>{{ currentChat?.username || selectedFriendName?.[0] }}</span>
                  <span
                    v-if="currentChat?.isOnline || selectedFriendOnline"
                    class="online-indicator"
                  ></span>
                </div>
                <div class="chat-header-name">
                  {{ getDisplayName(selectedFriendNickname, currentChat?.username || selectedFriendName || '') }}
                </div>
              </div>
              <!-- 对方正在输入中提示 -->
              <div
                v-if="currentChatId && typingMap.get(currentChatId)"
                class="typing-indicator-text"
              >
                对方正在输入…
              </div>
            </div>

            <!-- 消息列表 -->
            <div class="messages-container" ref="messagesContainer" @scroll="handleMessagesScroll">
              <div v-if="loading" class="loading">加载中...</div>
              <div v-else class="messages-list">
                <div
                  v-for="message in messages"
                  :key="message.id"
                  :class="[
                    'message-item',
                    {
                      'message-item--sent': message.senderId === currentUserId,
                      'message-item--received': message.senderId !== currentUserId,
                    },
                  ]"
                >
                  <div
                    v-if="message.senderId === currentUserId"
                    class="message-bubble-row message-bubble-row--sent"
                  >
                    <span
                      :class="[
                        'status-icon',
                        {
                          'status-sent': !message.localStatus || message.localStatus === 'sent',
                          'status-delivered': message.localStatus === 'delivered',
                          'status-read': message.localStatus === 'read' || message.isRead,
                        },
                      ]"
                    >
                      ✔
                    </span>
                    <div v-if="message.type === 'image'" class="message-content message-image">
                      <img
                        :src="getImageUrl(message.content)"
                        alt="图片"
                        class="chat-image"
                        @click="previewImage(getImageUrl(message.content))"
                      />
                    </div>
                    <div v-else-if="message.type === 'video'" class="message-content message-video">
                      <video :src="getFileUrl(message.content)" controls class="chat-video"></video>
                    </div>
                    <div v-else-if="message.type === 'emoji'" class="message-content message-emoji">
                      <span class="emoji-large">{{ message.content }}</span>
                    </div>
                    <div
                      v-else-if="message.type === 'share_post'"
                      :class="['share-card', { 'share-card--sent': message.senderId === currentUserId }]"
                      @click="handleOpenSharedPost(message)"
                    >
                      <div class="share-card-header">
                        <span class="share-card-title">动态分享</span>
                        <span class="share-card-hint">点击查看</span>
                      </div>
                      <div class="share-card-body">
                        <div class="share-card-text">
                          {{ getSharePayload(message)?.content || '查看分享的动态' }}
                          <span
                            v-if="getSharePayload(message)?.hasVideo"
                            class="share-card-video-tag"
                          >
                            含视频
                          </span>
                        </div>
                        <img
                          v-if="getSharePayload(message)?.image"
                          :src="getImageUrl(getSharePayload(message)!.image!)"
                          alt="封面"
                          class="share-card-thumb"
                        />
                      </div>
                      <div class="share-card-footer">
                        <span class="share-card-footer-text">
                          来自 {{ getSharePayload(message)?.author || '好友' }} ·
                          <span class="share-card-link-tip">打开动态 →</span>
                        </span>
                      </div>
                    </div>
                    <div v-else class="message-content message-text">{{ message.content }}</div>
                  </div>

                  <div v-else class="message-bubble-row">
                    <div v-if="message.type === 'image'" class="message-content message-image">
                      <img
                        :src="getImageUrl(message.content)"
                        alt="图片"
                        class="chat-image"
                        @click="previewImage(getImageUrl(message.content))"
                      />
                    </div>
                    <div v-else-if="message.type === 'video'" class="message-content message-video">
                      <video :src="getFileUrl(message.content)" controls class="chat-video"></video>
                    </div>
                    <div v-else-if="message.type === 'emoji'" class="message-content message-emoji">
                      <span class="emoji-large">{{ message.content }}</span>
                    </div>
                    <div
                      v-else-if="message.type === 'share_post'"
                      :class="['share-card', { 'share-card--sent': message.senderId === currentUserId }]"
                      @click="handleOpenSharedPost(message)"
                    >
                      <div class="share-card-header">
                        <span class="share-card-title">动态分享</span>
                        <span class="share-card-hint">点击查看</span>
                      </div>
                      <div class="share-card-body">
                        <div class="share-card-text">
                          {{ getSharePayload(message)?.content || '查看分享的动态' }}
                          <span
                            v-if="getSharePayload(message)?.hasVideo"
                            class="share-card-video-tag"
                          >
                            含视频
                          </span>
                        </div>
                        <img
                          v-if="getSharePayload(message)?.image"
                          :src="getImageUrl(getSharePayload(message)!.image!)"
                          alt="封面"
                          class="share-card-thumb"
                        />
                      </div>
                      <div class="share-card-footer">
                        <span class="share-card-footer-text">
                          来自 {{ getSharePayload(message)?.author || '好友' }} ·
                          <span class="share-card-link-tip">打开动态 →</span>
                        </span>
                      </div>
                    </div>
                    <div v-else class="message-content message-text">{{ message.content }}</div>
                  </div>

                  <div class="message-meta message-meta--sent" v-if="message.senderId === currentUserId">
                    <div class="message-time">
                      {{ formatMessageTime(message.createdAt) }}
                    </div>
                  </div>
                  <div class="message-meta" v-else>
                    <div class="message-time">
                      {{ formatMessageTime(message.createdAt) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 回到最新消息按钮 -->
            <button
              v-if="showScrollToLatest"
              class="scroll-to-latest-btn"
              type="button"
              @click="handleScrollToLatest"
            >
              <span class="scroll-to-latest-icon">⬇</span>
              <span class="scroll-to-latest-text">回到最新</span>
            </button>

            <!-- 输入框 -->
            <div class="chat-input-area">
              <!-- 表情包选择器 -->
              <div v-if="showEmojiPicker" class="emoji-picker">
                <div class="emoji-grid">
                  <span
                    v-for="emoji in emojiList"
                    :key="emoji"
                    class="emoji-item"
                    @click="insertEmoji(emoji)"
                  >
                    {{ emoji }}
                  </span>
                </div>
              </div>
              <div class="input-wrapper">
                <div class="input-actions">
                  <button
                    class="action-icon-btn"
                    title="选择图片"
                    @click="triggerImageUpload"
                  >
                    📷
                  </button>
                <button
                  class="action-icon-btn"
                  title="选择视频"
                  @click="triggerVideoUpload"
                >
                  🎬
                </button>
                  <button
                    class="action-icon-btn"
                    :class="{ 'action-icon-btn--active': showEmojiPicker }"
                    title="表情"
                    @click="showEmojiPicker = !showEmojiPicker"
                  >
                    😊
                  </button>
                </div>
                <input
                  ref="imageInput"
                  type="file"
                  accept="image/*"
                  class="hidden-input"
                  @change="handleImageSelect"
                />
              <input
                ref="videoInput"
                type="file"
                accept="video/*"
                class="hidden-input"
                @change="handleVideoSelect"
              />
                <input
                  v-model="inputMessage"
                  type="text"
                  class="message-input"
                  placeholder="输入消息..."
                  @input="handleInputChange"
                  @keyup.enter="handleSendMessage"
                />
                <BaseButton
                  type="primary"
                  size="small"
                  :disabled="!inputMessage.trim() && !uploadingImage"
                  @click="handleSendMessage"
                >
                  {{ uploadingImage ? '发送中...' : '发送' }}
                </BaseButton>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <div v-if="previewImageUrl" class="image-preview-overlay" @click="previewImageUrl = ''">
      <div class="image-preview-content" @click.stop>
        <button class="close-btn" @click="previewImageUrl = ''">✕</button>
        <img :src="previewImageUrl" alt="预览图片" class="preview-image" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFriendsStore } from '@/stores/friends'
import { useChatStore } from '@/stores/chat'
import { useUserStore } from '@/stores/user'
import { BaseInput, BaseButton } from '@/components/common'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const friendsStore = useFriendsStore()
const chatStore = useChatStore()
const userStore = useUserStore()

const activeTab = ref<'friends' | 'chats' | 'add'>('friends')
const selectedFriendId = ref<number | null>(null)
const selectedFriendName = ref<string>('') // username
const selectedFriendNickname = ref<string>('') // nickname
const selectedFriendOnline = ref(false)
const selectedFriendAvatar = ref<string>('')
const searchKeyword = ref('')
const searchResults = ref<
  {
    id: number
    username: string
    nickname?: string
    avatar?: string
    school?: string
    isOnline?: boolean
  }[]
>([])
const searchLoading = ref(false)
const hasSearched = ref(false)
const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)
const imageInput = ref<HTMLInputElement | null>(null)
const videoInput = ref<HTMLInputElement | null>(null)
const showEmojiPicker = ref(false)
const uploadingImage = ref(false)
const previewImageUrl = ref('')
const typingTimer = ref<number | null>(null)
const showScrollToLatest = ref(false)

// 常用表情包列表
const emojiList = [
  '😀', '😃', '😄', '😁', '😆', '😅', '🤣', '😂', '🙂', '🙃',
  '😉', '😊', '😇', '🥰', '😍', '🤩', '😘', '😗', '😚', '😙',
  '😋', '😛', '😜', '🤪', '😝', '🤑', '🤗', '🤭', '🤫', '🤔',
  '🤐', '🤨', '😐', '😑', '😶', '😏', '😒', '🙄', '😬', '🤥',
  '😌', '😔', '😪', '🤤', '😴', '😷', '🤒', '🤕', '🤢', '🤮',
  '🤧', '🥵', '🥶', '😶‍🌫️', '😵', '😵‍💫', '🤯', '🤠', '🥳', '😎',
  '🤓', '🧐', '😕', '😟', '🙁', '☹️', '😮', '😯', '😲', '😳',
  '🥺', '😦', '😧', '😨', '😰', '😥', '😢', '😭', '😱', '😖',
  '😣', '😞', '😓', '😩', '😫', '🥱', '😤', '😡', '😠', '🤬',
  '😈', '👿', '💀', '☠️', '💩', '🤡', '👹', '👺', '👻', '👽',
  '👾', '🤖', '😺', '😸', '😹', '😻', '😼', '😽', '🙀', '😿',
  '😾', '🙈', '🙉', '🙊', '💋', '💌', '💘', '💝', '💖', '💗',
  '💓', '💞', '💕', '💟', '❣️', '💔', '❤️', '🧡', '💛', '💚',
  '💙', '💜', '🖤', '🤍', '🤎', '💯', '💢', '💥', '💫', '💦',
  '💨', '🕳️', '💣', '💬', '👁️‍🗨️', '🗨️', '🗯️', '💭', '💤', '👋',
  '🤚', '🖐️', '✋', '🖖', '👌', '🤌', '🤏', '✌️', '🤞', '🤟',
  '🤘', '🤙', '👈', '👉', '👆', '🖕', '👇', '☝️', '👍', '👎',
  '✊', '👊', '🤛', '🤜', '👏', '🙌', '👐', '🤲', '🤝', '🙏',
]

const friends = computed(() => friendsStore.friends)
const friendRequests = computed(() => friendsStore.friendRequests)
const chats = computed(() => chatStore.chats)
const currentChatId = computed(() => chatStore.currentChatId)
const currentChat = computed(() => chatStore.currentChat)
const messages = computed(() => chatStore.messages)
const loading = computed(() => friendsStore.loading || chatStore.loading)
const currentUserId = computed(() => userStore.userInfo?.id || 1)
const hasUnreadChats = computed(() => chats.value.some((c) => (c.unreadCount || 0) > 0))
const typingMap = computed(() => chatStore.typingMap)
const onlineMap = computed(() => chatStore.onlineMap)
const chatHeaderAvatar = computed(() => {
  const chatAvatar = currentChat.value?.avatar
  if (chatAvatar) return getAvatarUrl(chatAvatar)
  return getAvatarUrl(selectedFriendAvatar.value)
})

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

const formatMessageTime = (timeString: string) => {
  const date = new Date(timeString)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
  })
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

const isFriend = (userId: number) => {
  return friends.value.some((f) => f.id === userId)
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

const handleSelectFriend = async (friendId: number, username: string) => {
  selectedFriendId.value = friendId
  selectedFriendName.value = username
  const friend = friends.value.find((f) => f.id === friendId)
  selectedFriendOnline.value = friend?.isOnline || false
  selectedFriendNickname.value = friend?.nickname || ''
  selectedFriendAvatar.value = friend?.avatar || ''

  // 查找是否已有聊天记录
  const existingChat = chats.value.find((c) => c.userId === friendId)
  if (existingChat) {
    await chatStore.fetchMessages(existingChat.id)
  } else {
    // 创建新聊天
    await chatStore.createChat(friendId, username)
    const newChat = chats.value.find((c) => c.userId === friendId)
    if (newChat) {
      await chatStore.fetchMessages(newChat.id)
    }
  }
  await nextTick()
  scrollToBottom()
}

const handleSelectChat = async (chatId: number) => {
  selectedFriendId.value = null
  selectedFriendAvatar.value = ''
  selectedFriendName.value = ''
  selectedFriendNickname.value = ''
  await chatStore.fetchMessages(chatId)
  const chat = chats.value.find((c) => c.id === chatId)
  if (chat) {
    selectedFriendName.value = chat.username
    selectedFriendOnline.value = chat.isOnline || false
    selectedFriendAvatar.value = chat.avatar || ''
  }
  await nextTick()
  scrollToBottom()
}

const getDisplayName = (nickname?: string, username?: string) => {
  if (!username) return nickname || ''
  if (nickname && nickname !== username) return `${nickname}（${username}）`
  return username
}

const handleRemoveFriend = async (friendId: number) => {
  if (confirm('确定要删除这个好友吗？')) {
    await friendsStore.removeFriend(friendId)
    if (selectedFriendId.value === friendId) {
      selectedFriendId.value = null
      selectedFriendName.value = ''
    }
    alert('已删除好友')
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    alert('请输入搜索关键词')
    return
  }

  searchLoading.value = true
  hasSearched.value = true

  try {
    const results = await friendsStore.searchUsers(searchKeyword.value)
    searchResults.value = results
  } catch (error) {
    console.error('Search failed:', error)
    alert('搜索失败，请稍后再试')
  } finally {
    searchLoading.value = false
  }
}

const handleAddFriend = async (user: { id: number; username: string; nickname?: string }) => {
  if (confirm('确定要添加这个用户为好友吗？')) {
    await friendsStore.sendFriendRequest(
      user.id,
      '你好，我想添加你为好友',
      user.nickname || user.username,
    )
    alert('好友请求已发送')
  }
}

const handleSendMessage = async () => {
  if (!inputMessage.value.trim() && !uploadingImage.value) return

  let targetChatId = currentChatId.value
  let receiverId: number

  if (selectedFriendId.value) {
    // 从好友列表发起的聊天
    receiverId = selectedFriendId.value
    const existingChat = chats.value.find((c) => c.userId === selectedFriendId.value)
    if (existingChat) {
      targetChatId = existingChat.id
    } else {
      const newChat = await chatStore.createChat(selectedFriendId.value, selectedFriendName.value)
      targetChatId = newChat.id
      await chatStore.fetchMessages(newChat.id)
    }
  } else if (currentChatId.value) {
    // 从聊天列表发起的聊天
    const chat = currentChat.value
    if (!chat) return
    receiverId = chat.userId
    targetChatId = currentChatId.value
  } else {
    return
  }

  if (targetChatId && receiverId) {
    await chatStore.sendMessage(targetChatId, receiverId, inputMessage.value, 'text')
    inputMessage.value = ''
    showEmojiPicker.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 输入中事件处理（节流）
const handleInputChange = () => {
  if (!currentUserId.value) return

  let targetChatId = currentChatId.value
  let receiverId: number | null = null

  if (selectedFriendId.value) {
    receiverId = selectedFriendId.value
    const existingChat = chats.value.find((c) => c.userId === selectedFriendId.value)
    if (existingChat) {
      targetChatId = existingChat.id
    }
  } else if (currentChatId.value && currentChat.value) {
    receiverId = currentChat.value.userId
    targetChatId = currentChatId.value
  }

  if (!targetChatId || !receiverId) return

  const client = (chatStore as any).stompClient?.value as import('@stomp/stompjs').Client | null
  if (!client || !client.connected) return

  const payload = JSON.stringify({
    chatId: targetChatId,
    fromUserId: currentUserId.value,
    toUserId: receiverId,
    typing: true,
  })
  client.publish({ destination: '/app/chat/typing', body: payload })

  if (typingTimer.value) {
    window.clearTimeout(typingTimer.value)
  }
  typingTimer.value = window.setTimeout(() => {
    const stopPayload = JSON.stringify({
      chatId: targetChatId,
      fromUserId: currentUserId.value,
      toUserId: receiverId,
      typing: false,
    })
    client.publish({ destination: '/app/chat/typing', body: stopPayload })
  }, 2000)
}

const triggerImageUpload = () => {
  imageInput.value?.click()
}

const handleImageSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 验证文件大小（限制为5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过5MB')
    return
  }

  uploadingImage.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    const uploadResult = (await request.post('/upload/chat', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })) as { path: string; url: string }

    // 发送图片消息
    let targetChatId = currentChatId.value
    let receiverId: number

    if (selectedFriendId.value) {
      receiverId = selectedFriendId.value
      const existingChat = chats.value.find((c) => c.userId === selectedFriendId.value)
      if (existingChat) {
        targetChatId = existingChat.id
      } else {
        const newChat = await chatStore.createChat(selectedFriendId.value, selectedFriendName.value)
        targetChatId = newChat.id
        await chatStore.fetchMessages(newChat.id)
      }
    } else if (currentChatId.value) {
      const chat = currentChat.value
      if (!chat) return
      receiverId = chat.userId
      targetChatId = currentChatId.value
    } else {
      return
    }

    if (targetChatId && receiverId) {
      await chatStore.sendMessage(targetChatId, receiverId, uploadResult.path, 'image')
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    alert('图片上传失败，请稍后重试')
  } finally {
    uploadingImage.value = false
    if (imageInput.value) {
      imageInput.value.value = ''
    }
  }
}

// 发送视频（与图片流程类似）
const handleVideoSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('video/')) {
    alert('请选择视频文件')
    return
  }

  uploadingImage.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    const uploadResult = (await request.post('/upload/chat/video', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })) as { path: string; url: string }

    let targetChatId = currentChatId.value
    let receiverId: number

    if (selectedFriendId.value) {
      receiverId = selectedFriendId.value
      const existingChat = chats.value.find((c) => c.userId === selectedFriendId.value)
      if (existingChat) {
        targetChatId = existingChat.id
      } else {
        const newChat = await chatStore.createChat(selectedFriendId.value, selectedFriendName.value)
        targetChatId = newChat.id
        await chatStore.fetchMessages(newChat.id)
      }
    } else if (currentChatId.value) {
      const chat = currentChat.value
      if (!chat) return
      receiverId = chat.userId
      targetChatId = currentChatId.value
    } else {
      return
    }

    if (targetChatId && receiverId) {
      await chatStore.sendMessage(targetChatId, receiverId, uploadResult.path, 'video')
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    console.error('视频上传失败:', error)
    alert('视频上传失败，请稍后重试')
  } finally {
    uploadingImage.value = false
  }
}

const insertEmoji = (emoji: string) => {
  inputMessage.value += emoji
  showEmojiPicker.value = false
}

const getImageUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

const getFileUrl = (path: string) => {
  return getImageUrl(path)
}

// 解析动态分享卡片内容
const getSharePayload = (message: { content: string }) => {
  try {
    const data = JSON.parse(message.content || '{}')
    if (data && data.type === 'share_post' && data.postId) {
      return data as {
        type: string
        postId: number
        author?: string
        authorAvatar?: string
        content?: string
        image?: string
        hasVideo?: boolean
      }
    }
  } catch {
    return null
  }
  return null
}

const handleOpenSharedPost = (message: { content: string }) => {
  const payload = getSharePayload(message)
  if (!payload?.postId) return
  router.push({ name: 'social', query: { postId: payload.postId } })
}

const triggerVideoUpload = () => {
  videoInput.value?.click()
}

const previewImage = (imageUrl: string) => {
  previewImageUrl.value = imageUrl
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const handleMessagesScroll = () => {
  const container = messagesContainer.value
  if (!container) return

  const distanceToBottom = container.scrollHeight - container.scrollTop - container.clientHeight
  const nearBottom = distanceToBottom < 80

  if (nearBottom) {
    showScrollToLatest.value = false
    return
  }

  // 只有当记录数超过 30 条并且用户明显离开底部时才展示按钮
  showScrollToLatest.value = messages.value.length > 30
}

const handleScrollToLatest = () => {
  scrollToBottom()
  showScrollToLatest.value = false
}

watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
})

onMounted(async () => {
  await friendsStore.fetchFriends()
  await friendsStore.fetchFriendRequests()
  await chatStore.fetchChats()

  // 确保WebSocket连接可靠建立
  chatStore.connectWebSocket()

  // 如果路由中有userId参数，选择该好友
  const userId = route.params.userId ? Number(route.params.userId) : null
  if (userId) {
    const friend = friends.value.find((f) => f.id === userId)
    if (friend) {
      await handleSelectFriend(userId, friend.username)
    }
  }

  await nextTick()
  scrollToBottom()
})
</script>

<style scoped>
.friends-chat-view {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  height: calc(100vh - 200px);
  padding: var(--spacing-lg) 0;
}

.friends-chat-layout {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: var(--spacing-xl);
  height: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 var(--spacing-md);
}

.left-sidebar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tabs {
  display: flex;
  border-bottom: 1px solid var(--border-lighter);
  background-color: var(--bg-color);
  padding: 0;
  border-radius: 12px 12px 0 0;
  gap: 0;
}

.tab {
  flex: 1;
  padding: var(--spacing-md) var(--spacing-lg);
  border: none;
  background-color: transparent;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border-radius: 0;
}

.tab:first-child {
  border-top-left-radius: 12px;
}

.tab:last-child {
  border-top-right-radius: 12px;
}

.tab:hover {
  color: var(--primary-color);
  background-color: rgba(64, 158, 255, 0.05);
}

.tab--active {
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-weight: 600;
}

.tab--active:hover {
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.tab-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
}

.section-header {
  margin-bottom: var(--spacing-md);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  background: linear-gradient(135deg, var(--primary-color), #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  padding-bottom: var(--spacing-xs);
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.red-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--danger-color);
  margin-left: 6px;
  vertical-align: middle;
}

.tab-red-dot {
  position: relative;
  top: -1px;
}

.subsection-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.friend-requests {
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-lighter);
}

.requests-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.request-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(248, 249, 250, 0.8));
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.request-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: var(--primary-color);
}

.request-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex: 1;
}

.request-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.8);
  transition: transform 0.3s ease;
}

.request-card:hover .request-avatar {
  transform: scale(1.1);
}

.request-details {
  flex: 1;
  min-width: 0;
}

.request-username {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
  margin-bottom: 2px;
}

.request-message {
  font-size: 12px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.request-actions {
  display: flex;
  gap: var(--spacing-xs);
}

.action-btn {
  padding: 6px 16px;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-btn--accept {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: #fff;
}

.action-btn--accept:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.action-btn--reject {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: #fff;
}

.action-btn--reject:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
}

.friends-list,
.chat-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.friend-item,
.chat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 4px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(248, 249, 250, 0.6));
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.friend-item:hover,
.chat-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.1));
  transform: translateX(6px) translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
}

/* 确保悬停时所有子元素文字颜色也变为黑色，同时不影响激活状态 */
.friend-item:hover .friend-name,
.friend-item:hover .friend-meta,
.friend-item:hover .friend-school,
.friend-item:hover .friend-status,
.chat-item:hover .chat-name,
.chat-item:hover .chat-last-message,
.chat-item:hover .chat-time {
  color: var(--text-primary);
}

.friend-item--active,
.chat-item--active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
  border-color: transparent;
  transform: translateX(4px);
}

.friend-item--active .friend-name,
.friend-item--active .friend-meta,
.chat-item--active .chat-name,
.chat-item--active .chat-last-message,
.chat-item--active .chat-time {
  color: #fff;
}

.friend-avatar,
.chat-avatar {
  position: relative;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.8);
  transition: transform 0.3s ease;
}

.friend-item:hover .friend-avatar,
.chat-item:hover .chat-avatar {
  transform: scale(1.1);
}

.friend-item--active .friend-avatar,
.chat-item--active .chat-avatar {
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 6px 20px rgba(255, 255, 255, 0.3);
}

.friend-avatar .avatar-img,
.chat-avatar .avatar-img,
.request-avatar .avatar-img,
.chat-header-avatar .avatar-img,
.user-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
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

.friend-item--active .online-indicator,
.chat-item--active .online-indicator {
  border-color: var(--primary-color);
}

.friend-info,
.chat-info {
  flex: 1;
  min-width: 0;
}

.friend-name,
.chat-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
  margin-bottom: 2px;
}

.friend-meta {
  font-size: 12px;
  color: var(--text-secondary);
}

.friend-school,
.friend-status {
  font-size: 11px;
}

.chat-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2px;
}

.unread-badge {
  background: linear-gradient(135deg, #f56c6c, #ff7875);
  color: #fff;
  border-radius: 12px;
  padding: 3px 8px;
  font-size: 11px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.4);
  animation: pulse-badge 2s ease-in-out infinite;
}

@keyframes pulse-badge {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.chat-last-message {
  font-size: 12px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}

.chat-time {
  font-size: 11px;
  color: var(--text-placeholder);
}

.remove-btn {
  width: 28px;
  height: 28px;
  border: none;
  background-color: transparent;
  font-size: 14px;
  cursor: pointer;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s;
  flex-shrink: 0;
}

.remove-btn:hover {
  background-color: var(--danger-color);
  color: #fff;
}

.search-section {
  margin-bottom: var(--spacing-md);
}

.search-box {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.8), rgba(233, 236, 239, 0.5));
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-box .base-input {
  flex: 1;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.user-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(248, 249, 250, 0.8));
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: var(--primary-color);
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.8);
  transition: transform 0.3s ease;
}

.user-card:hover .user-avatar {
  transform: scale(1.1);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
  margin-bottom: 2px;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 11px;
  color: var(--text-secondary);
}

.user-school {
  font-size: 11px;
}

.online-badge {
  padding: 2px 6px;
  background-color: var(--success-color);
  color: #fff;
  border-radius: var(--border-radius-sm);
  font-size: 10px;
}

.user-actions {
  flex-shrink: 0;
}

.friend-badge {
  padding: 4px 8px;
  background-color: var(--border-lighter);
  color: var(--text-secondary);
  border-radius: var(--border-radius-sm);
  font-size: 11px;
}

.loading {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  font-size: 14px;
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

.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  font-size: 14px;
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.5), rgba(233, 236, 239, 0.3));
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-light);
  margin: var(--spacing-md);
}

.right-main {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* 限制整体高度，内部滚动（适当再收缩一点高度） */
  max-height: calc(100vh - 260px);
  /* Grid 子项默认 min-width:auto，会被大视频撑破列宽 */
  min-width: 0;
}

.chat-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.5), rgba(233, 236, 239, 0.3));
}

.empty-content {
  text-align: center;
  color: var(--text-secondary);
  font-size: 16px;
  padding: var(--spacing-xl);
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-light);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.chat-header {
  padding: var(--spacing-lg);
  border-bottom: 2px solid var(--border-lighter);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 249, 250, 0.95));
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-header-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.chat-header-avatar {
  position: relative;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.chat-header-name {
  font-weight: 700;
  font-size: 16px;
  color: var(--text-primary);
  background: linear-gradient(135deg, var(--primary-color), #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.typing-indicator-text {
  margin-top: 4px;
  margin-left: 72px;
  font-size: 12px;
  color: var(--text-secondary);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  background-image:
    radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.03) 0%, transparent 50%);
  /* 限制高度，超出内部滚动，避免撑破布局 */
  max-height: calc(100vh - 260px);
  box-sizing: border-box;
}

.scroll-to-latest-btn {
  position: absolute;
  right: 32px;
  bottom: 96px;
  z-index: 10;
  width: 46px;
  height: 46px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.25s ease;
  padding: 0;
}

.scroll-to-latest-btn:hover {
  transform: translateY(-2px) scale(1.03);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.3);
}

.scroll-to-latest-icon {
  font-size: 20px;
  margin-right: 0;
}

.scroll-to-latest-text {
  display: none;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.message-item {
  display: flex;
  flex-direction: column;
  max-width: 70%;
  min-width: 0;
  word-wrap: break-word;
}

.message-item--sent {
  align-self: flex-end;
  align-items: flex-end;
}

.message-item--received {
  align-self: flex-start;
  align-items: flex-start;
}

.message-bubble-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  max-width: 100%;
}

.message-bubble-row--sent {
  justify-content: flex-end;
}

.message-content {
  padding: 12px 18px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
  max-width: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-text {
  padding: 12px 18px;
}

.message-item--sent .message-text {
  background: #95ec69;
  color: #000;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.message-item--received .message-text {
  background: rgba(255, 255, 255, 0.98);
  color: var(--text-primary);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-image {
  padding: 0;
  background: transparent;
  box-shadow: none;
  max-width: 300px;
}

.chat-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-image:hover {
  transform: scale(1.02);
}

.message-video {
  padding: 0;
  background: #0f0f0f;
  box-shadow: none;
  max-width: min(100%, 320px);
  width: 100%;
  overflow: hidden;
  border-radius: 12px;
}

.chat-video {
  display: block;
  width: 100%;
  max-width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: contain;
}

.message-emoji {
  padding: 8px 12px;
  background: transparent;
  box-shadow: none;
}

.emoji-large {
  font-size: 48px;
  line-height: 1;
  display: inline-block;
}

.message-item--sent .message-image,
.message-item--sent .message-video,
.message-item--sent .message-emoji {
  border-bottom-right-radius: 4px;
}

.message-item--received .message-image,
.message-item--received .message-video,
.message-item--received .message-emoji {
  border-bottom-left-radius: 4px;
}

.share-card {
  background: radial-gradient(circle at 0 0, rgba(102, 126, 234, 0.06), transparent 55%),
    radial-gradient(circle at 100% 100%, rgba(118, 75, 162, 0.07), transparent 55%),
    #ffffff;
  border: 1px solid rgba(102, 126, 234, 0.18);
  border-radius: 20px;
  padding: 18px 20px;
  cursor: pointer;
  max-width: 520px;
  width: 380px;
  box-shadow: 0 12px 32px rgba(50, 65, 140, 0.12);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.share-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 44px rgba(50, 65, 140, 0.18);
}

.share-card--sent {
  background: #ffffff;
  border-color: rgba(102, 126, 234, 0.18);
  color: var(--text-primary);
  box-shadow: 0 12px 32px rgba(50, 65, 140, 0.12);
}

.share-card-title {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.share-card-title::before {
  content: '📌';
  font-size: 16px;
}

.share-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 800;
  font-size: 15px;
  margin-bottom: 12px;
  letter-spacing: 0.2px;
}

.share-card-hint {
  font-size: 12px;
  color: #000;
  opacity: 0.9;
  padding: 3px 10px;
  border-radius: 999px;
  background-color: rgba(64, 158, 255, 0.06);
}

.share-card--sent .share-card-hint {
  color: #000;
}

.share-card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-start;
}

.share-card-text {
  flex: 1;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-primary);
  word-break: break-word;
  padding: 6px 10px;
  border-radius: 12px;
  background-color: rgba(248, 249, 252, 0.95);
  border: 1px dashed rgba(151, 171, 207, 0.5);
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.share-card--sent .share-card-text {
  color: var(--text-primary);
  background-color: rgba(248, 249, 252, 0.95);
  border-color: rgba(151, 171, 207, 0.5);
}

.share-card-video-tag {
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  color: #e67e22;
  background-color: rgba(230, 126, 34, 0.12);
  border: 1px solid rgba(230, 126, 34, 0.3);
}

.share-card-thumb {
  width: 132px;
  height: 132px;
  object-fit: cover;
  border-radius: 18px;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
}

.share-card-footer {
  margin-top: 10px;
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  padding-top: 8px;
  border-top: 1px dashed rgba(151, 171, 207, 0.5);
}

.share-card-footer-text {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.share-card--sent .share-card-footer {
  color: var(--text-secondary);
}

.share-card-link-tip {
  font-size: 12px;
  color: var(--primary-color);
  font-weight: 600;
}

.share-card--sent .share-card-link-tip {
  color: var(--primary-color);
}

.message-meta {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  margin-top: 4px;
}

.message-meta--sent {
  justify-content: flex-end;
}

.message-time {
  font-size: 11px;
  color: var(--text-placeholder);
}

.message-status {
  font-size: 11px;
  color: var(--text-secondary);
}

.status-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  font-size: 10px;
  line-height: 1;
  border: 1px solid transparent;
}

.status-sent {
  color: var(--text-placeholder);
  border-color: var(--text-placeholder);
}

.status-delivered {
  color: #0fb18f;
  border-color: #0fb18f;
}

.status-read {
  color: #0fb18f;
  border-color: #0fb18f;
}

.chat-input-area {
  padding: var(--spacing-lg);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
}

.emoji-picker {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
  max-height: 200px;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: var(--spacing-xs);
}

.emoji-item {
  font-size: 24px;
  padding: var(--spacing-xs);
  cursor: pointer;
  text-align: center;
  border-radius: 8px;
  transition: all 0.2s;
  user-select: none;
}

.emoji-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: scale(1.2);
}

.input-wrapper {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.input-actions {
  display: flex;
  gap: var(--spacing-xs);
}

.action-icon-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(0, 0, 0, 0.04);
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: scale(1.1);
}

.action-icon-btn--active {
  background: rgba(102, 126, 234, 0.2);
}

.hidden-input {
  display: none;
}

.message-input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid var(--border-lighter);
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message-input:hover {
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.message-input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
  background: #fff;
  transform: translateY(-1px);
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
  z-index: 2000;
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
  transition: transform 0.3s;
}

.close-btn:hover {
  transform: scale(1.2);
}

.preview-image {
  max-width: 100%;
  max-height: 80vh;
  border-radius: var(--border-radius-md);
}

@media (max-width: 968px) {
  .friends-chat-layout {
    grid-template-columns: 1fr;
  }

  .left-sidebar {
    display: none;
  }

  .emoji-grid {
    grid-template-columns: repeat(8, 1fr);
  }
}
</style>

