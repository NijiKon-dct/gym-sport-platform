<template>
  <div class="chat-view page-container">
    <div class="container">
      <div class="chat-layout">
        <!-- 左侧：聊天列表 -->
        <div class="chat-sidebar">
          <div class="chat-list-header">
            <h2 class="sidebar-title">聊天</h2>
            <RouterLink to="/friends" class="friends-link" title="好友列表">
              👥
            </RouterLink>
          </div>
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else class="chat-list">
            <div
              v-for="chat in chats"
              :key="chat.id"
              :class="['chat-item', { 'chat-item--active': currentChatId === chat.id }]"
              @click="handleSelectChat(chat.id)"
            >
              <div class="chat-avatar">
                {{ chat.username[0] }}
                <span
                  v-if="chat.isOnline"
                  class="online-indicator"
                  title="在线"
                ></span>
              </div>
              <div class="chat-info">
                <div class="chat-name-row">
                  <span class="chat-name">{{ chat.username }}</span>
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

        <!-- 右侧：聊天窗口 -->
        <div class="chat-main">
          <div v-if="!currentChatId" class="chat-empty">
            <p>选择一个聊天开始对话</p>
          </div>
          <div v-else class="chat-window">
            <!-- 聊天头部 -->
            <div class="chat-header">
              <div class="chat-header-info">
                <div class="chat-header-avatar">
                  {{ currentChat?.username[0] }}
                  <span
                    v-if="currentChat?.isOnline"
                    class="online-indicator"
                  ></span>
                </div>
                <div class="chat-header-name">{{ currentChat?.username }}</div>
              </div>
              <div
                v-if="currentChatId && typingMap.get(currentChatId)"
                class="typing-indicator-text"
              >
                对方正在输入…
              </div>
            </div>

            <!-- 消息列表 -->
            <div class="messages-container" ref="messagesContainer">
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
                      <img :src="getFileUrl(message.content)" alt="图片" class="chat-image" />
                    </div>
                    <div v-else-if="message.type === 'video'" class="message-content message-video">
                      <video :src="getFileUrl(message.content)" controls class="chat-video"></video>
                    </div>
                    <div v-else class="message-content">{{ message.content }}</div>
                  </div>
                  <div v-else class="message-bubble-row">
                    <div v-if="message.type === 'image'" class="message-content message-image">
                      <img :src="getFileUrl(message.content)" alt="图片" class="chat-image" />
                    </div>
                    <div v-else-if="message.type === 'video'" class="message-content message-video">
                      <video :src="getFileUrl(message.content)" controls class="chat-video"></video>
                    </div>
                    <div v-else class="message-content">{{ message.content }}</div>
                  </div>

                  <div
                    v-if="message.senderId === currentUserId"
                    class="message-time-row message-time-row--sent"
                  >
                    <div class="message-time">
                      {{ formatMessageTime(message.createdAt) }}
                    </div>
                  </div>
                  <div v-else class="message-time-row">
                    <div class="message-time">
                      {{ formatMessageTime(message.createdAt) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 输入框 -->
            <div class="chat-input-area">
              <div class="input-wrapper">
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
                  :disabled="!inputMessage.trim()"
                  @click="handleSendMessage"
                >
                  发送
                </BaseButton>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'
import { useUserStore } from '@/stores/user'
import { BaseButton } from '@/components/common'

const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()
const userStore = useUserStore()

const chats = computed(() => chatStore.chats)
const currentChatId = computed(() => chatStore.currentChatId)
const currentChat = computed(() => chatStore.currentChat)
const messages = computed(() => chatStore.messages)
const loading = computed(() => chatStore.loading)
const currentUserId = computed(() => userStore.userInfo?.id || 1)
const typingMap = computed(() => chatStore.typingMap)
const onlineMap = computed(() => chatStore.onlineMap)

const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

const getFileUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
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

const formatMessageTime = (timeString: string) => {
  const date = new Date(timeString)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
  })
}

const handleSelectChat = async (chatId: number) => {
  await chatStore.fetchMessages(chatId)
  scrollToBottom()
}

const handleSendMessage = async () => {
  if (!inputMessage.value.trim() || !currentChatId.value) return

  const chat = currentChat.value
  if (!chat) return

  await chatStore.sendMessage(currentChatId.value, chat.userId, inputMessage.value)
  inputMessage.value = ''
  await nextTick()
  scrollToBottom()
}

