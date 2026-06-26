<template>
  <div class="admin-users page-container">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">用户管理</h1>
        <BaseButton type="primary" @click="showAddModal = true">添加用户</BaseButton>
      </div>

      <!-- 概览统计 -->
      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div>
            <div class="stat-value">{{ totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🛡️</div>
          <div>
            <div class="stat-value">{{ adminCount }}</div>
            <div class="stat-label">管理员</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎓</div>
          <div>
            <div class="stat-value">{{ studentCount }}</div>
            <div class="stat-label">学生用户</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📄</div>
          <div>
            <div class="stat-value">{{ profileCompletionRate }}%</div>
            <div class="stat-label">资料完善度</div>
          </div>
        </div>
      </div>

      <div class="insights-grid">
        <div class="insight-card">
          <div class="section-header">
            <h2>角色占比</h2>
            <span class="section-tip">覆盖 {{ roleStats.length }} 种角色</span>
          </div>
          <div class="role-chart">
            <div class="pie-chart" :style="rolePieStyle">
              <div class="pie-center">
                <div class="pie-number">{{ totalUsers }}</div>
                <div class="pie-label">用户</div>
              </div>
            </div>
            <ul class="role-legend">
              <li v-for="role in roleStats" :key="role.label">
                <span class="legend-dot" :style="{ backgroundColor: role.color }"></span>
                <span class="legend-name">{{ role.label }}</span>
                <span class="legend-count">{{ role.count }} 人</span>
                <div class="legend-bar">
                  <div class="legend-fill" :style="{ width: role.percentage + '%', backgroundColor: role.color }"></div>
                </div>
                <span class="legend-percentage">{{ role.percentage }}%</span>
              </li>
            </ul>
          </div>
        </div>
        <div class="insight-card">
          <div class="section-header">
            <h2>学校分布 Top 5</h2>
            <span class="section-tip">共 {{ totalSchools }} 所学校</span>
          </div>
          <ul class="school-list">
            <li v-for="(school, index) in schoolStats" :key="school.name">
              <div class="school-info">
                <span class="school-rank">#{{ index + 1 }}</span>
                <div>
                  <div class="school-name">{{ school.name }}</div>
                  <div class="school-count">{{ school.count }} 名用户</div>
                </div>
              </div>
              <div class="school-bar">
                <div class="school-fill" :style="{ width: school.percentage + '%'}"></div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-filters">
        <BaseInput
          v-model="searchQuery"
          placeholder="搜索用户名、账号或邮箱"
        />
        <BaseSelect v-model="filterRole">
          <option value="">全部角色</option>
          <option value="admin">管理员</option>
          <option value="student">学生</option>
          <option value="normal">普通用户</option>
        </BaseSelect>
      </div>

      <!-- 用户列表 -->
      <div class="users-table-container">
        <table class="users-table">
          <thead>
            <tr>
              <th>操作</th>
              <th>ID</th>
              <th>头像</th>
              <th>账号</th>
              <th>用户名</th>
              <th>昵称</th>
              <th>邮箱</th>
              <th>手机号</th>
              <th>角色</th>
              <th>所属学校</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td class="actions">
                <BaseButton type="secondary" @click="editUser(user)">编辑</BaseButton>
                <BaseButton
                  type="danger"
                  @click="deleteUser(user.id)"
                  :disabled="user.id === currentUserId"
                >
                  删除
                </BaseButton>
              </td>
              <td>{{ user.id }}</td>
              <td>
                <img
                  v-if="getAvatarUrl(user.avatar)"
                  :src="getAvatarUrl(user.avatar)"
                  alt="用户头像"
                  class="avatar"
                />
                <div v-else class="avatar-placeholder">{{ user.username?.[0]?.toUpperCase() || '👤' }}</div>
              </td>
              <td>{{ user.account }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.nickname || '-' }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.phone || '-' }}</td>
              <td>
                <span :class="['role-badge', getRoleClass(user)]">
                  {{ getRoleName(user) }}
                </span>
              </td>
              <td>{{ user.school || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <div v-if="filteredUsers.length === 0" class="empty-state">暂无符合条件的用户</div>
      </div>

      <!-- 添加/编辑用户模态框 -->
      <BaseModal
        :visible="showAddModal || showEditModal"
        :title="showEditModal ? '编辑用户' : '添加用户'"
        @close="closeModal"
      >
        <BaseForm @submit="handleSubmit">
          <BaseFormItem label="账号" required :disabled="showEditModal">
            <BaseInput
              v-model="formData.account"
              placeholder="请输入账号"
              :error="errors.account"
              :disabled="showEditModal"
            />
          </BaseFormItem>

          <BaseFormItem label="用户名" required>
            <BaseInput
              v-model="formData.username"
              placeholder="请输入用户名"
              :error="errors.username"
            />
          </BaseFormItem>

          <BaseFormItem label="邮箱" required>
            <BaseInput
              v-model="formData.email"
              type="email"
              placeholder="请输入邮箱"
              :error="errors.email"
            />
          </BaseFormItem>

          <BaseFormItem label="手机号">
            <BaseInput
              v-model="formData.phone"
              placeholder="请输入手机号（可选）"
              :error="errors.phone"
            />
          </BaseFormItem>

          <BaseFormItem label="昵称">
            <BaseInput v-model="formData.nickname" placeholder="请输入昵称（可选）" />
          </BaseFormItem>

          <BaseFormItem label="是否为学生">
            <input v-model="formData.isStudent" type="checkbox" class="checkbox-input" />
          </BaseFormItem>

          <BaseFormItem v-if="formData.isStudent" label="所属学校">
            <BaseSelect v-model="formData.school">
              <option value="">请选择所属学校</option>
              <option v-for="school in schoolOptions" :key="school" :value="school">
                {{ school }}
              </option>
            </BaseSelect>
          </BaseFormItem>

          <BaseFormItem label="是否为管理员" v-if="!isCurrentUser">
            <input v-model="formData.isAdmin" type="checkbox" class="checkbox-input" />
            <span class="form-hint">（非管理员账号登录时可见）</span>
          </BaseFormItem>

          <BaseFormItem label="个人简介">
            <textarea
              v-model="formData.bio"
              class="bio-textarea"
              placeholder="请输入个人简介（可选）"
              rows="3"
            ></textarea>
          </BaseFormItem>

          <div class="modal-actions">
            <BaseButton type="secondary" @click="closeModal">取消</BaseButton>
            <BaseButton type="primary" htmlType="submit">
              {{ showEditModal ? '保存修改' : '添加' }}
            </BaseButton>
          </div>
        </BaseForm>
      </BaseModal>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore, type UserInfo } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import {
  BaseButton,
  BaseInput,
  BaseForm,
  BaseFormItem,
  BaseModal,
  BaseSelect,
} from '@/components/common'

const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()

const chartColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c']

// 当前登录用户ID
const currentUserId = computed(() => userStore.userInfo?.id || 0)

// 是否正在编辑当前登录用户
const isCurrentUser = computed(() => {
  return showEditModal.value && editingUserId.value === currentUserId.value
})

// 模态框状态
const showAddModal = ref(false)
const showEditModal = ref(false)
const editingUserId = ref<number | null>(null)

// 搜索和筛选
const searchQuery = ref('')
const filterRole = ref('')

// 表单数据 - 添加明确类型
const formData = reactive<{
  account: string
  username: string
  email: string
  phone: string
  nickname: string
  isStudent: boolean
  school: string
  isAdmin: boolean
  bio: string
  avatar: string
}>({
  account: '',
  username: '',
  email: '',
  phone: '',
  nickname: '',
  isStudent: false,
  school: '',
  isAdmin: false,
  bio: '',
  avatar: '',
})

// 表单验证错误
const errors = reactive({
  account: '',
  username: '',
  email: '',
  phone: '',
})

// 过滤后的用户列表
const filteredUsers = computed(() => {
  return userStore.users.filter((user) => {
    // 搜索条件
    const matchesSearch =
      !searchQuery.value ||
      user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.account.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (user.nickname && user.nickname.toLowerCase().includes(searchQuery.value.toLowerCase()))

    // 角色筛选
    let matchesRole = true
    if (filterRole.value) {
      switch (filterRole.value) {
        case 'admin':
          matchesRole = user.isAdmin || false
          break
        case 'student':
          matchesRole = user.isStudent || false
          break
        case 'normal':
          matchesRole = !(user.isAdmin || false) && !(user.isStudent || false)
          break
      }
    }

    return matchesSearch && matchesRole
  })
})

const totalUsers = computed(() => userStore.users.length)
const adminCount = computed(() => userStore.users.filter((user) => user.isAdmin).length)
const studentCount = computed(() => userStore.users.filter((user) => user.isStudent && !user.isAdmin).length)
const normalCount = computed(() => Math.max(totalUsers.value - adminCount.value - studentCount.value, 0))
const profileCompleted = computed(() =>
  userStore.users.filter((user) => user.phone && user.school && user.nickname).length,
)
const profileCompletionRate = computed(() => {
  if (!totalUsers.value) return 0
  return Math.round((profileCompleted.value / totalUsers.value) * 100)
})

const roleStats = computed(() => {
  const data = [
    { label: '管理员', count: adminCount.value },
    { label: '学生', count: studentCount.value },
    { label: '普通用户', count: normalCount.value },
  ]
  return data
    .filter((item) => item.count > 0)
    .map((item, index) => ({
      ...item,
      percentage: totalUsers.value ? Math.round((item.count / totalUsers.value) * 100) : 0,
      color: chartColors[index % chartColors.length],
    }))
})

const rolePieStyle = computed(() => {
  if (!roleStats.value.length) {
    return { background: '#f0f2f5' }
  }
  let currentAngle = 0
  const segments = roleStats.value.map((role) => {
    const angle = (role.percentage / 100) * 360
    const start = currentAngle
    const end = currentAngle + angle
    currentAngle = end
    return `${role.color} ${start}deg ${end}deg`
  })
  return {
    background: `conic-gradient(${segments.join(',')})`,
  }
})

const schoolStats = computed(() => {
  const counts: Record<string, number> = {}
  userStore.users.forEach((user) => {
    if (user.school) {
      counts[user.school] = (counts[user.school] || 0) + 1
    }
  })
  const entries = Object.entries(counts)
    .map(([name, count]) => ({
      name,
      count,
      percentage: totalUsers.value ? Math.round((count / totalUsers.value) * 100) : 0,
    }))
    .sort((a, b) => b.count - a.count)
  return entries.slice(0, 5)
})

const totalSchools = computed(() => {
  const schoolSet = new Set(userStore.users.map((user) => user.school).filter(Boolean))
  return schoolSet.size
})

// 学校选项
const schoolOptions = computed(() => {
  if (!schoolStore.schools.length) {
    schoolStore.resetToDefault()
  }
  return schoolStore.schools
})

// 获取角色名称
const getRoleName = (user: UserInfo): string => {
  if (user.isAdmin) return '管理员'
  if (user.isStudent) return '学生'
  return '普通用户'
}

// 获取角色样式类
const getRoleClass = (user: UserInfo): string => {
  if (user.isAdmin) return 'role-admin'
  if (user.isStudent) return 'role-student'
  return 'role-normal'
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

// 重置表单
const resetForm = () => {
  formData.account = ''
  formData.username = ''
  formData.email = ''
  formData.phone = ''
  formData.nickname = ''
  formData.isStudent = false
  formData.school = ''
  formData.isAdmin = false
  formData.bio = ''
  formData.avatar = ''

  // 清空错误信息
  Object.keys(errors).forEach((key) => {
    errors[key as keyof typeof errors] = ''
  })
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingUserId.value = null
  resetForm()
}

// 编辑用户
const editUser = (user: UserInfo) => {
  // 填充表单数据
  formData.account = user.account
  formData.username = user.username
  formData.email = user.email
  formData.phone = user.phone || ''
  formData.nickname = user.nickname || ''
  formData.isStudent = user.isStudent || false
  formData.school = user.school || ''
  formData.isAdmin = user.isAdmin || false
  formData.bio = user.bio || ''
  formData.avatar = user.avatar || ''

  editingUserId.value = user.id
  showEditModal.value = true
}

// 删除用户
const deleteUser = (id: number) => {
  if (id === currentUserId.value) {
    alert('不能删除当前登录用户')
    return
  }

  if (confirm('确定要删除这个用户吗？')) {
    try {
      userStore.deleteUser(id)
      alert('用户删除成功！')
    } catch (error) {
      alert((error as Error).message)
    }
  }
}

// 表单验证
const validateForm = () => {
  let isValid = true

  if (!showEditModal.value) {
    if (!formData.account.trim()) {
      errors.account = '请输入账号'
      isValid = false
    } else if (!/^\d+$/.test(formData.account)) {
      errors.account = '账号必须为纯数字'
      isValid = false
    } else {
      errors.account = ''
    }
  }

  if (!formData.username.trim()) {
    errors.username = '请输入用户名'
    isValid = false
  } else {
    errors.username = ''
  }

  if (!formData.email.trim()) {
    errors.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  } else {
    errors.email = ''
  }

  if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
    errors.phone = '请输入有效的手机号'
    isValid = false
  } else {
    errors.phone = ''
  }

  return isValid
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    if (showEditModal.value && editingUserId.value) {
      // 更新用户
      await userStore.updateUser(editingUserId.value, {
        username: formData.username,
        email: formData.email,
        phone: formData.phone || undefined,
        nickname: formData.nickname || undefined,
        isStudent: formData.isStudent,
        school: formData.isStudent ? formData.school : undefined,
        isAdmin: formData.isAdmin,
        bio: formData.bio || undefined,
        avatar: formData.avatar || undefined,
      })
      alert('用户更新成功！')
    } else {
      // 添加用户
      await userStore.addUser({
        account: formData.account,
        username: formData.username,
        email: formData.email,
        phone: formData.phone || undefined,
        nickname: formData.nickname || undefined,
        isStudent: formData.isStudent,
        school: formData.isStudent ? formData.school : undefined,
        isAdmin: formData.isAdmin,
        bio: formData.bio || undefined,
        avatar: formData.avatar || undefined,
      })
      alert('用户添加成功！')
    }
    closeModal()
  } catch (error) {
    console.error('操作失败:', error)
    alert('操作失败，请重试')
  }
}

