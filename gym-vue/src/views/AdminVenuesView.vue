<template>
  <div class="admin-venues page-container">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">场馆管理</h1>
        <BaseButton type="primary" @click="showAddModal = true">添加场馆</BaseButton>
      </div>

      <!-- 概览统计 -->
      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon">🏟️</div>
          <div>
            <div class="stat-value">{{ totalVenues }}</div>
            <div class="stat-label">总场馆数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎓</div>
          <div>
            <div class="stat-value">{{ campusVenues }}</div>
            <div class="stat-label">校园场馆</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div>
            <div class="stat-value">¥{{ averagePrice }}</div>
            <div class="stat-label">平均价格 / 小时</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div>
            <div class="stat-value">{{ totalCapacity }}</div>
            <div class="stat-label">总可容纳人数</div>
          </div>
        </div>
      </div>

      <!-- 类型分布 -->
      <div class="distribution-section">
        <div class="section-header">
          <h2>场馆类型分布</h2>
          <span class="section-tip">共 {{ uniqueTypes }} 种类型</span>
        </div>
        <div class="distribution-content">
          <div class="pie-wrapper">
            <div class="pie-chart" :style="typePieStyle">
              <div class="pie-center">
                <div class="pie-number">{{ totalVenues }}</div>
                <div class="pie-label">场馆</div>
              </div>
            </div>
          </div>
          <ul class="distribution-list">
            <li v-for="stat in typeStats" :key="stat.type">
              <span class="legend-dot" :style="{ backgroundColor: stat.color }"></span>
              <span class="legend-name">{{ stat.type }}</span>
              <span class="legend-count">{{ stat.count }} 个</span>
              <div class="legend-bar">
                <div class="legend-fill" :style="{ width: stat.percentage + '%', backgroundColor: stat.color }"></div>
              </div>
              <span class="legend-percentage">{{ stat.percentage }}%</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-filters">
        <BaseInput
          v-model="searchQuery"
          placeholder="搜索场馆名称、地址或学校"
        />
        <BaseSelect v-model="filterType">
          <option value="">全部类型</option>
          <option value="综合体育馆">综合体育馆</option>
          <option value="篮球场">篮球场</option>
          <option value="羽毛球场">羽毛球场</option>
          <option value="游泳池">游泳池</option>
          <option value="健身房">健身房</option>
          <option value="网球场">网球场</option>
          <option value="乒乓球馆">乒乓球馆</option>
          <option value="舞蹈室">舞蹈室</option>
          <option value="足球场">足球场</option>
        </BaseSelect>
      </div>

      <!-- 场馆列表 -->
      <div class="venues-table-container">
        <table class="venues-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>场馆名称</th>
              <th>地址</th>
              <th>类型</th>
              <th>容量</th>
              <th>价格</th>
              <th>所属学校</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="venue in filteredVenues" :key="venue.id">
              <td>{{ venue.id }}</td>
              <td>{{ venue.name }}</td>
              <td>{{ venue.address }}</td>
              <td>{{ venue.type }}</td>
              <td>{{ venue.capacity }}</td>
              <td>¥{{ venue.price }}/小时</td>
              <td>{{ venue.school || '公共场馆' }}</td>
              <td class="actions">
                <BaseButton type="secondary" @click="editVenue(venue)">编辑</BaseButton>
                <BaseButton type="danger" @click="deleteVenue(venue.id)">删除</BaseButton>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="filteredVenues.length === 0" class="empty-state">暂无符合条件的场馆</div>
      </div>

      <!-- 添加/编辑场馆模态框 -->
      <BaseModal
        :visible="showAddModal || showEditModal"
        :title="showEditModal ? '编辑场馆' : '添加场馆'"
        @close="closeModal"
        class="venue-modal"
      >
        <BaseForm @submit="handleSubmit" class="venue-form">
          <!-- 基本信息组 -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <div class="form-row">
              <BaseFormItem label="场馆名称" required class="form-item">
                <BaseInput v-model="formData.name" placeholder="请输入场馆名称" :error="errors.name" />
              </BaseFormItem>
              <BaseFormItem label="场馆类型" required class="form-item">
                <BaseSelect v-model="formData.type">
                  <option value="">请选择场馆类型</option>
                  <option value="综合体育馆">综合体育馆</option>
                  <option value="篮球场">篮球场</option>
                  <option value="羽毛球场">羽毛球场</option>
                  <option value="游泳池">游泳池</option>
                  <option value="健身房">健身房</option>
                  <option value="网球场">网球场</option>
                  <option value="乒乓球馆">乒乓球馆</option>
                  <option value="舞蹈室">舞蹈室</option>
                  <option value="足球场">足球场</option>
                </BaseSelect>
              </BaseFormItem>
            </div>

            <BaseFormItem label="地址" required class="form-item">
              <BaseInput
                v-model="formData.address"
                placeholder="请输入场馆地址"
                :error="errors.address"
              />
            </BaseFormItem>

            <div class="form-row">
              <BaseFormItem label="容量" required class="form-item">
                <BaseInput
                  v-model.number="formData.capacity"
                  type="number"
                  placeholder="请输入场馆容量"
                  :error="errors.capacity"
                />
              </BaseFormItem>
              <BaseFormItem label="价格" required class="form-item">
                <BaseInput
                  v-model.number="formData.price"
                  type="number"
                  placeholder="请输入每小时价格"
                  :error="errors.price"
                />
              </BaseFormItem>
            </div>
          </div>

          <!-- 场馆性质组 -->
          <div class="form-section">
            <h3 class="section-title">场馆性质</h3>
            <BaseFormItem label="场馆性质" required class="form-item">
              <div class="venue-type-selector">
                <label class="radio-label">
                  <input
                    type="radio"
                    v-model="formData.isSchoolVenue"
                    value="true"
                    class="radio-input"
                  />
                  <span class="radio-text">学校场馆</span>
                </label>
                <label class="radio-label">
                  <input
                    type="radio"
                    v-model="formData.isSchoolVenue"
                    value="false"
                    class="radio-input"
                    @change="formData.school = ''"
                  />
                  <span class="radio-text">社会场馆</span>
                </label>
              </div>
            </BaseFormItem>

            <BaseFormItem label="所属学校" v-if="formData.isSchoolVenue === 'true'" class="form-item">
              <BaseSelect v-model="formData.school">
                <option value="">请选择所属学校</option>
                <option v-for="school in schoolOptions" :key="school" :value="school">
                  {{ school }}
                </option>
              </BaseSelect>
            </BaseFormItem>
          </div>

          <!-- 开放信息组 -->
          <div class="form-section">
            <h3 class="section-title">开放信息</h3>
            <div class="form-row">
              <BaseFormItem label="开放时间" class="form-item">
                <BaseSelect v-model="formData.openTime">
                  <option value="">请选择开放时间</option>
                  <option value="07:00">07:00</option>
                  <option value="08:00">08:00</option>
                  <option value="09:00">09:00</option>
                  <option value="10:00">10:00</option>
                  <option value="11:00">11:00</option>
                  <option value="12:00">12:00</option>
                  <option value="13:00">13:00</option>
                  <option value="14:00">14:00</option>
                  <option value="15:00">15:00</option>
                  <option value="16:00">16:00</option>
                  <option value="17:00">17:00</option>
                </BaseSelect>
              </BaseFormItem>
              <BaseFormItem label="关闭时间" class="form-item">
                <BaseSelect v-model="formData.closeTime">
                  <option value="">请选择关闭时间</option>
                  <option value="08:00">08:00</option>
                  <option value="09:00">09:00</option>
                  <option value="10:00">10:00</option>
                  <option value="11:00">11:00</option>
                  <option value="12:00">12:00</option>
                  <option value="13:00">13:00</option>
                  <option value="14:00">14:00</option>
                  <option value="15:00">15:00</option>
                  <option value="16:00">16:00</option>
                  <option value="17:00">17:00</option>
                  <option value="18:00">18:00</option>
                  <option value="19:00">19:00</option>
                  <option value="20:00">20:00</option>
                  <option value="21:00">21:00</option>
                  <option value="22:00">22:00</option>
                  <option value="23:00">23:00</option>
                </BaseSelect>
              </BaseFormItem>
            </div>
          </div>

          <!-- 图片和描述组 -->
          <div class="form-section">
            <h3 class="section-title">图片和描述</h3>
            <BaseFormItem label="场馆图片" class="form-item">
              <div class="image-upload-section">
                <input
                  type="file"
                  accept="image/*"
                  class="file-input"
                  @change="handleImageUpload"
                  ref="fileInput"
                />
                <BaseButton type="secondary" @click="triggerFileInput" class="upload-btn">选择图片</BaseButton>
                <div v-if="formData.image" class="preview-image">
                  <img :src="getImageUrl(formData.image)" alt="场馆图片预览" class="preview-img" />
                  <button class="remove-image" @click.stop="removeImage" title="移除图片">✕</button>
                </div>
                <div v-else class="no-image">
                  <span class="no-image-text">未选择图片，将使用默认图片</span>
                </div>
              </div>
            </BaseFormItem>

            <BaseFormItem label="描述" class="form-item">
              <textarea
                v-model="formData.description"
                class="description-textarea"
                placeholder="请输入场馆描述（可选）"
                rows="4"
              ></textarea>
            </BaseFormItem>
          </div>

          <div class="modal-actions">
            <BaseButton type="secondary" @click="closeModal">取消</BaseButton>
            <BaseButton type="primary" htmlType="submit">
              {{ showEditModal ? '保存修改' : '添加' }}
            </BaseButton>
          </div>
        </BaseForm>
      </BaseModal>
    </div>

    <!-- 删除确认弹窗 -->
    <BaseModal
      :visible="showDeleteModal"
      title="确认删除"
      @close="showDeleteModal = false"
      class="delete-modal"
    >
      <div class="confirm-content">
        <p>删除后数据不可恢复，确定要删除该场馆吗？</p>
      </div>
      <template #footer>
        <BaseButton type="secondary" @click="showDeleteModal = false">取消</BaseButton>
        <BaseButton type="danger" @click="confirmDelete">确认删除</BaseButton>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useVenueStore, type Venue } from '@/stores/venue'
