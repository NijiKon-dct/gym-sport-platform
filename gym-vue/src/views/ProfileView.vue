<template>
  <div class="profile-view page-container">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-icon">👤</span>
          个人中心
        </h1>
        <p class="page-subtitle">管理您的个人信息和账户设置</p>
      </div>

      <div v-if="!userInfo" class="empty-state">
        <div class="empty-icon">🔒</div>
        <p class="empty-text">请先登录</p>
        <RouterLink to="/login">
          <BaseButton type="primary">去登录</BaseButton>
        </RouterLink>
      </div>
      <div v-else class="profile-content">
        <!-- 主要信息卡片 -->
        <div class="profile-card main-card">
          <div class="profile-header">
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <div class="avatar-border"></div>
                <img
                  v-if="getAvatarUrl(avatarPreview || userInfo.avatar)"
                  :src="getAvatarUrl(avatarPreview || userInfo.avatar)"
                  alt="头像"
                  class="avatar-image"
                />
                <div v-else class="avatar-placeholder">
                  {{ userInfo.username?.[0]?.toUpperCase() || '👤' }}
                </div>
                <div class="avatar-overlay">
                  <input
                    ref="avatarInput"
                    type="file"
                    accept="image/*"
                    class="avatar-input"
                    @change="handleAvatarChange"
                  />
                  <button class="avatar-upload-btn" @click="triggerAvatarUpload" title="更换头像">
                    <span class="upload-icon">📷</span>
                  </button>
                </div>
              </div>
              <p class="avatar-hint">点击上传头像</p>
            </div>
            <div class="user-info">
              <div class="user-name-section">
                <h2 class="username">{{ userInfo.nickname || userInfo.username }}</h2>
                <span class="user-id">ID: {{ userInfo.id }}</span>
              </div>
              <p class="email">
                <span class="info-icon">📧</span>
                {{ userInfo.email }}
              </p>
              <div class="identity">
                <span class="identity-label">身份：</span>
                <span class="identity-tag" :class="identityClass">
                  <span class="tag-dot"></span>
                  {{ identityText }}
                </span>
              </div>
              <!-- 动态统计 -->
              <div class="stats-section">
                <div class="stat-item">
                  <div class="stat-icon">❤️</div>
                  <div class="stat-content">
                    <div class="stat-value">{{ totalLikes }}</div>
                    <div class="stat-label">总获赞数</div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-icon">📝</div>
                  <div class="stat-content">
                    <div class="stat-value">{{ myPostsCount }}</div>
                    <div class="stat-label">发布动态</div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-icon">📅</div>
                  <div class="stat-content">
                    <div class="stat-value">{{ myBookingsAll.length }}</div>
                    <div class="stat-label">预约次数</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 快速统计卡片 -->
          <div class="quick-stats">
            <div class="quick-stat" v-for="(stat, index) in quickStats" :key="stat.label" :style="{ animationDelay: `${index * 0.1}s` }">
              <div class="quick-stat-icon">{{ getStatIcon(stat.label) }}</div>
              <div class="quick-stat-content">
                <div class="quick-stat-value">{{ stat.value }}</div>
                <div class="quick-stat-label">{{ stat.label }}</div>
                <div class="quick-stat-desc">{{ stat.desc }}</div>
              </div>
            </div>
          </div>

          <!-- 详细信息表单 -->
          <div class="profile-details">
            <h3 class="section-title">
              <span class="section-icon">📋</span>
              个人信息
            </h3>
            <div class="form-grid">
              <BaseFormItem label="姓名">
                <BaseInput
                  v-model="formData.username"
                  placeholder="请输入您的真实姓名"
                  :error="errors.username"
                />
              </BaseFormItem>
              <BaseFormItem label="邮箱">
                <BaseInput :model-value="userInfo.email" disabled />
              </BaseFormItem>
              <BaseFormItem label="昵称">
                <BaseInput v-model="formData.nickname" placeholder="请输入昵称" />
              </BaseFormItem>
              <BaseFormItem label="电话">
                <BaseInput v-model="formData.phone" type="tel" placeholder="请输入手机号" />
              </BaseFormItem>
              <BaseFormItem label="性别">
                <BaseInput :model-value="genderText" disabled />
              </BaseFormItem>
              <!-- 学生：显示学生号+学校，均不可修改 -->
              <BaseFormItem v-if="isStudent" label="学生号">
                <BaseInput :model-value="userInfo.studentNumber || '—'" disabled />
              </BaseFormItem>
              <BaseFormItem v-if="isStudent" label="学校">
                <BaseInput :model-value="userInfo.school || '—'" disabled />
              </BaseFormItem>
              <!-- 教职工：显示教职工号+学校，均不可修改 -->
              <template v-else-if="isStaff">
                <BaseFormItem label="教职工号">
                  <BaseInput :model-value="userInfo.studentNumber || '—'" disabled />
                </BaseFormItem>
                <BaseFormItem label="学校">
                  <BaseInput :model-value="userInfo.school || '—'" disabled />
                </BaseFormItem>
              </template>
              <!-- 普通用户：不提供学校选择，仅展示占位信息 -->
              <BaseFormItem v-else label="学校">
                <BaseInput model-value="普通用户无需选择学校" disabled />
              </BaseFormItem>
              <BaseFormItem label="个人简介" class="full-width">
                <textarea
                  v-model="formData.bio"
                  class="bio-textarea"
                  placeholder="介绍一下自己..."
                  rows="4"
                ></textarea>
              </BaseFormItem>
            </div>
          </div>

          <div class="profile-actions">
            <BaseButton type="primary" @click="handleSave" :loading="saving">
              <span v-if="!saving">💾</span>
              保存修改
            </BaseButton>
            <BaseButton type="secondary" @click="handleLogout">
              <span>🚪</span>
              退出登录
            </BaseButton>
          </div>
        </div>

        <div v-if="!isAdmin" class="profile-widgets">
          <!-- 最近预约卡片 -->
          <div class="widget-card">
            <div class="widget-header">
              <h3>
                <span class="widget-icon">📅</span>
                最近预约
              </h3>
              <RouterLink to="/bookings" class="widget-link">
                查看全部
                <span class="link-arrow">→</span>
              </RouterLink>
            </div>
            <div v-if="venueStore.loading" class="widget-loading">
              <div class="loading-spinner"></div>
              <p>加载中...</p>
            </div>
            <div v-else-if="myBookings.length === 0" class="widget-empty">
              <div class="empty-icon-small">📭</div>
              <p>还没有预约记录</p>
              <p class="empty-hint">去看看场馆吧～</p>
            </div>
            <ul v-else class="booking-list">
              <li v-for="booking in myBookings" :key="booking.id" class="booking-item">
                <div class="booking-info">
                  <div class="booking-name">{{ booking.venueName }}</div>
                  <div class="booking-time">
                    <span class="time-icon">🕐</span>
                    {{ formatDate(booking.date) }} · {{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }}
                  </div>
                  <div v-if="booking.price" class="booking-price">
                    <span class="price-icon">💰</span>
                    ¥{{ booking.price.toFixed(2) }}
                    <span v-if="booking.paid" class="paid-badge">已支付</span>
                  </div>
                </div>
                <span class="booking-status" :class="booking.status">
                  <span class="status-dot"></span>
                  {{ statusMap[booking.status] }}
                </span>
              </li>
            </ul>
          </div>

          <!-- 成就徽章卡片 -->
          <div class="widget-card">
            <div class="widget-header">
              <h3>
                <span class="widget-icon">🏆</span>
                成就徽章
              </h3>
              <span class="widget-tip">{{ activeBadgeCount }}/{{ achievementBadges.length }} 已解锁</span>
            </div>
            <div class="badge-grid">
              <div
                v-for="badge in achievementBadges"
                :key="badge.name"
                class="badge-card"
                :class="{ active: badge.active }"
              >
                <div class="badge-icon-wrapper">
                  <div class="badge-icon">{{ badge.icon }}</div>
                  <div v-if="badge.active" class="badge-check">✓</div>
                </div>
                <div class="badge-name">{{ badge.name }}</div>
                <div class="badge-desc">{{ badge.desc }}</div>
              </div>
            </div>
          </div>

          <!-- 快捷入口卡片 -->
          <div class="widget-card">
            <div class="widget-header">
              <h3>
                <span class="widget-icon">⚡</span>
                快捷入口
              </h3>
            </div>
            <div class="shortcut-grid">
              <RouterLink to="/venues" class="shortcut-item">
                <div class="shortcut-icon venue-icon">🏟️</div>
                <div class="shortcut-content">
                  <div class="shortcut-title">预约场馆</div>
                  <div class="shortcut-desc">浏览全部场馆</div>
                </div>
                <div class="shortcut-arrow">→</div>
              </RouterLink>
              <RouterLink to="/social" class="shortcut-item">
                <div class="shortcut-icon social-icon">💬</div>
                <div class="shortcut-content">
                  <div class="shortcut-title">发布动态</div>
                  <div class="shortcut-desc">分享运动日常</div>
                </div>
                <div class="shortcut-arrow">→</div>
              </RouterLink>
              <RouterLink to="/friends" class="shortcut-item">
                <div class="shortcut-icon friends-icon">🤝</div>
                <div class="shortcut-content">
                  <div class="shortcut-title">好友互动</div>
                  <div class="shortcut-desc">结识运动伙伴</div>
                </div>
                <div class="shortcut-arrow">→</div>
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import { useSocialStore } from '@/stores/social'
import { useVenueStore } from '@/stores/venue'
import { BaseButton, BaseInput, BaseFormItem, BaseSelect } from '@/components/common'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const socialStore = useSocialStore()
const venueStore = useVenueStore()
const schoolStore = useSchoolStore()

