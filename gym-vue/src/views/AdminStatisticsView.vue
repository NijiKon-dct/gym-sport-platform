<template>
  <div class="admin-statistics page-container">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">场馆预约使用统计</h1>
        <div class="header-controls">
          <select v-model="selectedWeek" class="week-select" @change="loadStatistics">
            <option v-for="week in weekOptions" :key="week.value" :value="week.value">
              {{ week.label }}
            </option>
          </select>
          <BaseButton type="primary" @click="loadStatistics">刷新数据</BaseButton>
        </div>
      </div>

      <!-- 统计概览卡片 -->
      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <div class="stat-number">{{ totalBookings }}</div>
            <div class="stat-label">总预约数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <div class="stat-number">¥{{ totalRevenue }}</div>
            <div class="stat-label">总收入</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🏟️</div>
          <div class="stat-content">
            <div class="stat-number">{{ activeVenues }}</div>
            <div class="stat-label">活跃场馆</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <div class="stat-number">{{ activeUsers }}</div>
            <div class="stat-label">活跃用户</div>
          </div>
        </div>
      </div>

      <!-- 场馆使用频率 -->
      <div class="statistics-section">
        <h2 class="section-title">场馆使用频率</h2>
        <div class="table-container">
          <table class="statistics-table">
            <thead>
              <tr>
                <th>排名</th>
                <th>场馆名称</th>
                <th>预约次数</th>
                <th>使用时长（小时）</th>
                <th>总收入（¥）</th>
                <th>使用率</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(venue, index) in venueStatistics" :key="venue.venueId">
                <td>{{ index + 1 }}</td>
                <td>{{ venue.venueName }}</td>
                <td>{{ venue.bookingCount }}</td>
                <td>{{ venue.totalHours }}</td>
                <td class="revenue-cell">¥{{ venue.revenue }}</td>
                <td>
                  <div class="usage-bar">
                    <div
                      class="usage-fill"
                      :style="{ width: `${venue.usageRate}%` }"
                    ></div>
                    <span class="usage-text">{{ venue.usageRate }}%</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 用户预约频率 -->
      <div class="statistics-section">
        <h2 class="section-title">用户预约频率</h2>
        <div class="table-container">
          <table class="statistics-table">
            <thead>
              <tr>
                <th>排名</th>
                <th>用户ID</th>
                <th>用户名</th>
                <th>预约次数</th>
                <th>预约场馆数</th>
                <th>消费金额（¥）</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(user, index) in userStatistics" :key="user.userId">
                <td>{{ index + 1 }}</td>
                <td>{{ user.userId }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.bookingCount }}</td>
                <td>{{ user.venueCount }}</td>
                <td class="revenue-cell">¥{{ user.totalSpent }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 每日收入趋势 -->
      <div class="statistics-section">
        <h2 class="section-title">每日收入趋势</h2>
        <div class="chart-container">
          <div class="daily-revenue-list">
            <div
              v-for="day in dailyRevenue"
              :key="day.date"
              class="daily-revenue-item"
            >
              <div class="day-info">
                <div class="day-name">{{ day.dayName }}</div>
                <div class="day-date">{{ day.date }}</div>
              </div>
              <div class="day-stats">
                <div class="day-revenue">¥{{ day.revenue }}</div>
                <div class="day-bookings">{{ day.bookings }}次预约</div>
              </div>
              <div class="day-bar">
                <div
                  class="day-bar-fill"
                  :style="{ width: `${(day.revenue / maxDailyRevenue) * 100}%` }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 付款明细 -->
      <div class="statistics-section">
        <h2 class="section-title">付款明细（本周）</h2>
        <div class="table-container">
          <table class="statistics-table">
            <thead>
              <tr>
                <th>订单ID</th>
                <th>用户ID</th>
                <th>场馆</th>
                <th>日期</th>
                <th>时间段</th>
                <th>时长（小时）</th>
                <th>单价（¥/小时）</th>
                <th>金额（¥）</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in paidRecords" :key="record.id">
                <td>#{{ record.id }}</td>
                <td>{{ record.userId }}</td>
                <td>{{ record.venueName }}</td>
                <td>{{ record.date }}</td>
                <td>{{ record.startTime }} - {{ record.endTime }}</td>
                <td>{{ record.hours }}</td>
                <td>¥{{ record.price }}</td>
                <td class="revenue-cell">¥{{ record.amount }}</td>
                <td>{{ record.statusLabel }}</td>
              </tr>
              <tr v-if="paidRecords.length === 0">
                <td colspan="9" style="text-align: center; color: var(--text-secondary);">
                  本周暂无付款相关记录
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useVenueStore, type Booking } from '@/stores/venue'
import { useUserStore } from '@/stores/user'
import { BaseButton } from '@/components/common'