import { useSchoolStore } from '@/stores/school'
import {
  BaseButton,
  BaseInput,
  BaseForm,
  BaseFormItem,
  BaseModal,
  BaseSelect,
} from '@/components/common'
import request from '@/utils/request'

// 文件上传相关
const fileInput = ref<HTMLInputElement | null>(null)

const router = useRouter()
const venueStore = useVenueStore()
const schoolStore = useSchoolStore()

const chartColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#a66dd4', '#20c997', '#ff7f50', '#3ba272']

// 学校选项
const schoolOptions = computed(() => {
  if (!schoolStore.schools.length) {
    schoolStore.resetToDefault()
  }
  return schoolStore.schools
})

// 模态框状态
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const pendingDeleteId = ref<number | null>(null)
const editingVenueId = ref<number | null>(null)

// 搜索和筛选
const searchQuery = ref('')
const filterType = ref('')

// 表单数据
const formData = reactive({
  name: '',
  address: '',
  type: '',
  capacity: 0,
  price: 0,
  isSchoolVenue: 'false', // 默认是社会场馆
  school: '',
  openTime: '',
  closeTime: '',
  description: '',
  image: '',
})

// 表单验证错误
const errors = reactive({
  name: '',
  address: '',
  type: '',
  capacity: '',
  price: '',
})