const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => !!userInfo.value?.isAdmin)
const isStudent = computed(() => !!userInfo.value?.isStudent)
// 教职工：非学生，且有学校和编号
const isStaff = computed(
  () =>
    !userInfo.value?.isStudent &&
    !!userInfo.value?.school &&
    !!userInfo.value?.studentNumber &&
    !userInfo.value?.isAdmin,
)
const avatarInput = ref<HTMLInputElement | null>(null)
const avatarPreview = ref<string>('')
const saving = ref(false)

const identityText = computed(() => {
  if (userInfo.value?.isAdmin) return '管理员'
  if (userInfo.value?.isStudent) return '学生'
  if (isStaff.value) return '教职工'
  return '普通用户'
})

const identityClass = computed(() => {
  if (userInfo.value?.isAdmin) return 'tag-admin'
  if (userInfo.value?.isStudent) return 'tag-student'
  if (isStaff.value) return 'tag-staff'
  return 'tag-normal'
})

// 性别显示文本
const genderText = computed(() => {
  const gender = userInfo.value?.gender
  if (!gender) return '—'
  const genderMap: Record<string, string> = {
    MALE: '男',
    FEMALE: '女',
    OTHER: '其他',
  }
  return genderMap[gender] || gender
})

// 计算我的动态和总点赞数
const myPosts = computed(() => {
  const currentUserId = userInfo.value?.id || 1
  return socialStore.posts.filter((post) => post.userId === currentUserId)
})

