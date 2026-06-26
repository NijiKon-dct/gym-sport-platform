<template>
  <div class="booking-form">
    <div class="booking-form__header">
      <div class="header-icon">📅</div>
      <h3 class="booking-form__title">预约信息</h3>
      <p class="booking-form__subtitle">请填写以下信息完成预约</p>
    </div>
    
    <!-- 时间预览卡片 -->
    <div v-if="formData.date && formData.startHour" class="time-preview-card">
      <div class="preview-icon">⏰</div>
      <div class="preview-content">
        <div class="preview-date">
          <span class="preview-label">预约日期</span>
          <span class="preview-value">{{ formatPreviewDate(formData.date) }}</span>
        </div>
        <div class="preview-time">
          <span class="preview-label">时间段</span>
          <span class="preview-value">{{ computedStartTime }} - {{ computedEndTime }}</span>
        </div>
        <div class="preview-duration">
          <span class="preview-label">总时长</span>
          <span class="preview-value highlight">{{ formatDuration(formData.durationHours) }}</span>
        </div>
      </div>
    </div>

    <BaseForm @submit="handleSubmit">
      <div class="form-section">
        <div class="section-header">
          <span class="section-icon">📆</span>
          <span class="section-title">日期与时间</span>
        </div>
        
        <BaseFormItem label="选择日期" required>
          <div class="input-wrapper">
            <span class="input-icon">📅</span>
            <input
              v-model="formData.date"
              type="date"
              class="form-date-input"
              :min="minDate"
            />
          </div>
        </BaseFormItem>
        
        <BaseFormItem label="开始时间" required>
          <div class="input-wrapper">
            <span class="input-icon">🕐</span>
            <select v-model="formData.startHour" class="form-select">
              <option v-for="hour in startHourOptions" :key="hour" :value="hour">
                {{ padHour(hour) }}:00
              </option>
            </select>
          </div>
          <div v-if="openTime || closeTime || occupiedBadgeText" class="badge-row">
            <div v-if="openTime || closeTime" class="info-badge">
              <span class="badge-icon">ℹ️</span>
              <span>开放时段：{{ displayOpenClose }}</span>
            </div>
            <div v-if="occupiedBadgeText" class="occupied-badge">
              <span class="badge-icon">📌</span>
              <span>{{ occupiedBadgeText }}</span>
            </div>
          </div>
          <div v-if="overlapWarning" class="overlap-warning">
            <span class="overlap-warning__icon">⚠️</span>
            {{ overlapWarning }}
          </div>
        </BaseFormItem>
        
        <BaseFormItem label="预约时长" required>
          <div class="input-wrapper">
            <span class="input-icon">⏱️</span>
            <select v-model.number="formData.durationHours" class="form-select">
              <option v-for="d in durationOptions" :key="d" :value="d">
                {{ formatDuration(d) }}
              </option>
            </select>
          </div>
          <div class="hint-text">
            <span class="hint-icon">💡</span>
            时间单位为小时，支持半小时
          </div>
        </BaseFormItem>
        
        <BaseFormItem label="结束时间">
          <div class="input-wrapper">
            <span class="input-icon">🕐</span>
            <input
              :value="computedEndTime"
              type="text"
              class="form-time-input"
              disabled
            />
          </div>
          <div class="hint-text">
            <span class="hint-icon">ℹ️</span>
            系统自动计算
          </div>
        </BaseFormItem>
      </div>

      <div class="form-section">
        <div class="section-header">
          <span class="section-icon">📝</span>
          <span class="section-title">备注信息</span>
        </div>
        
        <BaseFormItem label="备注">
          <div class="textarea-wrapper">
            <textarea
              v-model="formData.remark"
              class="form-textarea"
              rows="4"
              placeholder="请输入备注信息（选填）&#10;例如：需要特殊设备、人数等"
              maxlength="200"
            ></textarea>
            <div class="char-count">{{ formData.remark.length }}/200</div>
          </div>
        </BaseFormItem>
      </div>

      <div class="booking-form__actions">
        <BaseButton type="secondary" @click="handleCancel">
          <span class="button-icon">❌</span>
          取消
        </BaseButton>
        <BaseButton type="primary" htmlType="submit">
          <span class="button-icon">✅</span>
          确认预约
        </BaseButton>
      </div>
    </BaseForm>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { BaseForm, BaseFormItem, BaseButton } from './common'
import request from '@/utils/request'

