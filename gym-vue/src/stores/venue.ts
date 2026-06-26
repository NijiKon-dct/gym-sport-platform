import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export interface Venue {
  id: number
  name: string
  address: string
  type: string
  capacity: number
  price: number
  image?: string
  description?: string
  isFavorite?: boolean
  openTime?: string
  closeTime?: string
  school?: string // 所属学校
  isInUse?: boolean // 当前是否正在使用中
  averageRating?: number // 平均评分
  reviewCount?: number // 评价数量
}

export interface Booking {
  id: number
  userId: number
  venueId: number
  venueName: string
  date: string
  startTime: string
  endTime: string
  status: 'pending' | 'confirmed' | 'cancelled' | 'user_cancelled'
  createdAt: string
  price?: number // 预约时的价格
  paid?: boolean
}

export interface VenueReview {
  id: number
  venueId: number
  userId: number
  username: string
  userAvatar?: string
  rating: number
  content?: string
  imagePaths: string[]
  videoPaths: string[]
  createdAt: string
  updatedAt: string
}

export interface VenueReviewStats {
  averageRating: number
  reviewCount: number
}

export const useVenueStore = defineStore('venue', () => {
  const venues = ref<Venue[]>([])
  const currentVenue = ref<Venue | null>(null)
  const bookings = ref<Booking[]>([])
  const loading = ref(false)
  const reviews = ref<VenueReview[]>([])
  const reviewStats = ref<VenueReviewStats | null>(null)

  const filteredVenues = computed(() => venues.value)

  // 根据学校筛选场馆
  const getVenuesBySchool = (school: string) => {
    return venues.value.filter((venue) => venue.school === school)
  }

  const availableVenues = computed(() => {
    return venues.value.filter((venue) => venue.price > 0)
  })

  const fetchVenues = async (userId?: number) => {
    loading.value = true
    try {
      const params = userId ? { params: { userId } } : undefined
      const data = await request.get<Venue[]>('/venues', params)
      venues.value = data
    } finally {
      loading.value = false
    }
  }

  const fetchVenueById = async (id: number, userId?: number) => {
    loading.value = true
    try {
      const params = userId ? { params: { userId } } : undefined
      const data = await request.get<Venue>(`/venues/${id}`, params)
      currentVenue.value = data
    } finally {
      loading.value = false
    }
  }

  const makeBooking = async (bookingData: {
    venueId: number
    date: string
    startTime: string
    endTime: string
    remark?: string
    userId?: number
  }) => {
    const payload = {
      ...bookingData,
      userId: bookingData.userId || 1,
    }
    const newBooking = await request.post<Booking>('/bookings', payload)
    bookings.value.unshift(newBooking)
    return newBooking
  }

  const cancelBooking = async (bookingId: number) => {
    const updated = await request.patch<Booking>(`/bookings/${bookingId}/cancel`)
    bookings.value = bookings.value.map((booking) => (booking.id === bookingId ? updated : booking))
  }

  const confirmBooking = async (bookingId: number) => {
    const updated = await request.patch<Booking>(`/bookings/${bookingId}/confirm`)
    bookings.value = bookings.value.map((booking) => (booking.id === bookingId ? updated : booking))
    return updated
  }

  const payBooking = async (bookingId: number) => {
    const updated = await request.patch<Booking>(`/bookings/${bookingId}/pay`)
    bookings.value = bookings.value.map((booking) => (booking.id === bookingId ? updated : booking))
    return updated
  }

  // 管理员功能：添加场馆
  const addVenue = async (venueData: Omit<Venue, 'id'>) => {
    const created = await request.post<Venue>('/venues', venueData)
    venues.value.push(created)
    return created
  }

  // 管理员功能：更新场馆
  const updateVenue = async (id: number, venueData: Partial<Venue>) => {
    const updated = await request.put<Venue>(`/venues/${id}`, venueData)
    const index = venues.value.findIndex((v) => v.id === id)
    if (index !== -1) {
      venues.value[index] = updated
    }
    if (currentVenue.value && currentVenue.value.id === id) {
      currentVenue.value = updated
    }
    return updated
  }

  // 管理员功能：删除场馆
  const deleteVenue = async (id: number) => {
    await request.delete(`/venues/${id}`)
    const index = venues.value.findIndex((v) => v.id === id)
    if (index !== -1) {
      venues.value.splice(index, 1)
    }
    if (currentVenue.value && currentVenue.value.id === id) {
      currentVenue.value = null
    }
    return true
  }

  const fetchBookings = async (userId?: number) => {
    loading.value = true
    try {
      const params = userId ? { params: { userId } } : undefined
      const data = await request.get<Booking[]>('/bookings', params)
      bookings.value = data
    } finally {
      loading.value = false
    }
  }

  // 获取所有预约数据（管理员统计用）
  const getAllBookings = async (options?: { start?: string; end?: string }): Promise<Booking[]> => {
    if (options?.start && options?.end) {
      const data = await request.get<Booking[]>('/bookings/range', {
        params: { start: options.start, end: options.end },
      })
      return data
    }
    const data = await request.get<Booking[]>('/bookings')
    return data
  }

  // ========== 场馆收藏相关 ==========
  const toggleFavorite = async (venueId: number, userId: number) => {
    const venue = venues.value.find((v) => v.id === venueId) || currentVenue.value
    if (!venue) return

    if (venue.isFavorite) {
      await request.delete(`/venues/${venueId}/favorite`, { params: { userId } })
      venue.isFavorite = false
    } else {
      await request.post(`/venues/${venueId}/favorite`, null, { params: { userId } })
      venue.isFavorite = true
    }
  }

  const addFavorite = async (venueId: number, userId: number) => {
    await request.post(`/venues/${venueId}/favorite`, null, { params: { userId } })
    const venue = venues.value.find((v) => v.id === venueId) || currentVenue.value
    if (venue) {
      venue.isFavorite = true
    }
  }

  const removeFavorite = async (venueId: number, userId: number) => {
    await request.delete(`/venues/${venueId}/favorite`, { params: { userId } })
    const venue = venues.value.find((v) => v.id === venueId) || currentVenue.value
    if (venue) {
      venue.isFavorite = false
    }
  }

  // 获取用户收藏的场馆列表
  const fetchFavoriteVenues = async (userId: number) => {
    loading.value = true
    try {
      const data = await request.get<Venue[]>(`/venues/favorites/list`, { params: { userId } })
      return data
    } finally {
      loading.value = false
    }
  }

  // ========== 场馆评论相关 ==========
  const fetchReviews = async (venueId: number) => {
    try {
      const data = await request.get<VenueReview[]>(`/venues/${venueId}/reviews`)
      reviews.value = data
      return data
    } catch (error) {
      console.error('获取评论失败:', error)
      throw error
    }
  }

  const fetchReviewStats = async (venueId: number) => {
    try {
      const data = await request.get<VenueReviewStats>(`/venues/${venueId}/reviews/stats`)
      reviewStats.value = data
      return data
    } catch (error) {
      console.error('获取评论统计失败:', error)
      throw error
    }
  }

  const createReview = async (
    venueId: number,
    userId: number,
    reviewData: {
      rating: number
      content?: string
      imagePaths?: string[]
      videoPaths?: string[]
    }
  ) => {
    try {
      const data = await request.post<VenueReview>(
        `/venues/${venueId}/reviews`,
        reviewData,
        { params: { userId } }
      )
      reviews.value.unshift(data)
      // 更新统计
      await fetchReviewStats(venueId)
      return data
    } catch (error) {
      console.error('创建评论失败:', error)
      throw error
    }
  }

  const updateReview = async (
    venueId: number,
    reviewId: number,
    userId: number,
    reviewData: {
      rating: number
      content?: string
      imagePaths?: string[]
      videoPaths?: string[]
    }
  ) => {
    try {
      const data = await request.put<VenueReview>(
        `/venues/${venueId}/reviews/${reviewId}`,
        reviewData,
        { params: { userId } }
      )
      const index = reviews.value.findIndex((r) => r.id === reviewId)
      if (index !== -1) {
        reviews.value[index] = data
      }
      // 更新统计
      await fetchReviewStats(venueId)
      return data
    } catch (error) {
      console.error('更新评论失败:', error)
      throw error
    }
  }

  const deleteReview = async (venueId: number, reviewId: number, userId: number) => {
    try {
      await request.delete(`/venues/${venueId}/reviews/${reviewId}`, { params: { userId } })
      reviews.value = reviews.value.filter((r) => r.id !== reviewId)
      // 更新统计
      await fetchReviewStats(venueId)
    } catch (error) {
      console.error('删除评论失败:', error)
      throw error
    }
  }

  // ========== 文件上传相关 ==========
  const uploadBookingPicture = async (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    const data = await request.post<{ path: string; url: string }>(
      '/upload/booking/picture',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    )
    return data
  }

  const uploadBookingPictures = async (files: File[]) => {
    const formData = new FormData()
    files.forEach((file) => {
      formData.append('files', file)
    })
    const data = await request.post<{ paths: string[]; urls: string[] }>(
      '/upload/booking/pictures',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    )
    return data
  }

  const uploadBookingVideo = async (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    const data = await request.post<{ path: string; url: string }>(
      '/upload/booking/video',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    )
    return data
  }

  fetchVenues()

  return {
    venues,
    currentVenue,
    bookings,
    loading,
    reviews,
    reviewStats,
    filteredVenues,
    availableVenues,
    getVenuesBySchool,
    fetchVenues,
    fetchVenueById,
    makeBooking,
    cancelBooking,
    confirmBooking,
    payBooking,
    fetchBookings,
    // 管理员功能
    addVenue,
    updateVenue,
    deleteVenue,
    // 统计功能
    getAllBookings,
    // 收藏功能
    toggleFavorite,
    addFavorite,
    removeFavorite,
    fetchFavoriteVenues,
    // 评论功能
    fetchReviews,
    fetchReviewStats,
    createReview,
    updateReview,
    deleteReview,
    // 文件上传功能
    uploadBookingPicture,
    uploadBookingPictures,
    uploadBookingVideo,
  }
})
