import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

interface Post {
  id: number
  userId: number
  username: string
  avatar?: string
  content: string
  images?: string[]
  video?: string
  venueId?: number
  venueName?: string
  topics?: string[]
  mentionedUserIds?: number[]
  likes: number
  comments: number
  isLiked: boolean
  isPinned?: boolean
  isFeatured?: boolean
  isHot?: boolean
  hotScore?: number
  createdAt: string
}

interface Comment {
  id: number
  postId: number
  userId: number
  username: string
  avatar?: string
  content: string
  likes: number
  isLiked: boolean
  parentCommentId?: number
  parentCommentUsername?: string
  replies?: Comment[]
  createdAt: string
}

interface User {
  id: number
  username: string
  avatar?: string
  school?: string
  isFollowing?: boolean
  followers?: number
  following?: number
}

export const useSocialStore = defineStore('social', () => {
  const posts = ref<Post[]>([])
  const comments = ref<Comment[]>([])
  const followingUsers = ref<User[]>([])
  const loading = ref(false)

  // 获取动态列表
  const fetchPosts = async (topicId?: number, sortBy?: string) => {
    loading.value = true
    try {
      const userStore = await import('@/stores/user')
      const currentUserId = userStore.useUserStore().userInfo?.id
      const params: any = {}
      if (currentUserId) params.currentUserId = currentUserId
      if (topicId) params.topicId = topicId
      if (sortBy) params.sortBy = sortBy
      const data = await request.get<Post[]>('/posts', { params })
      posts.value = data
    } catch (error) {
      posts.value = []
    } finally {
      loading.value = false
    }
  }

  // 获取评论列表
  const fetchComments = async (postId: number) => {
    try {
      const userStore = await import('@/stores/user')
      const currentUserId = userStore.useUserStore().userInfo?.id
      const params: any = {}
      if (currentUserId) params.currentUserId = currentUserId
      const data = await request.get<Comment[]>(`/posts/${postId}/comments`, { params })
      // 更新评论列表，移除该动态的旧评论，添加新评论
      comments.value = comments.value.filter((c) => c.postId !== postId)
      comments.value.push(...data)
      return data
    } catch (error) {
      console.error('获取评论失败', error)
      return comments.value.filter((c) => c.postId === postId)
    }
  }

  // 发布动态
  const createPost = async (postData: {
    content: string
    images?: string[]
    video?: string
    venueId?: number
    venueName?: string
    topics?: string[]
    mentionedUserIds?: number[]
    userId?: number
    username?: string
    avatar?: string
  }) => {
    if (!postData.userId) {
      throw new Error('用户ID不能为空')
    }
    // 只使用传入的话题，不再从内容中提取
    const payload: any = {
      userId: postData.userId,
      content: postData.content,
      venueId: postData.venueId && postData.venueId > 0 ? postData.venueId : null,
      topics: postData.topics && postData.topics.length > 0 ? postData.topics : null,
      mentionedUserIds: postData.mentionedUserIds && postData.mentionedUserIds.length > 0 ? postData.mentionedUserIds : null,
    }
    if (postData.images && postData.images.length > 0) {
      payload.images = postData.images
    }
    if (postData.video) {
      payload.video = postData.video
    }
    const newPost = await request.post<Post>('/posts', payload)
    posts.value.unshift(newPost)
    return newPost
  }

  // 点赞/取消点赞
  const toggleLike = async (postId: number) => {
    const userStore = await import('@/stores/user')
    const userId = userStore.useUserStore().userInfo?.id
    if (!userId) {
      throw new Error('请先登录')
    }
    const updatedPost = await request.post<Post>(`/posts/${postId}/like`, null, {
      params: { userId },
    })
    const index = posts.value.findIndex((p) => p.id === postId)
    if (index !== -1) {
      posts.value[index] = updatedPost
    }
  }

  // 添加评论
  const addComment = async (postId: number, content: string, parentCommentId?: number) => {
    const userStore = await import('@/stores/user')
    const userId = userStore.useUserStore().userInfo?.id
    if (!userId) {
      throw new Error('请先登录')
    }
    const payload: any = {
      userId,
      content,
    }
    if (parentCommentId) {
      payload.parentCommentId = parentCommentId
    }
    const newComment = await request.post<Comment>(`/posts/${postId}/comments`, payload)
    // 如果是回复，需要更新父评论的回复列表
    if (parentCommentId) {
      const parentComment = comments.value.find((c) => c.id === parentCommentId)
      if (parentComment) {
        if (!parentComment.replies) {
          parentComment.replies = []
        }
        parentComment.replies.push(newComment)
      }
    } else {
      comments.value.push(newComment)
    }
    const post = posts.value.find((p) => p.id === postId)
    if (post) {
      post.comments += 1
    }
    return newComment
  }

  // 关注/取消关注用户
  const toggleFollow = async (userId: number) => {
    const user = followingUsers.value.find((u) => u.id === userId)
    if (user) {
      user.isFollowing = !user.isFollowing
    }
  }

  // 删除动态
  const deletePost = async (postId: number) => {
    const userStore = await import('@/stores/user')
    const userId = userStore.useUserStore().userInfo?.id
    if (!userId) {
      throw new Error('请先登录')
    }
    await request.delete(`/posts/${postId}`, {
      params: { currentUserId: userId },
    })
    posts.value = posts.value.filter((p) => p.id !== postId)
    // 同时删除相关评论
    comments.value = comments.value.filter((c) => c.postId !== postId)
  }

  // 更新动态
  const updatePost = async (
    postId: number,
    postData: {
      content?: string
      images?: string[]
      video?: string
      venueId?: number
      venueName?: string
      topics?: string[]
      mentionedUserIds?: number[]
    },
  ) => {
    const userStore = await import('@/stores/user')
    const userId = userStore.useUserStore().userInfo?.id
    if (!userId) {
      throw new Error('请先登录')
    }
    // 只使用传入的话题，不再从内容中提取
    const payload: any = {}
    if (postData.content !== undefined) payload.content = postData.content
    if (postData.images !== undefined) payload.images = postData.images
    if (postData.video !== undefined) payload.video = postData.video
    if (postData.venueId !== undefined) payload.venueId = postData.venueId && postData.venueId > 0 ? postData.venueId : null
    if (postData.topics && postData.topics.length > 0) payload.topics = postData.topics
    if (postData.mentionedUserIds !== undefined) payload.mentionedUserIds = postData.mentionedUserIds && postData.mentionedUserIds.length > 0 ? postData.mentionedUserIds : null
    const updatedPost = await request.put<Post>(`/posts/${postId}`, payload, {
      params: { currentUserId: userId },
    })
    const index = posts.value.findIndex((p) => p.id === postId)
    if (index !== -1) {
      posts.value[index] = updatedPost
    }
  }

  // 设置/取消置顶（管理员功能）
  const setPinned = async (postId: number, pinned: boolean) => {
    const userStore = await import('@/stores/user')
    const userId = userStore.useUserStore().userInfo?.id
    if (!userId) {
      throw new Error('请先登录')
    }
    const updatedPost = await request.put<Post>(`/posts/${postId}/pin`, null, {
      params: { currentUserId: userId, pinned },
    })
    const index = posts.value.findIndex((p) => p.id === postId)
    if (index !== -1) {
      posts.value[index] = updatedPost
    }
    return updatedPost
  }

  // 设置/取消精华（管理员功能）
  const setFeatured = async (postId: number, featured: boolean) => {
    const userStore = await import('@/stores/user')
    const userId = userStore.useUserStore().userInfo?.id
    if (!userId) {
      throw new Error('请先登录')
    }
    const updatedPost = await request.put<Post>(`/posts/${postId}/feature`, null, {
      params: { currentUserId: userId, featured },
    })
    const index = posts.value.findIndex((p) => p.id === postId)
    if (index !== -1) {
      posts.value[index] = updatedPost
    }
    return updatedPost
  }

  // 初始化时加载动态
  fetchPosts()

  return {
    posts,
    comments,
    followingUsers,
    loading,
    fetchPosts,
    fetchComments,
    createPost,
    toggleLike,
    addComment,
    toggleFollow,
    deletePost,
    updatePost,
    setPinned,
    setFeatured,
  }
})