interface BookingFormData {
  date: string
  startHour: number
  durationHours: number
  remark: string
}

interface OccupiedSlot {
  startTime: string
  endTime: string
}

const props = defineProps<{
  venueId: number
  openTime?: string
  closeTime?: string
}>()

const emit = defineEmits<{
  submit: [data: { date: string; startTime: string; endTime: string; remark: string }]
  cancel: []
}>()

const formData = ref<BookingFormData>({
  date: '',
  startHour: 8,
  durationHours: 1,
  remark: '',
})

const hourOptions = Array.from({ length: 24 }, (_, i) => i)
const durationOptions = [0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4]

const parseTimeToMinutes = (time?: string) => {
  if (!time) return null
  const [h, m] = time.split(':').map((v) => Number(v))
  if (Number.isNaN(h) || Number.isNaN(m)) return null
  return h * 60 + m
}

const openMinutes = computed(() => parseTimeToMinutes(props.openTime) ?? 0)
const closeMinutes = computed(() => parseTimeToMinutes(props.closeTime) ?? 24 * 60)

const startHourOptions = computed(() =>
  hourOptions.filter((h) => {
    const minutes = h * 60
    return minutes >= openMinutes.value && minutes < closeMinutes.value
  }),
)

watch(
  startHourOptions,
  (opts) => {
    if (opts.length > 0) {
      formData.value.startHour = opts[0]
    }
  },
  { immediate: true },
)

const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const padHour = (h: number) => h.toString().padStart(2, '0')

const formatDuration = (d: number) => `${d} 小时`

const computedStartTime = computed(() => `${padHour(formData.value.startHour)}:00`)

const computedEndTime = computed(() => {
  const startMinutes = formData.value.startHour * 60
  const durationMinutes = Math.round(formData.value.durationHours * 60)
  const endMinutes = startMinutes + durationMinutes
  const endHour = Math.floor(endMinutes / 60)
  const endMin = endMinutes % 60
  return `${endHour.toString().padStart(2, '0')}:${endMin.toString().padStart(2, '0')}`
})

/** 与后端 countOverlappingBookings 一致：半开区间，首尾相接不算重叠 */
const rangesOverlapMinutes = (
  newStart: number,
  newEnd: number,
  existStart: number,
  existEnd: number,
) => existStart < newEnd && existEnd > newStart

const parseClockToMinutes = (t: string) => {
  const parts = t.split(':').map((x) => Number(x))
  const h = parts[0] ?? 0
  const m = parts[1] ?? 0
  const s = parts[2] ?? 0
  return h * 60 + m + Math.round(s / 60)
}

const formatClockShort = (t: string) => {
  const [h, m] = t.split(':')
  return `${h!.padStart(2, '0')}:${(m ?? '00').padStart(2, '0')}`
}

const occupiedSlots = ref<OccupiedSlot[]>([])
const occupiedLoading = ref(false)

const fetchOccupiedSlots = async () => {
  const date = formData.value.date
  if (!date || !props.venueId) {
    occupiedSlots.value = []
    return
  }
  occupiedLoading.value = true
  try {
    const data = await request.get<OccupiedSlot[]>(
      `/bookings/occupied-slots?venueId=${props.venueId}&date=${encodeURIComponent(date)}`,
    )
    occupiedSlots.value = data ?? []
  } catch {
    occupiedSlots.value = []
  } finally {
    occupiedLoading.value = false
  }
}

watch(
  () => [formData.value.date, props.venueId] as const,
  () => {
    void fetchOccupiedSlots()
  },
)

const occupiedBadgeText = computed(() => {
  if (!formData.value.date) {
    return ''
  }
  if (occupiedLoading.value) {
    return '已预约时段加载中…'
  }
  if (occupiedSlots.value.length === 0) {
    return '当日暂无他人有效预约时段'
  }
  const parts = occupiedSlots.value.map(
    (s) => `${formatClockShort(s.startTime)}-${formatClockShort(s.endTime)}`,
  )
  return `已预约时段：${parts.join('，')}`
})

const selectionRangeMinutes = computed(() => {
  const start = formData.value.startHour * 60
  const end = start + Math.round(formData.value.durationHours * 60)
  return { start, end }
})

