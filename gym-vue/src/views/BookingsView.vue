<template>
  <div class="bookings-view page-container">
    <div class="container">
      <h1 class="page-title">我的预约</h1>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner">⏳</div>
        <p class="loading-text">加载中...</p>
      </div>
      <div class="bookings-toolbar" v-else>
        <div class="status-filters">
          <button
            v-for="item in statusFilters"
            :key="item.value"
            :class="['filter-chip', { 'filter-chip--active': activeStatus === item.value }]"
            @click="activeStatus = item.value"
          >
            <span class="chip-icon">{{ getFilterIcon(item.value) }}</span>
            {{ item.label }}
            <span class="filter-count" v-if="bookingStats[item.value as keyof typeof bookingStats] !== undefined">
              {{ bookingStats[item.value as keyof typeof bookingStats] }}
            </span>
          </button>
        </div>
        <div class="toolbar-stats">
          <div class="stat-card">
            <div class="stat-icon">📊</div>
            <div class="stat-content">
              <div class="stat-label">总预约</div>
              <div class="stat-value">{{ bookingStats.total }}</div>
            </div>
          </div>
          <div class="stat-card success">
            <div class="stat-icon">✅</div>
            <div class="stat-content">
              <div class="stat-label">已确认</div>
              <div class="stat-value success">{{ bookingStats.confirmed }}</div>
            </div>
          </div>
          <div class="stat-card warning">
            <div class="stat-icon">⏳</div>
            <div class="stat-content">
              <div class="stat-label">待确认</div>
              <div class="stat-value warning">{{ bookingStats.pending }}</div>
            </div>
          </div>
          <div class="stat-card danger">
            <div class="stat-icon">❌</div>
            <div class="stat-content">
              <div class="stat-label">已取消</div>
              <div class="stat-value danger">{{ bookingStats.cancelled }}</div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && bookings.length === 0" class="empty-state">
        <div class="empty-icon">📅</div>
        <h3 class="empty-title">暂无预约记录</h3>
        <p class="empty-description">快去预约您心仪的场馆吧！</p>
        <RouterLink to="/venues">
          <BaseButton type="primary">
            <span class="button-icon">🏃</span>
            去预约
          </BaseButton>
        </RouterLink>
      </div>
      <div v-else-if="filteredBookings.length > 0" class="bookings-list">
        <div
          v-for="(booking, index) in filteredBookings"
          :key="booking.id"
          class="booking-card"
          :class="`booking-card--${booking.status}`"
          :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <div class="booking-card__header">
            <div class="booking-header-left">
              <div class="venue-icon">🏟️</div>
              <div class="venue-info">
                <h3 class="booking-venue-name">{{ booking.venueName }}</h3>
                <div class="booking-id">预约编号: #{{ booking.id }}</div>
              </div>
            </div>
            <span :class="['booking-status', `booking-status--${booking.status}`]">
              <span class="status-icon">{{ getStatusIcon(booking.status) }}</span>
              {{ getStatusText(booking.status) }}
            </span>
          </div>

          <div class="booking-card__body">
            <div class="booking-info">
              <div class="info-item">
                <span class="info-icon">📅</span>
                <div class="info-content">
                  <span class="info-label">预约日期</span>
                  <span class="info-value">{{ formatBookingDate(booking.date) }}</span>
                </div>
              </div>
              <div class="info-item">
                <span class="info-icon">⏰</span>
                <div class="info-content">
                  <span class="info-label">时间段</span>
                  <span class="info-value time-range">{{ booking.startTime }} - {{ booking.endTime }}</span>
                </div>
              </div>
              <div class="info-item">
                <span class="info-icon">🕐</span>
                <div class="info-content">
                  <span class="info-label">创建时间</span>
                  <span class="info-value">{{ formatDate(booking.createdAt) }}</span>
                </div>
              </div>
              <div class="info-item" v-if="booking.price != null">
                <span class="info-icon">💰</span>
                <div class="info-content">
                  <span class="info-label">总金额</span>
                  <span class="info-value price">¥{{ formatBookingMoney(getBookingTotal(booking)) }}</span>
                </div>
              </div>
            </div>

            <div v-if="booking.status === 'user_cancelled'" class="payment-status">
              <span class="payment-badge payment-badge--refunded">
                <span class="payment-icon">↩️</span>
                已退款（模拟）
              </span>
            </div>
            <div class="payment-status" v-else-if="booking.status !== 'cancelled'">
              <span :class="['payment-badge', { 'payment-badge--paid': booking.paid }]">
                <span class="payment-icon">{{ booking.paid ? '✅' : '⏸️' }}</span>
                {{ booking.paid ? '已付款' : '未付款' }}
              </span>
            </div>
          </div>

          <div class="booking-card__footer">
            <div class="action-left">
              <BaseButton type="secondary" size="small" @click="openDetail(booking)">
                <span class="button-icon">👁️</span>
                查看详情
              </BaseButton>
              <BaseButton type="primary" size="small" @click="handleViewVenue(booking)">
                <span class="button-icon">🏟️</span>
                查看场馆
              </BaseButton>
              <BaseButton type="success" size="small" @click="handleRebook(booking)">
                <span class="button-icon">🔄</span>
                再次预约
              </BaseButton>
            </div>
            <div class="action-right" v-if="booking.status === 'pending' || booking.status === 'confirmed'">
              <BaseButton
                v-if="!booking.paid && booking.status !== 'cancelled'"
                type="warning"
                size="small"
                @click="handlePay(booking)"
              >
                <span class="button-icon">💳</span>
                付款
              </BaseButton>
              <BaseButton
                v-if="booking.status === 'pending' && booking.paid"
                type="success"
                size="small"
                @click="handleConfirm(booking.id)"
              >
                <span class="button-icon">✓</span>
                确认预约
              </BaseButton>
              <BaseButton type="danger" size="small" @click="handleCancel(booking)">
                <span class="button-icon">🗑️</span>
                取消预约
              </BaseButton>
            </div>
          </div>
        </div>
      </div>

      <BaseModal :visible="detailVisible" title="预约详情" @close="closeDetail">
        <div v-if="selectedBooking" class="detail-content">
          <div class="detail-header">
            <div class="detail-venue-icon">🏟️</div>
            <div class="detail-venue-info">
              <h4 class="detail-venue-name">{{ selectedBooking.venueName }}</h4>
              <p class="detail-venue-id">场馆ID: {{ selectedBooking.venueId }}</p>
            </div>
          </div>

          <div class="detail-grid">
            <div class="detail-item">
              <div class="detail-icon">🔢</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">预约编号</div>
                <div class="detail-value">#{{ selectedBooking.id }}</div>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-icon">📅</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">日期</div>
                <div class="detail-value">{{ formatBookingDate(selectedBooking.date) }}</div>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-icon">⏰</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">时间段</div>
                <div class="detail-value time-range">{{ selectedBooking.startTime }} - {{ selectedBooking.endTime }}</div>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-icon">📊</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">状态</div>
                <span :class="['detail-status', `detail-status--${selectedBooking.status}`]">
                  <span class="status-icon">{{ getStatusIcon(selectedBooking.status) }}</span>
                  {{ getStatusText(selectedBooking.status) }}
                </span>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-icon">💳</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">付款状态</div>
                <div class="detail-value">
                  <span
                    v-if="selectedBooking.status === 'user_cancelled'"
                    class="payment-indicator payment-indicator--refunded"
                  >
                    ↩️ 已退款（模拟）
                  </span>
                  <span
                    v-else
                    :class="['payment-indicator', { 'payment-indicator--paid': selectedBooking.paid }]"
                  >
                    {{ selectedBooking.paid ? '✅ 已付款' : '⏸️ 未付款' }}
                  </span>
                </div>
              </div>
            </div>
            <div class="detail-item" v-if="selectedBooking.price != null">
              <div class="detail-icon">💰</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">总金额</div>
                <div class="detail-value price">¥{{ formatBookingMoney(getBookingTotal(selectedBooking)) }}</div>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-icon">🕐</div>
              <div class="detail-content-wrapper">
                <div class="detail-label">创建时间</div>
                <div class="detail-value">{{ formatDate(selectedBooking.createdAt) }}</div>
              </div>
            </div>
          </div>
        </div>
        <template #footer>
          <BaseButton type="secondary" @click="handleViewVenue(selectedBooking!)">
            <span class="button-icon">🏟️</span>
            查看场馆
          </BaseButton>
          <BaseButton type="primary" @click="handleRebook(selectedBooking!)">
            <span class="button-icon">🔄</span>
            再次预约
          </BaseButton>
          <BaseButton v-if="selectedBooking?.status === 'pending' || selectedBooking?.status === 'confirmed'" type="danger" @click="handleCancel(selectedBooking!)">
            <span class="button-icon">🗑️</span>
            取消预约
          </BaseButton>
        </template>
      </BaseModal>

      <!-- 付款确认弹窗 -->
      <BaseModal :visible="payVisible" title="确认付款" @close="cancelPayment">
        <div v-if="currentPayBooking" class="pay-content">
          <div class="pay-header">
            <div class="pay-icon">💳</div>
            <h4 class="pay-title">付款信息</h4>
          </div>
          <div class="pay-info">
            <div class="pay-item">
              <span class="pay-item-label">单价</span>
              <span class="pay-item-value">¥{{ currentPayBooking.price }} / 小时</span>
            </div>
            <div class="pay-item">
              <span class="pay-item-label">时长</span>
              <span class="pay-item-value">{{ payHours }} 小时</span>
            </div>
            <div class="pay-divider"></div>
            <div class="pay-item total">
              <span class="pay-item-label">本次需支付合计</span>
              <span class="pay-item-value">¥{{ payTotal }}</span>
            </div>
          </div>
          <div class="qr-code-container">
            <div class="qr-header">
              <span class="qr-icon">📱</span>
              <h4>扫描二维码付款</h4>
            </div>
            <div v-if="qrCodeGenerating" class="qr-loading">
              <div class="qr-spinner">⏳</div>
              <p>生成二维码中...</p>
            </div>
            <div v-else-if="qrCodeData" class="qr-code">
              <img :src="qrCodeData" alt="付款二维码" />
              <p class="qr-hint">请使用手机扫描二维码完成支付</p>
            </div>
            <div v-else class="qr-error">
              <span class="error-icon">❌</span>
              <p>二维码生成失败，请重试</p>
            </div>
          </div>
        </div>
        <template #footer>
          <BaseButton type="secondary" @click="cancelPayment">
            <span class="button-icon">❌</span>
            取消
          </BaseButton>
          <BaseButton type="primary" @click="confirmPayment">
            <span class="button-icon">✅</span>
            确认付款
          </BaseButton>
        </template>
      </BaseModal>

      <!-- 确认预约弹窗 -->
      <BaseModal :visible="confirmVisible" title="确认预约" @close="cancelConfirm">
        <div class="confirm-content">
          <div class="confirm-icon">✅</div>
          <p class="confirm-message">您确定要确认这个预约吗？</p>
          <p class="confirm-note">确认后，预约状态将变为"已确认"，请确保已查看预约详情。</p>
        </div>
        <template #footer>
          <BaseButton type="secondary" @click="cancelConfirm">
            <span class="button-icon">❌</span>
            取消
          </BaseButton>
          <BaseButton type="primary" @click="confirmBooking">
            <span class="button-icon">✓</span>
            确认预约
          </BaseButton>
        </template>
      </BaseModal>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useVenueStore } from '@/stores/venue'