// 过滤后的场馆列表 - 明确返回类型
const filteredVenues = computed<Venue[]>(() => {
  return venueStore.venues.filter((venue) => {
    // 搜索条件
    const matchesSearch =
      !searchQuery.value ||
      venue.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      venue.address.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (venue.school && venue.school.toLowerCase().includes(searchQuery.value.toLowerCase()))

    // 类型筛选
    const matchesType = !filterType.value || venue.type === filterType.value

    return matchesSearch && matchesType
  })
})

const totalVenues = computed(() => venueStore.venues.length)

const campusVenues = computed(() => venueStore.venues.filter((venue) => venue.school).length)

const averagePrice = computed(() => {
  if (!venueStore.venues.length) return 0
  const totalPrice = venueStore.venues.reduce((sum, venue) => sum + (venue.price || 0), 0)
  return Math.round(totalPrice / venueStore.venues.length)
})

const totalCapacity = computed(() => {
  return venueStore.venues.reduce((sum, venue) => sum + (venue.capacity || 0), 0)
})

const typeStats = computed(() => {
  const counts: Record<string, number> = {}
  venueStore.venues.forEach((venue) => {
    counts[venue.type] = (counts[venue.type] || 0) + 1
  })
  const entries = Object.entries(counts)
  return entries.map(([type, count], index) => {
    const percentage = totalVenues.value ? Math.round((count / totalVenues.value) * 100) : 0
    return {
      type,
      count,
      percentage,
      color: chartColors[index % chartColors.length],
    }
  })
})