const myPostsCount = computed(() => myPosts.value.length)

const totalLikes = computed(() => {
  return myPosts.value.reduce((sum, post) => sum + post.likes, 0)
})

const myBookingsAll = computed(() => {
  const currentUserId = userInfo.value?.id
  if (!currentUserId) return []
  // 过滤当前用户的预约，并按日期和时间排序（最新的在前）
  return venueStore.bookings
    .filter((booking) => booking.userId === currentUserId)
    .sort((a, b) => {
      // 先按日期排序
      const dateCompare = new Date(b.date).getTime() - new Date(a.date).getTime()
      if (dateCompare !== 0) return dateCompare
      // 如果日期相同，按开始时间排序
      return b.startTime.localeCompare(a.startTime)
    })
})

const myBookings = computed(() => myBookingsAll.value.slice(0, 3))

const quickStats = computed(() => [
  {
    label: '预约记录',
    value: myBookingsAll.value.length,
    desc: '累计预约次数',
  },
  {
    label: '待确认',
    value: myBookingsAll.value.filter((b) => b.status === 'pending').length,
    desc: '当前排队',
  },
  {
    label: '已完成',
    value: myBookingsAll.value.filter((b) => b.status === 'confirmed').length,
    desc: '成功预约',
  },
  {
    label: '社交动态',
    value: myPostsCount.value,
    desc: '发布内容',
  },
])