import { useUserStore } from '@/stores/user'
import { BaseButton, BaseModal } from '@/components/common'
import QRCode from 'qrcode'
import type { Booking } from '@/stores/venue'

const router = useRouter()
const venueStore = useVenueStore()
const userStore = useUserStore()

const loading = computed(() => venueStore.loading)
const bookings = computed(() => venueStore.bookings)
const activeStatus = ref<'all' | 'pending' | 'confirmed' | 'cancelled'>('pending')
const detailVisible = ref(false)
const selectedBooking = ref<Booking | null>(null)
// 付款弹窗相关变量
const payVisible = ref(false)
const currentPayBooking = ref<Booking | null>(null)
const payTotal = ref(0)
const payHours = ref(0)
const qrCodeData = ref('')
const qrCodeGenerating = ref(false)

// 确认预约弹窗相关变量
const confirmVisible = ref(false)
const currentConfirmBookingId = ref<number | null>(null)

const statusFilters = [
  { value: 'all' as const, label: '全部' },
  { value: 'pending' as const, label: '待确认' },
  { value: 'confirmed' as const, label: '已确认' },
  { value: 'cancelled' as const, label: '已取消' },
]

const filteredBookings = computed(() => {
  const priority: Record<string, number> = {
    confirmed: 1,
    pending: 2,
    cancelled: 3,
    user_cancelled: 3,
  }
  const list =
    activeStatus.value === 'all'
      ? bookings.value
      : activeStatus.value === 'cancelled'
        ? bookings.value.filter((b) => b.status === 'cancelled' || b.status === 'user_cancelled')
        : bookings.value.filter((b) => b.status === activeStatus.value)
  return [...list].sort((a, b) => {
    const pa = priority[a.status] ?? 99
    const pb = priority[b.status] ?? 99
    if (pa !== pb) return pa - pb
    // 次序相同按创建时间倒序
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  })
})