const venueStore = useVenueStore()
const userStore = useUserStore()

// 选中的周
const selectedWeek = ref('current')
const allBookings = ref<Booking[]>([])
const loading = ref(false)

// 周选项
const weekOptions = [
  { value: 'current', label: '本周' },
  { value: 'last', label: '上周' },
]

// 计算时间范围
const getWeekRange = () => {
  const now = new Date()
  const dayOfWeek = now.getDay()
  const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek // 周一

  let startDate: Date
  if (selectedWeek.value === 'current') {
    startDate = new Date(now)
    startDate.setDate(now.getDate() + diff)
  } else {
    startDate = new Date(now)
    startDate.setDate(now.getDate() + diff - 7)
  }

  startDate.setHours(0, 0, 0, 0)
  const endDate = new Date(startDate)
  endDate.setDate(startDate.getDate() + 6)
  endDate.setHours(23, 59, 59, 999)

  return { startDate, endDate }
}

/** 本周日期范围内的所有预约（含已取消、用户已退款取消），供明细展示 */
const bookingsInWeekRange = computed(() => {
  const { startDate, endDate } = getWeekRange()
  return allBookings.value.filter((booking) => {
    const bookingDate = new Date(booking.date)
    return bookingDate >= startDate && bookingDate <= endDate
  })
})

/** 计入总预约数、收入、场馆/用户统计：仅待确认、已确认 */
const statsBookings = computed(() =>
  bookingsInWeekRange.value.filter((b) => b.status === 'pending' || b.status === 'confirmed'),
)

// 计算时长（小时）
const calculateHours = (startTime: string, endTime: string): number => {
  const [startHour, startMin] = startTime.split(':').map(Number)
  const [endHour, endMin] = endTime.split(':').map(Number)
  const start = startHour * 60 + startMin
  const end = endHour * 60 + endMin
  return (end - start) / 60
}

// 总预约数（不含用户已退款取消、普通已取消）
const totalBookings = computed(() => statsBookings.value.length)

// 总收入（仅统计已付款）
const totalRevenue = computed(() => {
  return statsBookings.value.reduce((sum, booking) => {
    if (!booking.paid) return sum
    const hours = calculateHours(booking.startTime, booking.endTime)
    const price = booking.price || 0
    return sum + hours * price
  }, 0)
})

// 活跃场馆数
const activeVenues = computed(() => {
  const venueIds = new Set(statsBookings.value.map((b) => b.venueId))
  return venueIds.size
})

// 活跃用户数
const activeUsers = computed(() => {
  const userIds = new Set(statsBookings.value.map((b) => b.userId))
  return userIds.size
})