const statusMap: Record<string, string> = {
  pending: '待确认',
  confirmed: '已确认',
  cancelled: '已取消',
  user_cancelled: '用户已取消',
}

const achievementBadges = computed(() => [
  {
    icon: '🌟',
    name: '活力新星',
    desc: '完善个人资料',
    active: !!userInfo.value?.phone && !!userInfo.value?.school,
  },
  {
    icon: '🏅',
    name: '预约达人',
    desc: '完成 3 次预约',
    active: myBookingsAll.value.length >= 3,
  },
  {
    icon: '🔥',
    name: '社交高手',
    desc: '发布 3 条动态',
    active: myPostsCount.value >= 3,
  },
])

const activeBadgeCount = computed(() => {
  return achievementBadges.value.filter(badge => badge.active).length
})

const getStatIcon = (label: string) => {
  const iconMap: Record<string, string> = {
    '预约记录': '📅',
    '待确认': '⏳',
    '已完成': '✅',
    '社交动态': '💬',
  }
  return iconMap[label] || '📊'
}

const formData = ref({
  username: '',
  nickname: '',
  phone: '',
  school: '',
  bio: '',
})

const schoolOptions = computed(() => {
  if (!schoolStore.schools.length) {
    schoolStore.resetToDefault()
  }
  return schoolStore.schools
})

const errors = ref({
  username: '',
})

// 初始化表单数据
watch(
  userInfo,
  (newUserInfo) => {
    if (newUserInfo) {
      formData.value = {
        username: newUserInfo.username || '',
        nickname: newUserInfo.nickname || '',
        phone: newUserInfo.phone || '',
        school: newUserInfo.school || '',
        bio: (newUserInfo as any).bio || '',
      }
    }
  },
  { immediate: true },
)

const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 图片压缩函数
const compressImage = (file: File, maxSizeMB: number = 5, maxWidth: number = 1920): Promise<File> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = (event) => {
      const img = new Image()
      img.src = event.target?.result as string
      img.onload = () => {
        // 创建canvas
        const canvas = document.createElement('canvas')
        let width = img.width
        let height = img.height

        // 按比例缩小图片，最大宽度为maxWidth
        if (width > maxWidth) {
          height = (height * maxWidth) / width
          width = maxWidth
        }

        canvas.width = width
        canvas.height = height

        // 绘制图片
        const ctx = canvas.getContext('2d')
        ctx?.drawImage(img, 0, 0, width, height)

        // 压缩质量，从0.9开始尝试
        let quality = 0.9
        let compressedDataUrl: string
        let compressedBlob: Blob

        // 循环压缩，直到文件大小小于maxSizeMB或质量达到0.1
        const compress = () => {
          compressedDataUrl = canvas.toDataURL(file.type, quality)
          compressedBlob = dataURLToBlob(compressedDataUrl)

          if (compressedBlob.size <= maxSizeMB * 1024 * 1024 || quality <= 0.1) {
            // 创建新的File对象
            const compressedFile = new File([compressedBlob], file.name, {
              type: file.type,
              lastModified: file.lastModified
            })
            resolve(compressedFile)
          } else {
            quality -= 0.1
            compress()
          }
        }

        compress()
      }
      img.onerror = () => reject(new Error('图片加载失败'))
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
  })
}