const bookingStats = computed(() => ({
  total: bookings.value.length,
  pending: bookings.value.filter((b) => b.status === 'pending').length,
  confirmed: bookings.value.filter((b) => b.status === 'confirmed').length,
  cancelled: bookings.value.filter((b) => b.status === 'cancelled' || b.status === 'user_cancelled')
    .length,
}))

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待确认',
    confirmed: '已确认',
    cancelled: '已取消',
    user_cancelled: '用户已取消',
  }
  return statusMap[status] || status
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const formatBookingDate = (dateString: string) => {
  const date = new Date(dateString)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = weekdays[date.getDay()]
  return `${month}月${day}日 ${weekday}`
}

const getFilterIcon = (status: string) => {
  const icons: Record<string, string> = {
    all: '📋',
    pending: '⏳',
    confirmed: '✅',
    cancelled: '❌',
    user_cancelled: '↩️',
  }
  return icons[status] || '📋'
}

const getStatusIcon = (status: string) => {
  const icons: Record<string, string> = {
    pending: '⏳',
    confirmed: '✅',
    cancelled: '❌',
    user_cancelled: '↩️',
  }
  return icons[status] || '📋'
}

const calculateHours = (startTime: string, endTime: string): number => {
  const [sh, sm] = startTime.split(':').map(Number)
  const [eh, em] = endTime.split(':').map(Number)
  const start = sh * 60 + (sm || 0)
  const end = eh * 60 + (em || 0)
  return (end - start) / 60
}