const uniqueTypes = computed(() => typeStats.value.length)

const typePieStyle = computed(() => {
  if (!typeStats.value.length) {
    return { background: '#f0f2f5' }
  }
  let currentAngle = 0
  const segments = typeStats.value.map((stat) => {
    const angle = (stat.percentage / 100) * 360
    const start = currentAngle
    const end = currentAngle + angle
    currentAngle = end
    return `${stat.color} ${start}deg ${end}deg`
  })
  return {
    background: `conic-gradient(${segments.join(',')})`,
  }
})

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理图片上传
const handleImageUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    const file = input.files[0]

    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件')
      return
    }

    // 验证文件大小（限制为5MB）
    if (file.size > 5 * 1024 * 1024) {
      alert('图片大小不能超过5MB')
      return
    }

    // 上传文件
    try {
      const uploadFormData = new FormData()
      uploadFormData.append('file', file)

      const response = await request.post<{ path: string; url: string }>('/upload/venue', uploadFormData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })

      // 保存路径
      formData.image = response.path
    } catch (error) {
      alert('图片上传失败，请稍后重试')
    }
  }
}

// 移除已上传的图片
const removeImage = () => {
  formData.image = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 获取图片URL（用于显示）
const getImageUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

// 重置表单
const resetForm = () => {
  formData.name = ''
  formData.address = ''
  formData.type = ''
  formData.capacity = 0
  formData.price = 0
  formData.isSchoolVenue = 'false'
  formData.school = ''
  formData.openTime = ''
  formData.closeTime = ''
  formData.description = ''
  formData.image = ''

  // 清空错误信息
  Object.keys(errors).forEach((key) => {
    errors[key as keyof typeof errors] = ''
  })
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingVenueId.value = null
  resetForm()
}

// 编辑场馆
const editVenue = (venue: Venue) => {
  // 填充表单数据
  formData.name = venue.name
  formData.address = venue.address
  formData.type = venue.type
  formData.capacity = venue.capacity
  formData.price = venue.price
  formData.isSchoolVenue = venue.school ? 'true' : 'false' // 根据是否有学校判断场馆性质
  formData.school = venue.school || ''
  formData.openTime = venue.openTime || ''
  formData.closeTime = venue.closeTime || ''
  formData.description = venue.description || ''
  formData.image = venue.image || ''

  editingVenueId.value = venue.id
  showEditModal.value = true
}

// 删除场馆
const deleteVenue = async (id: number) => {
  pendingDeleteId.value = id
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!pendingDeleteId.value) return
  try {
    await venueStore.deleteVenue(pendingDeleteId.value)
    alert('场馆删除成功！')
  } catch (error) {
    console.error('删除场馆失败:', error)
    alert('删除场馆失败，请重试')
  } finally {
    showDeleteModal.value = false
    pendingDeleteId.value = null
  }
}