// 场馆统计
const venueStatistics = computed(() => {
  const venueMap = new Map<
    number,
    {
      venueId: number
      venueName: string
      bookingCount: number
      totalHours: number
      revenue: number
    }
  >()

  statsBookings.value.forEach((booking) => {
    if (!booking.paid) return
    const hours = calculateHours(booking.startTime, booking.endTime)
    const price = booking.price || 0
    const revenue = hours * price

    if (!venueMap.has(booking.venueId)) {
      venueMap.set(booking.venueId, {
        venueId: booking.venueId,
        venueName: booking.venueName,
        bookingCount: 0,
        totalHours: 0,
        revenue: 0,
      })
    }

    const venue = venueMap.get(booking.venueId)!
    venue.bookingCount++
    venue.totalHours += hours
    venue.revenue += revenue
  })

  const statistics = Array.from(venueMap.values())

  // 计算使用率（基于最大预约次数）
  const maxBookings = Math.max(...statistics.map((v) => v.bookingCount), 1)

  return statistics
    .map((venue) => ({
      ...venue,
      revenue: Math.round(venue.revenue),
      totalHours: Math.round(venue.totalHours * 10) / 10,
      usageRate: Math.round((venue.bookingCount / maxBookings) * 100),
    }))
    .sort((a, b) => b.bookingCount - a.bookingCount)
})

// 用户统计
const userStatistics = computed(() => {
  const userMap = new Map<
    number,
    {
      userId: number
      username: string
      bookingCount: number
      venueIds: Set<number>
      totalSpent: number
    }
  >()

  statsBookings.value.forEach((booking) => {
    if (!booking.paid) return
    const hours = calculateHours(booking.startTime, booking.endTime)
    const price = booking.price || 0
    const spent = hours * price

    if (!userMap.has(booking.userId)) {
      const user = userStore.users.find((u) => u.id === booking.userId)
      userMap.set(booking.userId, {
        userId: booking.userId,
        username: user?.username || `用户${booking.userId}`,
        bookingCount: 0,
        venueIds: new Set(),
        totalSpent: 0,
      })
    }

    const user = userMap.get(booking.userId)!
    user.bookingCount++
    user.venueIds.add(booking.venueId)
    user.totalSpent += spent
  })

  return Array.from(userMap.values())
    .map((user) => ({
      ...user,
      venueCount: user.venueIds.size,
      totalSpent: Math.round(user.totalSpent),
    }))
    .sort((a, b) => b.bookingCount - a.bookingCount)
})

// 每日收入
const dailyRevenue = computed(() => {
  const { startDate } = getWeekRange()
  const dailyMap = new Map<string, { revenue: number; bookings: number }>()

  statsBookings.value.forEach((booking) => {
    if (!booking.paid) return
    const date = booking.date
    if (!dailyMap.has(date)) {
      dailyMap.set(date, { revenue: 0, bookings: 0 })
    }

    const day = dailyMap.get(date)!
    const hours = calculateHours(booking.startTime, booking.endTime)
    const price = booking.price || 0
    day.revenue += hours * price
    day.bookings++
  })

  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const result = []

  for (let i = 0; i < 7; i++) {
    const date = new Date(startDate)
    date.setDate(startDate.getDate() + i)
    const dateStr = date.toISOString().split('T')[0]
    const dayData = dailyMap.get(dateStr) || { revenue: 0, bookings: 0 }
    const dayOfWeek = date.getDay()

    result.push({
      date: dateStr,
      dayName: days[dayOfWeek],
      revenue: Math.round(dayData.revenue),
      bookings: dayData.bookings,
    })
  }

  return result
})

// 最大日收入（用于图表比例）
const maxDailyRevenue = computed(() => {
  return Math.max(...dailyRevenue.value.map((d) => d.revenue), 1)
})