/** 与付款弹窗一致：单价(元/小时) × 预约时长 */
const getBookingTotal = (booking: Booking): number => {
  const p = booking.price
  if (p == null || Number.isNaN(Number(p))) return 0
  const hours = Math.max(0, calculateHours(booking.startTime, booking.endTime))
  return Math.round(hours * Number(p) * 100) / 100
}

const formatBookingMoney = (n: number) => {
  if (!Number.isFinite(n)) return '0'
  return Number.isInteger(n) ? String(n) : n.toFixed(2)
}

const handleCancel = async (booking: Booking) => {
  const total = getBookingTotal(booking)
  const isPaidConfirmed = booking.status === 'confirmed' && booking.paid
  const confirmMsg = isPaidConfirmed
    ? `确定要取消该预约吗？已支付的 ¥${formatBookingMoney(total)} 将原路退回（模拟）。`
    : '确定要取消这个预约吗？'
  if (!confirm(confirmMsg)) return
  try {
    await venueStore.cancelBooking(booking.id)
    alert(
      isPaidConfirmed
        ? `预约已取消，¥${formatBookingMoney(total)} 已退回（模拟）。`
        : '预约已取消',
    )
  } catch (e) {
    alert(e instanceof Error ? e.message : '取消失败，请稍后重试')
  }
}