const overlapWarning = computed(() => {
  if (!formData.value.date || occupiedSlots.value.length === 0) return ''
  const { start, end } = selectionRangeMinutes.value
  for (const slot of occupiedSlots.value) {
    const os = parseClockToMinutes(slot.startTime)
    const oe = parseClockToMinutes(slot.endTime)
    if (rangesOverlapMinutes(start, end, os, oe)) {
      return `您选择的时间段与「${formatClockShort(slot.startTime)}-${formatClockShort(slot.endTime)}」重叠，该时段已被他人预约，请更换开始时间或时长。`
    }
  }
  return ''
})

const handleSubmit = () => {
  const startMinutes = formData.value.startHour * 60
  const durationMinutes = Math.round(formData.value.durationHours * 60)
  const endMinutes = startMinutes + durationMinutes
  if (startHourOptions.value.length === 0) {
    alert('当前场馆暂无可预约时间段')
    return
  }
  if (overlapWarning.value) {
    alert(overlapWarning.value)
    return
  }
  if (endMinutes > closeMinutes.value) {
    alert('预约时间超过场馆闭馆时间，请调整开始时间或时长')
    return
  }
  if (startMinutes < openMinutes.value) {
    alert('预约时间早于场馆开放时间')
    return
  }
  emit('submit', {
    date: formData.value.date,
    startTime: computedStartTime.value,
    endTime: computedEndTime.value,
    remark: formData.value.remark,
  })
}

const handleCancel = () => {
  emit('cancel')
}

const displayOpenClose = computed(() => {
  const open = props.openTime || '00:00'
  const close = props.closeTime || '24:00'
  return `${open} - ${close}`
})

const formatPreviewDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = weekdays[date.getDay()]
  return `${month}月${day}日 ${weekday}`
}
</script>

<style scoped>
.booking-form {
  background: linear-gradient(135deg, var(--bg-color) 0%, #f8f9fa 100%);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--border-lighter);
  position: relative;
  overflow: hidden;
}

.booking-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
}

.booking-form__header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-lighter);
}

.header-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-sm);
  animation: bounce 2s infinite;
}

.booking-form__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: var(--spacing-sm) 0;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.booking-form__subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 时间预览卡片 */
.time-preview-card {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border: 2px solid var(--primary-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  animation: slideInDown 0.5s ease-out;
}

.preview-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.preview-content {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--spacing-md);
}

.preview-date,
.preview-time,
.preview-duration {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.preview-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.preview-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.preview-value.highlight {
  color: var(--primary-color);
  font-size: 18px;
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表单区块 */
.form-section {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--bg-color);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-lighter);
}

.section-icon {
  font-size: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 输入框包装器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  font-size: 16px;
  z-index: 1;
  pointer-events: none;
}

.form-date-input,
.form-time-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 15px 12px 40px;
  border: 2px solid var(--border-base);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-color);
  transition: all 0.3s ease;
  outline: none;
  font-family: inherit;
}

.form-date-input:hover,
.form-time-input:hover,
.form-select:hover,
.form-textarea:hover {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.form-date-input:focus,
.form-time-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
  transform: translateY(-1px);
}

.form-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23303133' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 35px;
}

.form-time-input:disabled {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  cursor: not-allowed;
  opacity: 0.7;
}

.textarea-wrapper {
  position: relative;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  padding-right: 60px;
}

.char-count {
  position: absolute;
  bottom: 8px;
  right: 12px;
  font-size: 12px;
  color: var(--text-secondary);
  pointer-events: none;
}

/* 提示信息 */
.hint-text {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.hint-icon {
  font-size: 14px;
}

.badge-row {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 8px;
}

.info-badge {
  padding: 8px 12px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border: 1px solid var(--primary-color);
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  color: var(--primary-color);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.occupied-badge {
  padding: 8px 12px;
  background: linear-gradient(135deg, #fff8e6 0%, #ffefd5 100%);
  border: 1px solid #e6a23c;
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  color: #b88230;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  max-width: min(100%, 520px);
}

.overlap-warning {
  margin-top: 10px;
  padding: 10px 12px;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: var(--border-radius-sm);
  font-size: 13px;
  color: #c45656;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.overlap-warning__icon {
  flex-shrink: 0;
  line-height: 1.4;
}

.badge-icon {
  font-size: 14px;
}

/* 按钮样式 */
.booking-form__actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 2px solid var(--border-lighter);
}

.button-icon {
  margin-right: 6px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .booking-form {
    padding: var(--spacing-md);
  }

  .preview-content {
    grid-template-columns: 1fr;
  }

  .booking-form__actions {
    flex-direction: column;
  }

  .booking-form__actions .base-button {
    width: 100%;
  }
}
</style>