// 将dataURL转换为Blob
const dataURLToBlob = (dataURL: string): Blob => {
  const parts = dataURL.split(';base64,')
  const contentType = parts[0].split(':')[1]
  const raw = window.atob(parts[1])
  const rawLength = raw.length
  const uInt8Array = new Uint8Array(rawLength)

  for (let i = 0; i < rawLength; ++i) {
    uInt8Array[i] = raw.charCodeAt(i)
  }

  return new Blob([uInt8Array], { type: contentType })
}

const handleAvatarChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 上传文件
  try {
    let uploadFile = file

    // 如果文件大于5MB，进行压缩
    if (file.size > 5 * 1024 * 1024) {
      uploadFile = await compressImage(file)
    }

    const formData = new FormData()
    formData.append('file', uploadFile)

    const response = await request.post<{ path: string; url: string }>('/upload/profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    // 保存路径用于提交
    avatarPreview.value = response.path
  } catch (error) {
    alert('头像上传失败，请稍后重试')
  }
}

// 获取头像URL（用于显示）
const getAvatarUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  // 对路径进行URL编码，处理空格等特殊字符
  const encodedPath = path.split('/').map(segment => encodeURIComponent(segment)).join('/')
  return `${baseURL.replace('/api', '')}/api/files/${encodedPath}`
}

const handleSave = async () => {
  if (!userInfo.value) return

  // 重置错误信息
  errors.value.username = ''

  // 验证姓名
  if (!formData.value.username.trim()) {
    errors.value.username = '请输入姓名'
    return
  }

  if (formData.value.username.length < 2) {
    errors.value.username = '姓名长度不能少于2位'
    return
  }

  if (formData.value.username.length > 20) {
    errors.value.username = '姓名长度不能超过20位'
    return
  }

  if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(formData.value.username)) {
    errors.value.username = '姓名只能包含字母、数字、下划线和中文'
    return
  }

  // 验证手机号格式（如果填写了）
  if (formData.value.phone && !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
    alert('请输入有效的手机号')
    return
  }

  const updateData: any = {
    username: formData.value.username,
    nickname: formData.value.nickname,
    phone: formData.value.phone,
    // 学生身份不可修改学校，强制使用原值
    school: userInfo.value?.isStudent ? userInfo.value.school : formData.value.school,
    bio: formData.value.bio,
  }

  // 如果有新头像，用新路径；否则保持现有头像（避免被清空）
  updateData.avatar = avatarPreview.value || userInfo.value?.avatar || undefined

  saving.value = true
  try {
    await userStore.updateUserInfo(updateData)
    // 清空预览，使用保存后的头像（从userInfo中读取）
    avatarPreview.value = ''
    alert('保存成功！')
  } catch (error) {
    alert('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

// 格式化日期显示
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  return `${month}月${day}日 周${weekday}`
}

// 格式化时间显示（去掉秒数）
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  // 如果是 HH:mm:ss 格式，只取前5位
  if (timeStr.length >= 5) {
    return timeStr.substring(0, 5)
  }
  return timeStr
}

// 监听用户信息变化，重新加载预约数据
watch(
  () => userInfo.value?.id,
  (newUserId) => {
    if (newUserId) {
      venueStore.fetchBookings(newUserId)
    }
  },
  { immediate: true }
)

onMounted(async () => {
  // 加载动态数据以计算统计信息
  await socialStore.fetchPosts()
  const userId = userStore.userInfo?.id
  if (userId) {
    await venueStore.fetchBookings(userId)
  }
})
</script>