const handleConfirm = (bookingId: number) => {
  currentConfirmBookingId.value = bookingId
  confirmVisible.value = true
}

// 确认预约操作
const confirmBooking = async () => {
  if (!currentConfirmBookingId.value) return

  try {
    await venueStore.confirmBooking(currentConfirmBookingId.value)
    confirmVisible.value = false
    currentConfirmBookingId.value = null
    alert('已确认')
  } catch (e) {
    alert('确认失败，请稍后重试')
  }
}

// 取消确认预约
const cancelConfirm = () => {
  confirmVisible.value = false
  currentConfirmBookingId.value = null
}

// 生成二维码
const generateQRCode = async (data: string) => {
  qrCodeGenerating.value = true
  try {
    // 生成随机的付款二维码内容，实际项目中应该是从后端获取的付款链接
    const randomData = `${data}-${Math.random().toString(36).substring(2, 15)}`
    qrCodeData.value = await QRCode.toDataURL(randomData, {
      width: 200,
      margin: 1
    })
  } catch (error) {
    console.error('生成二维码失败:', error)
    qrCodeData.value = ''
  } finally {
    qrCodeGenerating.value = false
  }
}

const handlePay = async (booking: Booking) => {
  if (!booking.price) {
    alert('该场馆暂未设置价格，无法付款')
    return
  }
  const hours = calculateHours(booking.startTime, booking.endTime)
  const total = Math.round((hours * (booking.price || 0)) * 100) / 100

  // 设置付款信息
  currentPayBooking.value = booking
  payHours.value = hours
  payTotal.value = total

  // 显示付款弹窗
  payVisible.value = true

  // 生成二维码
  await generateQRCode(`payment-${booking.id}-${total}`)
}

// 确认付款
const confirmPayment = async () => {
  if (!currentPayBooking.value) return

  try {
    const updated = await venueStore.payBooking(currentPayBooking.value.id)
    selectedBooking.value = updated
    payVisible.value = false
    alert('付款成功（模拟）')
  } catch (e) {
    alert('付款失败，请稍后重试')
  }
}

// 取消付款
const cancelPayment = () => {
  payVisible.value = false
  currentPayBooking.value = null
  qrCodeData.value = ''
}

const openDetail = (booking: typeof bookings.value[number]) => {
  selectedBooking.value = booking
  detailVisible.value = true
}

const closeDetail = () => {
  detailVisible.value = false
  selectedBooking.value = null
}

const handleViewVenue = (booking: typeof bookings.value[number]) => {
  router.push(`/venues/${booking.venueId}`)
}

const handleRebook = (booking: typeof bookings.value[number]) => {
  router.push({ path: `/venues/${booking.venueId}`, query: { date: booking.date } })
}

onMounted(() => {
  const userId = userStore.userInfo?.id
  if (!userId) {
    router.push('/login')
    return
  }
  venueStore.fetchBookings(userId)
})
</script>

<style scoped>
.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.page-title::before {
  content: '📋';
  font-size: 36px;
}

.bookings-toolbar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.status-filters {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.filter-chip {
  border: 2px solid var(--border-base);
  background: var(--bg-color);
  color: var(--text-primary);
  padding: 10px 18px;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
}

.filter-chip:hover {
  border-color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.1);
}