// 表单验证
const validateForm = () => {
  let isValid = true

  if (!formData.name.trim()) {
    errors.name = '请输入场馆名称'
    isValid = false
  } else {
    errors.name = ''
  }

  if (!formData.address.trim()) {
    errors.address = '请输入场馆地址'
    isValid = false
  } else {
    errors.address = ''
  }

  if (!formData.type) {
    errors.type = '请选择场馆类型'
    isValid = false
  } else {
    errors.type = ''
  }

  if (!formData.capacity || formData.capacity <= 0) {
    errors.capacity = '请输入有效的容量'
    isValid = false
  } else {
    errors.capacity = ''
  }

  if (formData.price < 0) {
    errors.price = '价格不能为负数'
    isValid = false
  } else {
    errors.price = ''
  }

  return isValid
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    const venueData = {
      name: formData.name,
      address: formData.address,
      type: formData.type,
      capacity: formData.capacity,
      price: formData.price,
      school: formData.isSchoolVenue === 'true' ? formData.school || undefined : undefined,
      openTime: formData.openTime || undefined,
      closeTime: formData.closeTime || undefined,
      description: formData.description || undefined,
      image: formData.image || undefined, // 如果没有上传图片，使用undefined而不是随机图片
    }

    if (showEditModal.value && editingVenueId.value) {
      // 更新场馆
      await venueStore.updateVenue(editingVenueId.value, venueData)
      alert('场馆更新成功！')
    } else {
      // 添加场馆
      await venueStore.addVenue(venueData)
      alert('场馆添加成功！')
    }
    closeModal()
  } catch (error) {
    console.error('操作失败:', error)
    alert('操作失败，请重试')
  }
}

onMounted(async () => {
  // 确保加载场馆数据
  await venueStore.fetchVenues()
  // 确保学校列表已初始化
  if (!schoolStore.schools.length) {
    schoolStore.resetToDefault()
  }
})
</script>

<style scoped>
.admin-venues {
  padding: var(--spacing-xl) 0;
}

/* 模态框样式 */
.venue-modal {
  max-width: 700px;
  width: 90%;
}

.venue-form {
  padding: var(--spacing-md) 0;
}

/* 表单分组 */
.form-section {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background-color: var(--bg-color);
  border-radius: var(--border-radius-md);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-light);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-light);
}

/* 表单行布局 */
.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-item {
  margin-bottom: var(--spacing-lg);
}

.form-item:last-child {
  margin-bottom: 0;
}

/* 场馆类型选择器 */
.venue-type-selector {
  display: flex;
  gap: var(--spacing-xl);
  padding: var(--spacing-sm) 0;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.radio-label:hover {
  color: var(--primary-color);
}

.radio-input {
  margin: 0;
  cursor: pointer;
}

.radio-text {
  cursor: pointer;
  user-select: none;
}

/* 图片上传区域 */
.image-upload-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-md) 0;
}

.upload-btn {
  align-self: flex-start;
}

.preview-image {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: auto;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border-light);
}

.preview-img {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
  transition: transform 0.2s ease;
}

.preview-image:hover .preview-img {
  transform: scale(1.02);
}

.remove-image {
  position: absolute;
  top: var(--spacing-xs);
  right: var(--spacing-xs);
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background-color: rgba(245, 108, 108, 0.9);
  color: white;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  line-height: 1;
}

.remove-image:hover {
  background-color: var(--danger-color);
  transform: scale(1.1);
}