<style scoped>
.page-header {
  margin-bottom: var(--spacing-xl);
  animation: fadeInDown 0.6s ease-out;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin-left: 40px;
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  animation: fadeIn 0.5s ease-out;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: var(--spacing-sm);
  opacity: 0.6;
}

.empty-text {
  font-size: 16px;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  animation: fadeIn 0.6s ease-out;
}

.profile-card {
  background-color: var(--bg-color);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.profile-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color) 0%, #764ba2 100%);
}

.profile-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.main-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
}

.profile-header {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--border-lighter);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: visible;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-border {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  z-index: 0;
  animation: rotate 3s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.avatar-wrapper:hover {
  transform: scale(1.08);
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-wrapper:hover .avatar-border {
  animation-duration: 1s;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: relative;
  z-index: 1;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  font-weight: 600;
  position: relative;
  z-index: 1;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
  z-index: 2;
  backdrop-filter: blur(4px);
}

.avatar-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
  overflow: hidden;
}

.avatar-upload-btn {
  width: 44px;
  height: 44px;
  border: none;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.upload-icon {
  font-size: 20px;
  display: block;
}

.avatar-upload-btn:hover {
  background-color: #fff;
  transform: scale(1.15) rotate(15deg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.avatar-hint {
  font-size: 12px;
  color: var(--text-secondary);
  text-align: center;
  margin: 0;
}

.user-info {
  flex: 1;
}

.user-name-section {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.username {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(135deg, var(--text-primary) 0%, var(--primary-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-id {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-color-page);
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.email {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-md);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.info-icon {
  font-size: 16px;
  opacity: 0.7;
}

.identity {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: var(--spacing-md);
  font-size: 14px;
  color: var(--text-secondary);
}

.identity-label {
  font-weight: 600;
  color: var(--text-primary);
}

.identity-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.5px;
}

.tag-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
  margin-right: 4px;
}

.tag-student {
  color: #2f9b57;
  background: #e9f7ef;
  border: 1px solid #c7ebd5;
}

.tag-staff {
  color: #fa8c16;
  background: #fff7e6;
  border: 1px solid #ffd591;
}

.tag-admin {
  color: #1f6feb;
  background: #e9f1ff;
  border: 1px solid #cddcff;
}

.tag-normal {
  color: #7a7a7a;
  background: #f5f5f5;
  border: 1px solid #e3e3e3;
}

.stats-section {
  display: flex;
  gap: var(--spacing-lg);
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-lighter);
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  background: var(--bg-color-page);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: var(--border-extra-light);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 2px;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.profile-details {
  margin-bottom: var(--spacing-xl);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-lighter);
}

.section-icon {
  font-size: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.form-grid .full-width {
  grid-column: 1 / -1;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.quick-stat {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  border: 1px solid var(--border-lighter);
  transition: all 0.3s ease;
  animation: slideInUp 0.6s ease-out backwards;
}

.quick-stat:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.quick-stat-icon {
  font-size: 32px;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.quick-stat-content {
  flex: 1;
}

.quick-stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 4px;
  line-height: 1.2;
}

.quick-stat-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.quick-stat-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.bio-textarea {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-color);
  font-family: inherit;
  resize: vertical;
  outline: none;
  transition: border-color 0.3s;
}

.bio-textarea:focus {
  border-color: var(--primary-color);
}

.profile-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-lighter);
}

.profile-actions .base-button {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.profile-actions .base-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.profile-widgets {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.widget-card {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.widget-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.widget-icon {
  font-size: 20px;
}

.widget-link {
  font-size: 14px;
  color: var(--primary-color);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.widget-link:hover {
  color: #66b1ff;
  gap: 8px;
}

.link-arrow {
  transition: transform 0.3s ease;
}

.widget-link:hover .link-arrow {
  transform: translateX(4px);
}

.widget-tip {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-color-page);
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.widget-loading {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  padding: var(--spacing-xl) var(--spacing-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-lighter);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.widget-empty {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  padding: var(--spacing-xl) var(--spacing-md);
}

.empty-icon-small {
  font-size: 48px;
  margin-bottom: var(--spacing-sm);
  opacity: 0.5;
}

.empty-hint {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: var(--spacing-xs);
}

.booking-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.booking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  background: var(--bg-color-page);
  border: 1px solid var(--border-lighter);
  transition: all 0.3s ease;
}

.booking-item:hover {
  background: var(--border-extra-light);
  border-color: var(--primary-color);
  transform: translateX(4px);
}

.booking-info {
  flex: 1;
}

.booking-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
  font-size: 15px;
}

.booking-time {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: var(--spacing-xs);
}

.time-icon {
  font-size: 14px;
  opacity: 0.7;
}

.booking-price {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: var(--spacing-xs);
}

.price-icon {
  font-size: 14px;
  opacity: 0.7;
}

.paid-badge {
  margin-left: var(--spacing-xs);
  padding: 2px 6px;
  background: var(--success-color);
  color: #fff;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
}

.booking-status {
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 999px;
  background-color: var(--border-extra-light);
  color: var(--text-secondary);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  display: inline-block;
}

.booking-status.pending {
  background-color: #fff7e6;
  color: #d48806;
  border: 1px solid #ffe7ba;
}

.booking-status.confirmed {
  background-color: #e6fffb;
  color: #08979c;
  border: 1px solid #87e8de;
}

.booking-status.cancelled {
  background-color: #fff1f0;
  color: #cf1322;
  border: 1px solid #ffccc7;
}

.booking-status.user_cancelled {
  background-color: #f9f0ff;
  color: #722ed1;
  border: 1px solid #d3adf7;
}

.badge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: var(--spacing-md);
}

.badge-card {
  border: 2px dashed var(--border-base);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  text-align: center;
  opacity: 0.6;
  transition: all 0.3s ease;
  background: var(--bg-color-page);
  position: relative;
  overflow: hidden;
}

.badge-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--border-base);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.badge-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-sm);
}

.badge-card.active {
  border-style: solid;
  border-color: var(--primary-color);
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  opacity: 1;
  border-width: 2px;
}

.badge-card.active::before {
  background: linear-gradient(90deg, var(--primary-color) 0%, #764ba2 100%);
  transform: scaleX(1);
}

.badge-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: var(--spacing-sm);
}

.badge-icon {
  font-size: 32px;
  display: block;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  transition: transform 0.3s ease;
}

.badge-card.active .badge-icon {
  transform: scale(1.1);
  animation: bounce 2s ease-in-out infinite;
}

.badge-check {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 20px;
  height: 20px;
  background: var(--success-color);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.badge-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--text-primary);
  font-size: 14px;
}

.badge-desc {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
}

.shortcut-item {
  border: 1px solid var(--border-lighter);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  background: var(--bg-color-page);
  position: relative;
  overflow: hidden;
}

.shortcut-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.shortcut-item:hover {
  border-color: var(--primary-color);
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
  background: #fff;
}

.shortcut-item:hover::before {
  transform: scaleY(1);
}

.shortcut-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.shortcut-item:hover .shortcut-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.shortcut-content {
  flex: 1;
}

.shortcut-title {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  font-size: 15px;
}

.shortcut-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.shortcut-arrow {
  font-size: 18px;
  color: var(--primary-color);
  opacity: 0;
  transform: translateX(-8px);
  transition: all 0.3s ease;
}

.shortcut-item:hover .shortcut-arrow {
  opacity: 1;
  transform: translateX(0);
}

/* 动画 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .user-name-section {
    justify-content: center;
  }

  .stats-section {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .stat-item {
    justify-content: center;
  }

  .profile-actions {
    flex-direction: column;
  }

  .profile-actions .base-button {
    width: 100%;
  }

  .quick-stats,
  .badge-grid,
  .shortcut-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .widget-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
}
</style>