.filter-chip--active {
  border-color: var(--primary-color);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15) 0%, rgba(64, 158, 255, 0.05) 100%);
  color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  font-weight: 600;
}

.chip-icon {
  font-size: 16px;
}

.filter-count {
  background: var(--bg-color-page);
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.toolbar-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: var(--spacing-sm);
}

.stat-card {
  background: var(--bg-color);
  border: 2px solid var(--border-lighter);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--border-base);
  transition: all 0.3s ease;
}

.stat-card.success::before {
  background: var(--primary-color);
}

.stat-card.warning::before {
  background: var(--warning-color);
}

.stat-card.danger::before {
  background: var(--danger-color);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.stat-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 6px;
  font-weight: 500;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-value.success {
  color: var(--primary-color);
}

.stat-value.warning {
  color: var(--warning-color);
}

.stat-value.danger {
  color: var(--danger-color);
}

.loading-container {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.loading-spinner {
  font-size: 48px;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 16px;
  color: var(--text-secondary);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xl) var(--spacing-lg);
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  background: linear-gradient(135deg, var(--bg-color) 0%, #f8f9fa 100%);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-base);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: var(--spacing-sm);
  animation: bounce 2s infinite;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.empty-description {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.booking-card {
  background: linear-gradient(135deg, var(--bg-color) 0%, #fafbfc 100%);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 2px solid var(--border-lighter);
  transition: all 0.3s ease;
  animation: slideInLeft 0.5s ease-out both;
  position: relative;
  overflow: hidden;
}

.booking-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--border-base);
  transition: all 0.3s ease;
}

.booking-card--pending::before {
  background: var(--warning-color);
}

.booking-card--confirmed::before {
  background: var(--primary-color);
}

.booking-card--cancelled::before {
  background: var(--danger-color);
}

.booking-card--user_cancelled::before {
  background: #722ed1;
}

.booking-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
  border-color: var(--primary-color);
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.booking-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-lighter);
}

.booking-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.venue-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.venue-info {
  flex: 1;
}

.booking-venue-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.booking-id {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.booking-status {
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.status-icon {
  font-size: 14px;
}

.booking-status--pending {
  background-color: #fdf6ec;
  color: var(--warning-color);
}

.booking-status--confirmed {
  background-color: #f0f9ff;
  color: var(--primary-color);
}

.booking-status--cancelled {
  background-color: #fef0f0;
  color: var(--danger-color);
}

.booking-status--user_cancelled {
  background-color: #f9f0ff;
  color: #722ed1;
}

.booking-card__body {
  margin-bottom: var(--spacing-lg);
}

.booking-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  background: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
}

.info-item:hover {
  background: var(--bg-color);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.info-icon {
  font-size: 18px;
  flex-shrink: 0;
  margin-top: 2px;
}

.info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  color: var(--text-primary);
  font-weight: 600;
}

.info-value.time-range {
  color: var(--primary-color);
  font-size: 16px;
}

.info-value.price {
  color: var(--warning-color);
  font-size: 16px;
}

.payment-status {
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-lighter);
}

.payment-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  background: #fef0f0;
  color: var(--danger-color);
  border: 1px solid #fde2e2;
}

.payment-badge--paid {
  background: #f0f9ff;
  color: var(--primary-color);
  border-color: #b3d8ff;
}

.payment-badge--refunded {
  background: #f9f0ff;
  color: #722ed1;
  border-color: #d3adf7;
}

.payment-icon {
  font-size: 14px;
}

.booking-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 2px solid var(--border-lighter);
}

.booking-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  width: 100%;
}

.action-left {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.action-right {
  display: flex;
  gap: var(--spacing-sm);
}

.detail-content {
  padding: var(--spacing-md);
}

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: var(--border-radius-md);
  margin-bottom: var(--spacing-lg);
  border: 2px solid var(--primary-color);
}

.detail-venue-icon {
  font-size: 48px;
  flex-shrink: 0;
}

