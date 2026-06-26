import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export interface Topic {
  id: number
  name: string
  description?: string
  postCount: number
  subscriberCount: number
  isSubscribed?: boolean
  createdAt: string
}

export const useTopicStore = defineStore('topic', () => {
  const topics = ref<Topic[]>([])
  const subscribedTopics = ref<Topic[]>([])
  const currentTopic = ref<Topic | null>(null)
  const loading = ref(false)

  // 获取话题列表
  const fetchTopics = async (keyword?: string) => {
    loading.value = true
    try {
      const userStore = await import('@/stores/user')
      const currentUserId = userStore.useUserStore().userInfo?.id
      const params: any = {}
      if (keyword) params.keyword = keyword
      if (currentUserId) params.currentUserId = currentUserId
      const data = await request.get<Topic[]>('/topics', { params })
      topics.value = data
    } catch (error) {
      console.error('获取话题列表失败:', error)
      topics.value = []
    } finally {
      loading.value = false
    }
  }

  // 获取订阅的话题列表
  const fetchSubscribedTopics = async (userId: number) => {
    try {
      const data = await request.get<Topic[]>('/topics/subscribed', {
        params: { userId },
      })
      subscribedTopics.value = data
    } catch (error) {
      console.error('获取订阅话题失败:', error)
      subscribedTopics.value = []
    }
  }

  // 获取话题详情
  const fetchTopic = async (topicId: number) => {
    try {
      const userStore = await import('@/stores/user')
      const currentUserId = userStore.useUserStore().userInfo?.id
      const params: any = {}
      if (currentUserId) params.currentUserId = currentUserId
      const data = await request.get<Topic>(`/topics/${topicId}`, { params })
      currentTopic.value = data
      return data
    } catch (error) {
      console.error('获取话题详情失败:', error)
      return null
    }
  }

  // 根据名称获取话题
  const fetchTopicByName = async (topicName: string) => {
    try {
      const userStore = await import('@/stores/user')
      const currentUserId = userStore.useUserStore().userInfo?.id
      const params: any = {}
      if (currentUserId) params.currentUserId = currentUserId
      const name = topicName.startsWith('#') ? topicName.substring(1) : topicName
      const data = await request.get<Topic>(`/topics/name/${encodeURIComponent(name)}`, { params })
      currentTopic.value = data
      return data
    } catch (error) {
      console.error('获取话题失败:', error)
      return null
    }
  }

  // 订阅话题
  const subscribeTopic = async (topicId: number, userId: number) => {
    try {
      const data = await request.post<Topic>(`/topics/${topicId}/subscribe`, null, {
        params: { userId },
      })
      // 更新话题列表中的订阅状态
      const topic = topics.value.find((t) => t.id === topicId)
      if (topic) {
        topic.isSubscribed = true
        topic.subscriberCount += 1
      }
      // 更新当前话题
      if (currentTopic.value?.id === topicId) {
        currentTopic.value.isSubscribed = true
        currentTopic.value.subscriberCount += 1
      }
      // 添加到订阅列表
      if (!subscribedTopics.value.find((t) => t.id === topicId)) {
        subscribedTopics.value.push(data)
      }
      return data
    } catch (error) {
      console.error('订阅话题失败:', error)
      throw error
    }
  }

  // 取消订阅话题
  const unsubscribeTopic = async (topicId: number, userId: number) => {
    try {
      const data = await request.post<Topic>(`/topics/${topicId}/unsubscribe`, null, {
        params: { userId },
      })
      // 更新话题列表中的订阅状态
      const topic = topics.value.find((t) => t.id === topicId)
      if (topic) {
        topic.isSubscribed = false
        topic.subscriberCount = Math.max(0, topic.subscriberCount - 1)
      }
      // 更新当前话题
      if (currentTopic.value?.id === topicId) {
        currentTopic.value.isSubscribed = false
        currentTopic.value.subscriberCount = Math.max(0, currentTopic.value.subscriberCount - 1)
      }
      // 从订阅列表移除
      subscribedTopics.value = subscribedTopics.value.filter((t) => t.id !== topicId)
      return data
    } catch (error) {
      console.error('取消订阅话题失败:', error)
      throw error
    }
  }

  // 从内容中提取话题标签（#话题名）
  const extractTopics = (content: string): string[] => {
    const topicRegex = /#([^\s#]+)/g
    const matches = content.matchAll(topicRegex)
    const topics: string[] = []
    for (const match of matches) {
      const topicName = match[1].trim()
      if (topicName && !topics.includes(topicName)) {
        topics.push(topicName)
      }
    }
    return topics
  }

  return {
    topics,
    subscribedTopics,
    currentTopic,
    loading,
    fetchTopics,
    fetchSubscribedTopics,
    fetchTopic,
    fetchTopicByName,
    subscribeTopic,
    unsubscribeTopic,
    extractTopics,
  }
})
















