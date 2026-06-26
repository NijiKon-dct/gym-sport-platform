import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import { useUserStore } from './user'
import type { UserInfo } from './user'
import { useChatStore } from './chat'

interface Friend {
  id: number
  username: string
  nickname?: string
  avatar?: string
  school?: string
  isOnline?: boolean
  lastSeen?: string
}

interface FriendRequest {
  id: number
  fromUserId: number
  fromUsername: string
  fromAvatar?: string
  message?: string
  status: 'pending' | 'accepted' | 'rejected'
  createdAt: string
}

export const useFriendsStore = defineStore('friends', () => {
  const friends = ref<Friend[]>([])
  const friendRequests = ref<FriendRequest[]>([])
  const loading = ref(false)
  const chatStore = useChatStore()

  // 获取朋友列表
  const fetchFriends = async () => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) {
      friends.value = []
      return
    }
    loading.value = true
    try {
      const data = (await request.get<Friend[]>('/friends', {
        params: { userId: userStore.userInfo.id },
      })) as unknown as Friend[]
      friends.value = data
    } finally {
      loading.value = false
    }
  }

  // 获取好友请求列表
  const fetchFriendRequests = async () => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) {
      friendRequests.value = []
      return
    }
    loading.value = true
    try {
      const data = (await request.get<FriendRequest[]>('/friend-requests', {
        params: { userId: userStore.userInfo.id },
      })) as unknown as FriendRequest[]
      friendRequests.value = data
    } finally {
      loading.value = false
    }
  }

  // 搜索用户
  const searchUsers = async (keyword: string) => {
    loading.value = true
    try {
      const results = (await request.get<UserInfo[]>('/users/search', {
        params: { keyword },
      })) as unknown as UserInfo[]
      return results.map((user) => ({
        id: user.id,
        username: user.username,
        nickname: user.nickname,
        avatar: user.avatar,
        school: user.school,
          isOnline: false,
      }))
    } finally {
      loading.value = false
    }
  }

  // 发送好友请求
  const sendFriendRequest = async (userId: number, message?: string, targetUsername?: string) => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) throw new Error('请先登录')
    const payload = {
      fromUserId: userStore.userInfo.id,
      toUserId: userId,
      message,
    }
    const created = (await request.post<FriendRequest>('/friend-requests', payload)) as unknown as FriendRequest
    // 当前客户端只负责发起请求，真正的系统消息由后端统一发送，避免重复
    return created
  }

  // 接受好友请求
  const acceptFriendRequest = async (requestId: number) => {
    const friendRequest = friendRequests.value.find((r) => r.id === requestId)
    if (!friendRequest) return

    const newFriend = (await request.post<Friend>(`/friend-requests/${requestId}/accept`)) as unknown as Friend
    
    // 添加新好友到列表（如果不存在）
    if (!friends.value.some((f) => f.id === newFriend.id)) {
      friends.value.push(newFriend)
    }
    
    // 移除已处理的好友请求
    friendRequests.value = friendRequests.value.filter((r) => r.id !== requestId)
    
    // 刷新好友列表，确保双方都看到对方
    await fetchFriends()
  }

  // 拒绝好友请求
  const rejectFriendRequest = async (requestId: number) => {
    await request.post(`/friend-requests/${requestId}/reject`)
    friendRequests.value = friendRequests.value.filter((r) => r.id !== requestId)
  }

  // 删除好友
  const removeFriend = async (friendId: number) => {
    const userStore = useUserStore()
    if (!userStore.userInfo?.id) return
    await request.delete('/friends', {
      params: { userId: userStore.userInfo.id, friendId },
    })
    friends.value = friends.value.filter((f) => f.id !== friendId)
  }

  // 初始化时加载朋友列表
  fetchFriends()
  fetchFriendRequests()

  return {
    friends,
    friendRequests,
    loading,
    fetchFriends,
    fetchFriendRequests,
    searchUsers,
    sendFriendRequest,
    acceptFriendRequest,
    rejectFriendRequest,
    removeFriend,
  }
})