// 付款明细：有效已付款 + 用户已取消（已退款）仍展示，后者不计入上方汇总
const paidRecords = computed(() => {
  const rows: {
    id: number
    userId: number
    venueName: string
    date: string
    startTime: string
    endTime: string
    hours: number
    price: number
    amount: number
    statusLabel: string
  }[] = []

  for (const b of bookingsInWeekRange.value) {
    const hours = calculateHours(b.startTime, b.endTime)
    const price = b.price || 0
    const amount = Math.round(hours * price)
    if (b.status === 'user_cancelled') {
      rows.push({
        id: b.id,
        userId: b.userId,
        venueName: b.venueName,
        date: b.date,
        startTime: b.startTime,
        endTime: b.endTime,
        hours: Math.round(hours * 10) / 10,
        price,
        amount,
        statusLabel: '用户已取消（已退款）',
      })
      continue
    }
    if (b.paid && (b.status === 'pending' || b.status === 'confirmed')) {
      rows.push({
        id: b.id,
        userId: b.userId,
        venueName: b.venueName,
        date: b.date,
        startTime: b.startTime,
        endTime: b.endTime,
        hours: Math.round(hours * 10) / 10,
        price,
        amount,
        statusLabel: '正常',
      })
    }
  }

  return rows.sort((a, b) => (a.date < b.date ? 1 : -1))
})

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    const { startDate, endDate } = getWeekRange()
    const start = startDate.toISOString().split('T')[0]
    const end = endDate.toISOString().split('T')[0]
    allBookings.value = await venueStore.getAllBookings({ start, end })
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
  // 确保用户数据已加载
  if (userStore.users.length === 0) {
    userStore.fetchUsers()
  }
})
</script>

<style scoped>
.admin-statistics {
  padding: var(--spacing-xl) 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  padding-bottom: var(--spacing-sm);
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  border-radius: 2px;
}

.header-controls {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.week-select {
  padding: 10px 18px;
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.week-select:hover {
  border-color: var(--primary-color);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.15);
}

.week-select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 统计概览卡片 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  color: var(--text-primary);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--border-lighter);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-icon {
  font-size: 2.5rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(102, 177, 255, 0.05));
  border-radius: var(--border-radius-md);
  transition: transform 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: var(--spacing-xs);
  background: linear-gradient(135deg, var(--text-primary), var(--text-regular));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 统计区块 */
.statistics-section {
  margin-bottom: var(--spacing-2xl);
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-lighter);
  position: relative;
  background: linear-gradient(135deg, var(--text-primary), var(--text-regular));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-title::before {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  border-radius: 2px;
}

.table-container {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow-x: auto;
  border: 1px solid var(--border-lighter);
}

.statistics-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.statistics-table th,
.statistics-table td {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  border-bottom: 1px solid var(--border-lighter);
  vertical-align: middle;
}

.statistics-table th {
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  font-weight: 600;
  color: var(--text-primary);
  position: sticky;
  top: 0;
  z-index: 10;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: var(--spacing-md) var(--spacing-lg);
}

.statistics-table tbody tr {
  transition: all 0.2s ease;
}

.statistics-table tbody tr:nth-child(even) {
  background-color: rgba(248, 249, 250, 0.5);
}

.statistics-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.05), rgba(102, 177, 255, 0.02));
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.revenue-cell {
  color: var(--primary-color);
  font-weight: 600;
}

.usage-bar {
  position: relative;
  width: 100%;
  height: 24px;
  background-color: var(--border-lighter);
  border-radius: var(--border-radius-sm);
  overflow: hidden;
}

.usage-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color) 0%, #66b1ff 100%);
  transition: width 0.3s ease;
}

.usage-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  z-index: 1;
}

/* 每日收入趋势 */
.chart-container {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-md);
}

.daily-revenue-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.daily-revenue-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: background-color 0.3s;
}

.daily-revenue-item:hover {
  background-color: var(--bg-secondary);
}

.day-info {
  min-width: 120px;
}

.day-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.day-date {
  font-size: 12px;
  color: var(--text-secondary);
}

.day-stats {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.day-revenue {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.day-bookings {
  font-size: 12px;
  color: var(--text-secondary);
}

.day-bar {
  flex: 2;
  height: 32px;
  background-color: var(--border-lighter);
  border-radius: var(--border-radius-sm);
  overflow: hidden;
  position: relative;
}

.day-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color) 0%, #66b1ff 100%);
  transition: width 0.3s ease;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-controls {
    width: 100%;
    flex-direction: column;
  }

  .week-select {
    width: 100%;
  }

  .stats-overview {
    grid-template-columns: 1fr;
  }

  .daily-revenue-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .day-bar {
    width: 100%;
  }
}
</style>



