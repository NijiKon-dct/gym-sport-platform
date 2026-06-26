import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'
import { useUserStore } from './user'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

type ChatMessageType = 'text' | 'image' | 'file' | 'emoji' | 'video' | 'share_post'

interface ChatMessage {
  id: number
  chatId: number
  senderId: number
  receiverId: number
  content: string
  type: ChatMessageType
  createdAt: string
  isRead: boolean
  // 本地状态：sent(已发出待送达确认)、delivered(已送达对方设备)、read(对方已读)
  localStatus?: 'sent' | 'delivered' | 'read'
}

interface Chat {
  id: number
  userId: number
  username: string
  avatar?: string
  lastMessage?: string
  lastMessageTime?: string
  unreadCount: number
  isOnline?: boolean
}

export const useChatStore = defineStore('chat', () => {
  const chats = ref<Chat[]>([])
  const currentChatId = ref<number | null>(null)
  const messages = ref<ChatMessage[]>([])
  // 存储所有聊天的消息，确保新消息不会丢失
  const allMessages = ref<Map<number, ChatMessage[]>>(new Map())
  const loading = ref(false)
  const stompClient = ref<Client | null>(null)
  const isWsConnected = ref(false)
  // 正在输入状态：chatId -> 是否对方正在输入
  const typingMap = ref<Map<number, boolean>>(new Map())
  // 在线状态：userId -> 是否在线
  const onlineMap = ref<Map<number, boolean>>(new Map())

  const connectWebSocket = () => {
    if (isWsConnected.value || stompClient.value) return
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) return

    // 使用相对路径，通过 Vite 代理连接到后端 WebSocket
    // 携带 userId，便于后端在 Presence 监听器中识别当前用户
    const socket = new SockJS(`/ws-chat?userId=${encodeURIComponent(userStore.userInfo.id)}`)
    const client = new Client({
      webSocketFactory: () => socket as any,
      reconnectDelay: 5000,
    })

    client.onConnect = () => {
      isWsConnected.value = true
      const userId = userStore.userInfo!.id
      // 订阅个人专属消息 topic：/topic/user.{userId}.messages
      client.subscribe(`/topic/user.${userId}.messages`, (frame) => {
        try {
          const body = JSON.parse(frame.body) as ChatMessage

          // 如果是自己发出的消息，说明服务端已回推，视为送达
          if (body.senderId === userId) {
            body.localStatus = body.isRead ? 'read' : 'delivered'
          }

          // 将新消息存储到allMessages中，确保不丢失
          if (!allMessages.value.has(body.chatId)) {
            allMessages.value.set(body.chatId, [])
          }
          const chatMessages = allMessages.value.get(body.chatId)! as ChatMessage[]
          // 检查消息是否已存在，避免重复；已存在则更新状态
          // 通过ID或内容+发送者+时间戳去重
          const existing = chatMessages.find(msg => 
            msg.id === body.id || 
            (msg.content === body.content && 
             msg.senderId === body.senderId && 
             Math.abs(new Date(msg.createdAt).getTime() - new Date(body.createdAt).getTime()) < 1000)
          )
          
          if (existing) {
            // 如果ID不同但内容相同，更新ID
            if (existing.id !== body.id && body.id) {
              existing.id = body.id
            }
            existing.localStatus = body.localStatus
            existing.isRead = body.isRead
            existing.content = body.content
            existing.type = body.type
            existing.createdAt = body.createdAt
          } else {
            chatMessages.push(body)
          }

          // 如果当前正在看该聊天，更新当前消息列表
          if (currentChatId.value === body.chatId) {
            // 使用去重后的完整列表，避免重复
            messages.value = [...chatMessages]

            // 如果当前用户是接收方，并且正在这个会话窗口里，认为消息已读，立刻上报已读
            if (body.receiverId === userId) {
              // 异步调用，避免阻塞 STOMP 回调
              setTimeout(() => {
                markMessagesAsRead(body.chatId)
              }, 0)
            }
          }

          // 更新聊天列表中的最后一条消息和未读数
          const chat = chats.value.find((c) => c.id === body.chatId)
          if (chat) {
            if (body.type === 'image') {
              chat.lastMessage = '[图片]'
            } else if (body.type === 'emoji') {
              chat.lastMessage = '[表情]'
            } else {
              chat.lastMessage = body.content
            }
            chat.lastMessageTime = body.createdAt

            // 收到对方发来的消息并且不是当前打开的会话时，增加未读数
            if (body.senderId !== userStore.userInfo!.id && currentChatId.value !== body.chatId) {
              chat.unreadCount = (chat.unreadCount || 0) + 1
            }
          }
        } catch (e) {
          // 忽略解析错误
        }
      })
      // 订阅「正在输入」事件：/topic/user.{userId}.typing
      client.subscribe(`/topic/user.${userId}.typing`, (frame) => {
        try {
          const body = JSON.parse(frame.body) as {
            chatId: number
            fromUserId: number
            toUserId: number
            typing: boolean
          }
          if (!body.chatId) return
          typingMap.value.set(body.chatId, body.typing)
        } catch {
          // ignore
        }
      })
      // 订阅已读回执：/topic/user.{userId}.read-receipts
      client.subscribe(`/topic/user.${userId}.read-receipts`, (frame) => {
        try {
          const body = JSON.parse(frame.body) as {
            chatId: number
            readerId: number
            readAt: string
          }
          const chatId = body.chatId
          if (!chatId) return
          // 将该会话中由当前用户发送的消息标记为已读
          const updated = (allMessages.value.get(chatId) || []).map((msg) =>
            msg.senderId === userId ? { ...msg, isRead: true, localStatus: 'read' } : msg,
          )
          allMessages.value.set(chatId, updated)
          if (currentChatId.value === chatId) {
            messages.value = updated
          }
          // 对应聊天的未读数置 0
          const chat = chats.value.find((c) => c.id === chatId)
          if (chat) {
            chat.unreadCount = 0
          }
        } catch {
          // ignore
        }
      })
      // 订阅在线状态广播：/topic/presence
      client.subscribe('/topic/presence', (frame) => {
        try {
          const body = JSON.parse(frame.body) as {
            userId: number
            online: boolean
          }
          if (!body.userId) return
          onlineMap.value.set(body.userId, body.online)
          // 更新聊天列表中的在线状态
          chats.value.forEach((chat) => {
            if (chat.userId === body.userId) {
              chat.isOnline = body.online
            }
          })
        } catch {
          // ignore
        }
      })
    }

    client.onStompError = () => {
      isWsConnected.value = false
    }
    client.onWebSocketClose = () => {
      isWsConnected.value = false
      stompClient.value = null
    }

    client.activate()
    stompClient.value = client
  }

  // 获取聊天列表
  const fetchChats = async () => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) {
      chats.value = []
      messages.value = []
      return
    }
    loading.value = true
    try {
      chats.value = await request.get<Chat[]>('/chats', {
        params: { userId: userStore.userInfo.id },
      })
    } finally {
      loading.value = false
    }

    // 确保在有用户信息时建立 WebSocket 连接
    connectWebSocket()
  }

  // 获取聊天消息
  const fetchMessages = async (chatId: number) => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) return
    loading.value = true
    try {
      // 首先检查allMessages中是否已经有该聊天的消息
      if (allMessages.value.has(chatId)) {
        messages.value = allMessages.value.get(chatId)! as ChatMessage[]
      } else {
        // 如果没有，从服务器获取
        const fetchedMessages = await request.get<ChatMessage[]>(`/chats/${chatId}/messages`)
        const hydrated = fetchedMessages.map((m) => {
          if (m.senderId === userStore.userInfo!.id) {
            return { ...m, localStatus: m.isRead ? 'read' : 'delivered' }
          }
          return m
        })
        messages.value = hydrated
        // 存储到allMessages中
        allMessages.value.set(chatId, hydrated)
      }
      currentChatId.value = chatId
      await markMessagesAsRead(chatId)
    } finally {
      loading.value = false
    }
  }

  // 发送消息
  const sendMessage = async (
    chatId: number,
    receiverId: number,
    content: string,
    type: ChatMessageType = 'text',
  ) => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) throw new Error('请先登录')

    // 将前端的小写类型转换为后端期望的大写枚举值
    const typeMap: Record<string, string> = {
      text: 'TEXT',
      image: 'IMAGE',
      file: 'FILE',
      emoji: 'EMOJI',
      video: 'VIDEO',
      share_post: 'SHARE_POST',
    }

    const payload = {
      chatId,
      senderId: userStore.userInfo.id,
      receiverId,
      content,
      type: typeMap[type] || 'TEXT',
    }
    const newMessage = await request.post<ChatMessage>(`/chats/${chatId}/messages`, payload)
    newMessage.localStatus = 'sent'

    // 更新allMessages中的消息（避免与 WebSocket 回推产生重复）
    if (!allMessages.value.has(chatId)) {
      allMessages.value.set(chatId, [])
    }
    const chatMessages = allMessages.value.get(chatId)! as ChatMessage[]
    
    // 检查消息是否已存在（通过ID或内容+时间戳去重）
    const existing = chatMessages.find((m) => 
      m.id === newMessage.id || 
      (m.content === newMessage.content && 
       m.senderId === newMessage.senderId && 
       Math.abs(new Date(m.createdAt).getTime() - new Date(newMessage.createdAt).getTime()) < 1000)
    )
    
    if (existing) {
      // 如果 WebSocket 已经先推到了同一条消息，只更新状态/内容
      if (existing.id !== newMessage.id && newMessage.id) {
        // 如果ID不同但内容相同，更新ID
        existing.id = newMessage.id
      }
      existing.content = newMessage.content
      existing.type = newMessage.type
      existing.createdAt = newMessage.createdAt
      existing.isRead = newMessage.isRead
      existing.localStatus = newMessage.localStatus
      // 同步到当前消息列表（使用去重后的数组）
      if (currentChatId.value === chatId) {
        messages.value = [...chatMessages]
      }
    } else {
      chatMessages.push(newMessage)
      // 更新当前消息列表
      if (currentChatId.value === chatId) {
        // 再次检查当前消息列表，避免重复
        const existsInCurrent = messages.value.find((m) => 
          m.id === newMessage.id || 
          (m.content === newMessage.content && 
           m.senderId === newMessage.senderId && 
           Math.abs(new Date(m.createdAt).getTime() - new Date(newMessage.createdAt).getTime()) < 1000)
        )
        if (!existsInCurrent) {
          messages.value.push(newMessage)
        }
      }
    }

    // 更新聊天列表中的最后一条消息
    const chat = chats.value.find((c) => c.id === chatId)
    if (chat) {
      // 根据消息类型设置最后一条消息的显示
      if (type === 'image') {
        chat.lastMessage = '[图片]'
      } else if (type === 'emoji') {
        chat.lastMessage = '[表情]'
      } else {
        chat.lastMessage = newMessage.content
      }
      chat.lastMessageTime = newMessage.createdAt
    }
    return newMessage
  }

  // 创建新聊天
  const createChat = async (userId: number, username: string) => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) throw new Error('请先登录')
    const existingChat = chats.value.find((c) => c.userId === userId)
    if (existingChat) {
      return existingChat
    }
    const payload = {
      currentUserId: userStore.userInfo.id,
      targetUserId: userId,
    }
    const newChat = (await request.post<Chat>('/chats', payload)) as Chat
    chats.value.unshift(newChat)
    return newChat
  }

  // 标记消息为已读
  const markMessagesAsRead = async (chatId: number) => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) return
    await request.post(`/chats/${chatId}/read`, {
      chatId,
      userId: userStore.userInfo.id,
    })

    // 更新当前消息列表中的已读状态
    messages.value = messages.value.map((msg) =>
      msg.chatId === chatId && msg.receiverId === userStore.userInfo!.id
        ? { ...msg, isRead: true, localStatus: 'read' }
        : msg,
    )

    // 更新allMessages中的已读状态
    if (allMessages.value.has(chatId)) {
      const chatMessages = allMessages.value.get(chatId)! as ChatMessage[]
      const updatedMessages = chatMessages.map((msg) =>
        msg.chatId === chatId && msg.receiverId === userStore.userInfo!.id
          ? { ...msg, isRead: true, localStatus: 'read' }
          : msg,
      )
      allMessages.value.set(chatId, updatedMessages)
    }

    // 清除未读数
    const chat = chats.value.find((c) => c.id === chatId)
    if (chat) {
      chat.unreadCount = 0
    }
  }

  // 获取当前聊天
  const currentChat = computed(() => {
    if (!currentChatId.value) return null
    return chats.value.find((c) => c.id === currentChatId.value) || null
  })

  // 初始化时加载聊天列表
  fetchChats()

  return {
    chats,
    currentChatId,
    currentChat,
    messages,
    loading,
    fetchChats,
    fetchMessages,
    sendMessage,
    createChat,
    markMessagesAsRead,
    typingMap,
    onlineMap,
    connectWebSocket,
  }
})