.detail-venue-info {
  flex: 1;
}

.detail-venue-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.detail-venue-id {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
}

.detail-item {
  background: var(--bg-color-page);
  border: 2px solid var(--border-lighter);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  transition: all 0.2s ease;
}

.detail-item:hover {
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.detail-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.detail-content-wrapper {
  flex: 1;
}

.detail-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 6px;
  font-weight: 500;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  word-break: break-all;
}

.detail-value.time-range {
  color: var(--primary-color);
  font-size: 18px;
}

.detail-value.price {
  color: var(--warning-color);
  font-size: 18px;
}

.payment-indicator {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  background: #fef0f0;
  color: var(--danger-color);
}

.payment-indicator--paid {
  background: #f0f9ff;
  color: var(--primary-color);
}

.payment-indicator--refunded {
  background: #f9f0ff;
  color: #722ed1;
}

.detail-status {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.detail-status--pending {
  background: #fdf6ec;
  color: var(--warning-color);
}

.detail-status--confirmed {
  background: #f0f9ff;
  color: var(--primary-color);
}

.detail-status--cancelled {
  background: #fef0f0;
  color: var(--danger-color);
}

.detail-status--user_cancelled {
  background: #f9f0ff;
  color: #722ed1;
}

@media (max-width: 768px) {
  .booking-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }

  .booking-header-left {
    width: 100%;
  }

  .booking-info {
    grid-template-columns: 1fr;
  }

  .bookings-toolbar {
    gap: var(--spacing-sm);
  }

  .toolbar-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .booking-actions {
    flex-direction: column;
  }

  .action-left,
  .action-right {
    width: 100%;
    flex-direction: column;
  }

  .action-left .base-button,
  .action-right .base-button {
    width: 100%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-header {
    flex-direction: column;
    text-align: center;
  }
}

/* 付款弹窗样式 */
.pay-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.pay-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-lighter);
}

.pay-icon {
  font-size: 32px;
}

.pay-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.pay-info {
  width: 100%;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #f5f7fa 100%);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-md);
  border: 2px solid var(--border-lighter);
}

.pay-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
  font-size: 16px;
  color: var(--text-primary);
}

.pay-item:last-child {
  margin-bottom: 0;
}

.pay-item-label {
  font-weight: 500;
  color: var(--text-secondary);
}

.pay-item-value {
  font-weight: 600;
  color: var(--text-primary);
}

.pay-divider {
  height: 1px;
  background: var(--border-base);
  margin: var(--spacing-md) 0;
}

.pay-item.total {
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 2px solid var(--primary-color);
}

.pay-item.total .pay-item-label {
  font-size: 18px;
  color: var(--text-primary);
}

.pay-item.total .pay-item-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

.qr-code-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border: 2px solid var(--border-lighter);
}

.qr-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.qr-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.qr-icon {
  font-size: 20px;
}

.qr-code {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: white;
  border-radius: var(--border-radius-md);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 2px solid var(--border-lighter);
}

.qr-code img {
  width: 200px;
  height: 200px;
  object-fit: contain;
}

.qr-hint {
  margin-top: var(--spacing-md);
  font-size: 12px;
  color: var(--text-secondary);
  text-align: center;
}

.qr-loading {
  padding: var(--spacing-lg);
  font-size: 16px;
  color: var(--text-secondary);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.qr-spinner {
  font-size: 32px;
  animation: spin 1s linear infinite;
}

.qr-error {
  padding: var(--spacing-lg);
  font-size: 16px;
  color: var(--danger-color);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.error-icon {
  font-size: 32px;
}

/* 确认预约弹窗样式 */
.confirm-content {
  text-align: center;
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.confirm-icon {
  font-size: 64px;
  animation: bounce 1s ease-in-out;
}

.confirm-message {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.confirm-note {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.6;
}

.button-icon {
  margin-right: 6px;
  font-size: 14px;
}
</style>