onMounted(() => {
  // 确保加载用户数据
  if (userStore.users.length === 0) {
    userStore.fetchUsers()
  }
  // 确保学校列表已初始化
  if (!schoolStore.schools.length) {
    schoolStore.resetToDefault()
  }
})
</script>

<style scoped>
.avatar-placeholder {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar-placeholder:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}
.admin-users {
  padding: var(--spacing-xl) 0;
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

.insights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.insight-card {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-md);
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
  font-weight: 700;
  background: linear-gradient(135deg, var(--text-primary), var(--text-regular));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-tip {
  font-size: 14px;
  color: var(--text-secondary);
}

.role-chart {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
  align-items: center;
}

.pie-chart {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  position: relative;
  box-shadow: inset 0 0 30px rgba(0, 0, 0, 0.05);
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background-color: var(--bg-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
}

.pie-number {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
}

.pie-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.role-legend {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.role-legend li {
  display: grid;
  grid-template-columns: auto auto auto 1fr auto;
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
  font-weight: 500;
  color: var(--text-primary);
}

.legend-count {
  font-size: 14px;
  color: var(--text-secondary);
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

.school-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.school-info {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  margin-bottom: var(--spacing-xs);
}

.school-rank {
  font-weight: 700;
  color: var(--primary-color);
}

.school-name {
  font-weight: 600;
  color: var(--text-primary);
}

.school-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.school-bar {
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background-color: var(--border-extra-light);
  overflow: hidden;
}

.school-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  transition: width 0.3s ease;
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

.users-table-container {
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow-x: auto;
  border: 1px solid var(--border-lighter);
}

.users-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.users-table th,
.users-table td {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  border-bottom: 1px solid var(--border-lighter);
  vertical-align: middle;
  white-space: nowrap;
  height: 100%;
}

.users-table th {
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

.users-table tbody tr {
  transition: all 0.2s ease;
}

.users-table tbody tr:nth-child(even) {
  background-color: rgba(248, 249, 250, 0.5);
}

.users-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.05), rgba(102, 177, 255, 0.02));
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  border-color: var(--primary-color);
}

.role-badge {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  display: inline-block;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.role-badge:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.role-admin {
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  color: #1565c0;
  border: 1px solid rgba(21, 101, 192, 0.2);
}

.role-student {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  color: #2e7d32;
  border: 1px solid rgba(46, 125, 50, 0.2);
}

.role-normal {
  background: linear-gradient(135deg, #f5f5f5, #e0e0e0);
  color: #424242;
  border: 1px solid rgba(66, 66, 66, 0.2);
}

.actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

/* 去除操作按钮单元格下方的横线 */
.users-table td.actions {
  border-bottom: none;
  /* 添加底部内边距补偿缺失的边框，确保高度对齐 */
  padding-bottom: calc(var(--spacing-md) + 1px);
}

.actions .base-button {
  padding: var(--spacing-xs) var(--spacing-md);
  font-size: 14px;
  margin-top: 2px;
}

.actions .base-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

.checkbox-input {
  margin-right: var(--spacing-sm);
}

.form-hint {
  color: var(--text-secondary);
  font-size: 14px;
  margin-left: var(--spacing-sm);
}

.bio-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  resize: vertical;
  font-family: inherit;
  font-size: inherit;
}

.bio-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
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

  .users-table {
    font-size: 14px;
  }

  .users-table th,
  .users-table td {
    padding: var(--spacing-sm);
  }

  .avatar {
    width: 30px;
    height: 30px;
  }
}
</style>