// 输入中事件
const typingTimer = ref<number | null>(null)
const handleInputChange = () => {
  if (!currentUserId.value || !currentChatId.value || !currentChat.value) return
  const receiverId = currentChat.value.userId
  const client = (chatStore as any).stompClient?.value as import('@stomp/stompjs').Client | null
  if (!client || !client.connected) return

  const payload = JSON.stringify({
    chatId: currentChatId.value,
    fromUserId: currentUserId.value,
    toUserId: receiverId,
    typing: true,
  })
  client.publish({ destination: '/app/chat/typing', body: payload })

  if (typingTimer.value) window.clearTimeout(typingTimer.value)
  typingTimer.value = window.setTimeout(() => {
    const stopPayload = JSON.stringify({
      chatId: currentChatId.value,
      fromUserId: currentUserId.value,
      toUserId: receiverId,
      typing: false,
    })
    client.publish({ destination: '/app/chat/typing', body: stopPayload })
  }, 2000)
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
})

// 如果路由中有userId参数，创建或选择聊天
onMounted(async () => {
  // 确保WebSocket连接已建立
  chatStore.connectWebSocket()
  const userId = route.params.userId ? Number(route.params.userId) : null
  if (userId) {
    const chat = chats.value.find((c) => c.userId === userId)
    if (chat) {
      await handleSelectChat(chat.id)
    } else {
      // 创建新聊天
      const newChat = await chatStore.createChat(userId, '用户')
      await handleSelectChat(newChat.id)
    }
  }
})
</script>

<style scoped>
.chat-view {
  background-color: var(--bg-color-page);
  height: calc(100vh - 200px);
}

.chat-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: var(--spacing-lg);
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.chat-sidebar {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-lighter);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.friends-link {
  font-size: 20px;
  text-decoration: none;
  cursor: pointer;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid var(--border-lighter);
}

.chat-item:hover {
  background-color: var(--bg-color-page);
}

.chat-item--active {
  background-color: var(--primary-color);
  color: #fff;
}

.chat-item--active .chat-name,
.chat-item--active .chat-last-message,
.chat-item--active .chat-time {
  color: #fff;
}

.chat-avatar {
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

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: var(--success-color);
  border: 2px solid var(--bg-color);
}

.chat-item--active .online-indicator {
  border-color: var(--primary-color);
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.chat-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.unread-badge {
  background-color: var(--danger-color);
  color: #fff;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: 600;
}

.chat-last-message {
  font-size: 13px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.chat-time {
  font-size: 11px;
  color: var(--text-placeholder);
}

.chat-main {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-secondary);
}

.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-lighter);
  background-color: var(--bg-color);
}

.chat-header-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.chat-header-avatar {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.chat-header-name {
  font-weight: 600;
  color: var(--text-primary);
}

.typing-indicator-text {
  margin-top: 4px;
  margin-left: 60px;
  font-size: 12px;
  color: var(--text-secondary);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
  background-color: var(--bg-color-page);
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
  word-wrap: break-word;
}

.message-bubble-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-bubble-row--sent {
  justify-content: flex-end;
}

.message-item--sent {
  align-self: flex-end;
  align-items: flex-end;
}

.message-item--received {
  align-self: flex-start;
  align-items: flex-start;
}

.message-content {
  padding: 10px 15px;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  line-height: 1.5;
}

.message-item--sent .message-content {
  background-color: var(--primary-color);
  color: #fff;
}

.message-item--received .message-content {
  background-color: var(--bg-color);
  color: var(--text-primary);
  border: 1px solid var(--border-lighter);
}

.message-time {
  font-size: 11px;
  color: var(--text-placeholder);
  margin-top: 4px;
  padding: 0 4px;
}

.message-time-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.message-time-row--sent {
  justify-content: flex-end;
}

.message-status {
  display: flex;
  align-items: center;
  gap: 4px;
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
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-lighter);
  background-color: var(--bg-color);
}

.input-wrapper {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.message-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.message-input:focus {
  border-color: var(--primary-color);
}

@media (max-width: 968px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }

  .chat-sidebar {
    display: none;
  }
}
</style>