.no-image {
  padding: var(--spacing-md);
  background-color: var(--bg-extra-light);
  border: 1px dashed var(--border-light);
  border-radius: var(--border-radius-md);
  width: fit-content;
}

.no-image-text {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 描述文本框 */
.description-textarea {
  width: 100%;
  padding: var(--spacing-sm);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  resize: vertical;
  font-size: 14px;
  line-height: 1.5;
  font-family: inherit;
  transition: all 0.2s ease;
}

.description-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 模态框按钮区域 */
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-light);
}

.delete-modal .confirm-content {
  padding: var(--spacing-lg);
  color: var(--text-regular);
  font-size: 14px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
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

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
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

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
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

.distribution-section {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--spacing-xl);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.section-header h2 {
  font-size: 20px;
  margin: 0;
  color: var(--text-primary);
}

.section-tip {
  font-size: 14px;
  color: var(--text-secondary);
}

.distribution-content {
  display: flex;
  gap: var(--spacing-xl);
  flex-wrap: wrap;
}

.pie-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 220px;
}

.pie-chart {
  width: 220px;
  height: 220px;
  border-radius: 50%;
  position: relative;
  box-shadow: inset 0 0 30px rgba(0, 0, 0, 0.05);
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: var(--bg-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
}

.pie-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.pie-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.distribution-list {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.distribution-list li {
  display: grid;
  grid-template-columns: auto 120px auto 1fr auto;
  align-items: center;
  gap: var(--spacing-sm);
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.legend-name {
  min-width: 80px;
  font-weight: 500;
  color: var(--text-primary);
}

.legend-count {
  color: var(--text-secondary);
  font-size: 14px;
}

.legend-bar {
  position: relative;
  width: 100%;
  height: 8px;
  background-color: var(--border-extra-light);
  border-radius: 999px;
  overflow: hidden;
}

.legend-fill {
  height: 100%;
  border-radius: inherit;
  transition: width 0.3s ease;
}

.legend-percentage {
  min-width: 40px;
  text-align: right;
  font-weight: 600;
  color: var(--text-primary);
}

.search-filters {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  flex-wrap: wrap;
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.8), rgba(233, 236, 239, 0.5));
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-filters .base-input {
  flex: 1;
  min-width: 200px;
}

.search-filters .base-select {
  width: 200px;
}

.venues-table-container {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow-x: auto;
  border: 1px solid var(--border-lighter);
}

.venues-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.venues-table th,
.venues-table td {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  border-bottom: 1px solid var(--border-lighter);
  vertical-align: middle;
}

.venues-table th {
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

.venues-table tbody tr {
  transition: all 0.2s ease;
}

.venues-table tbody tr:nth-child(even) {
  background-color: rgba(248, 249, 250, 0.5);
}

.venues-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.05), rgba(102, 177, 255, 0.02));
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
  justify-content: flex-start;
  white-space: nowrap;
}

.actions .base-button {
  padding: var(--spacing-xs) var(--spacing-md);
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
  font-size: 16px;
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.5), rgba(233, 236, 239, 0.3));
  border-radius: var(--border-radius-md);
  border: 2px dashed var(--border-light);
}

.description-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  resize: vertical;
  font-family: inherit;
  font-size: inherit;
}

.description-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
}

/* 场馆性质选择器样式 */
.venue-type-selector {
  display: flex;
  gap: var(--spacing-lg);
  margin: var(--spacing-md) 0;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: var(--spacing-sm);
}

.radio-input {
  cursor: pointer;
  margin: 0;
}

.radio-text {
  font-size: 15px;
  color: var(--text-primary);
  user-select: none;
}

/* 图片上传样式 */
.image-upload-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.file-input {
  display: none;
}

.preview-image {
  position: relative;
  width: 200px;
  height: auto;
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.preview-image img {
  width: 100%;
  height: auto;
  display: block;
}

.remove-image {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-image:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .search-filters {
    flex-direction: column;
  }

  .search-filters .base-select {
    width: 100%;
  }
}
</style>
