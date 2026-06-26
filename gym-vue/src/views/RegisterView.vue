<template>
  <div class="auth-view register-view">
    <div class="bg-glow glow-one"></div>
    <div class="bg-glow glow-two"></div>
    <div class="bg-glow glow-three"></div>
    <div class="animated-particles">
      <div class="particle" v-for="i in 20" :key="i"></div>
    </div>
    <div class="auth-container">
      <section class="auth-panel auth-illustration">
        <div class="logo-badge">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="3" width="18" height="18" rx="4" stroke="currentColor" stroke-width="2" />
            <path
              d="M8 16L10.5 9.5L13 16M9 14h3.5M15 10.5C15 9.67157 15.6716 9 16.5 9C17.3284 9 18 9.67157 18 10.5C18 11.3284 17.3284 12 16.5 12C15.6716 12 15 12.6716 15 13.5C15 14.3284 15.6716 15 16.5 15C17.3284 15 18 14.3284 18 13.5"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          JOIN US
        </div>
        <h1>注册账号，解锁校园运动的全部玩法</h1>
        <p>
          统一身份即可畅享各校场馆、社交圈和训练数据。创建账号不到一分钟，你的运动计划立刻上线。
        </p>
        <div class="stats-grid">
          <div>
            <strong>{{ formattedVenueCount }}</strong>
            <span>可预约的校内/校外场馆</span>
          </div>
          <div>
            <strong>{{ formattedUserCount }}</strong>
            <span>同学在这里相约训练</span>
          </div>
          <div>
            <strong>24/7</strong>
            <span>全天候自助预约与提醒</span>
          </div>
        </div>
        <ul class="feature-list">
          <li>
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" />
                <path d="M8 12l3 3 5-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </div>
            <div>
              <strong>个性档案</strong>
              <p>完善个人信息即可获得推荐训练伙伴</p>
            </div>
          </li>
          <li>
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="4" y="4" width="16" height="16" rx="2" stroke="currentColor" stroke-width="2" />
                <path d="M8 9h8M8 13h4M8 17h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              </svg>
            </div>
            <div>
              <strong>赛事积分</strong>
              <p>自动关联成绩、学分与活动记录</p>
            </div>
          </li>
        </ul>
      </section>

      <section class="auth-panel auth-card">
        <div class="card-header">
          <div class="header-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M12 12c2.7614 0 5-2.2386 5-5s-2.2386-5-5-5-5 2.2386-5 5 2.2386 5 5 5z"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M4 21v-1a5 5 0 0 1 5-5h6a5 5 0 0 1 5 5v1"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <h2>创建新账户</h2>
          <p>填写基础信息即可完成注册，如已有账号请直接登录</p>
        </div>

        <BaseForm @submit="handleSubmit">
          <BaseFormItem label="账号" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0zM12 14a7 7 0 0 0-7 7h14a7 7 0 0 0-7-7z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </div>
              <BaseInput
                v-model="formData.account"
                type="text"
                placeholder="请输入学号/工号"
                :error="errors.account"
                class="input-with-icon"
              />
            </div>
          </BaseFormItem>
          <BaseFormItem label="姓名" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M12 12c2.2091 0 4-1.7909 4-4s-1.7909-4-4-4-4 1.7909-4 4 1.7909 4 4 4z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M6 20a6 6 0 0 1 12 0"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </div>
              <BaseInput
                v-model="formData.username"
                type="text"
                placeholder="请输入姓名"
                :error="errors.username"
                class="input-with-icon"
              />
            </div>
          </BaseFormItem>

          <BaseFormItem label="邮箱" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path d="M22 6l-10 7L2 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </div>
              <BaseInput
                v-model="formData.email"
                type="email"
                placeholder="请输入可联系邮箱"
                :error="errors.email"
                class="input-with-icon"
              />
            </div>
          </BaseFormItem>

          <BaseFormItem label="手机号" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect
                    x="5"
                    y="2"
                    width="14"
                    height="20"
                    rx="2"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <circle cx="12" cy="18" r="1" fill="currentColor" />
                </svg>
              </div>
              <BaseInput
                v-model="formData.phone"
                type="tel"
                placeholder="请输入手机号"
                :error="errors.phone"
                class="input-with-icon"
              />
            </div>
          </BaseFormItem>

          <BaseFormItem label="身份" required>
            <div class="radio-group">
              <label class="radio-item">
                <input type="radio" v-model="formData.identityType" value="STUDENT" />
                <span>学生</span>
              </label>
              <label class="radio-item">
                <input type="radio" v-model="formData.identityType" value="STAFF" />
                <span>教职工</span>
              </label>
              <label class="radio-item">
                <input type="radio" v-model="formData.identityType" value="NORMAL" />
                <span>普通用户</span>
              </label>
            </div>
            <div v-if="errors.identityType" class="error-message">
              {{ errors.identityType }}
            </div>
          </BaseFormItem>

          <BaseFormItem v-if="showSchool" label="学校" required>
            <BaseSelect v-model="formData.school" :error="errors.school">
              <option value="">请选择学校</option>
              <option v-for="school in schoolList" :key="school" :value="school">
                {{ school }}
              </option>
            </BaseSelect>
          </BaseFormItem>

          <BaseFormItem v-if="showCampusNumber" :label="campusNumberLabel" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect
                    x="3"
                    y="4"
                    width="18"
                    height="16"
                    rx="2"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path d="M7 4v3M17 4v3M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </div>
              <BaseInput
                v-model="formData.studentNumber"
                :placeholder="campusNumberPlaceholder"
                :error="errors.studentNumber"
                class="input-with-icon"
              />
            </div>
          </BaseFormItem>

          <BaseFormItem label="性别" required>
            <div class="radio-group">
              <label class="radio-item">
                <input type="radio" v-model="formData.gender" value="MALE" />
                <span>男</span>
              </label>
              <label class="radio-item">
                <input type="radio" v-model="formData.gender" value="FEMALE" />
                <span>女</span>
              </label>
              <label class="radio-item">
                <input type="radio" v-model="formData.gender" value="OTHER" />
                <span>其他</span>
              </label>
            </div>
            <div v-if="errors.gender" class="error-message">
              {{ errors.gender }}
            </div>
          </BaseFormItem>

          <BaseFormItem label="密码" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect
                    x="3"
                    y="11"
                    width="18"
                    height="10"
                    rx="2"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M7 11V8a5 5 0 0 1 10 0v3"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </div>
              <BaseInput
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="至少 6 位字符"
                :error="errors.password"
                class="input-with-icon"
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
              >
                <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20C5 20 1 12 1 12a18.45 18.45 0 0 1 5.06-5.94"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M14.12 14.12A3 3 0 0 1 9.88 9.88"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <line
                    x1="1"
                    y1="1"
                    x2="23"
                    y2="23"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M1 12S5 4 12 4s11 8 11 8-4 8-11 8S1 12 1 12z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <circle
                    cx="12"
                    cy="12"
                    r="3"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </button>
            </div>
          </BaseFormItem>
          <BaseFormItem label="确认密码" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M12 21s-7-4-7-10V7l7-4 7 4v4c0 6-7 10-7 10z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path d="M9 11l3 3 3-3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </div>
              <BaseInput
                v-model="formData.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="请再次输入"
                :error="errors.confirmPassword"
                class="input-with-icon"
              />
              <button
                type="button"
                class="password-toggle"
                @click="showConfirmPassword = !showConfirmPassword"
                :aria-label="showConfirmPassword ? '隐藏密码' : '显示密码'"
              >
                <svg v-if="showConfirmPassword" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20C5 20 1 12 1 12a18.45 18.45 0 0 1 5.06-5.94"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M14.12 14.12A3 3 0 0 1 9.88 9.88"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <line
                    x1="1"
                    y1="1"
                    x2="23"
                    y2="23"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M1 12S5 4 12 4s11 8 11 8-4 8-11 8S1 12 1 12z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <circle
                    cx="12"
                    cy="12"
                    r="3"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </button>
            </div>
          </BaseFormItem>

          <div class="register-actions">
            <BaseButton type="primary" htmlType="submit" :loading="loading">
              立即注册
            </BaseButton>
            <div class="register-links">
              <RouterLink to="/login" class="link">已有账号？返回登录</RouterLink>
            </div>
          </div>
        </BaseForm>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useVenueStore } from '@/stores/venue'
import { useSchoolStore } from '@/stores/school'
import { BaseForm, BaseFormItem, BaseInput, BaseButton, BaseSelect } from '@/components/common'

const router = useRouter()
const userStore = useUserStore()
const venueStore = useVenueStore()
const schoolStore = useSchoolStore()

const formData = reactive({
  account: '',
  username: '',
  email: '',
  phone: '',
  identityType: 'STUDENT' as 'STUDENT' | 'STAFF' | 'NORMAL',
  school: '',
  studentNumber: '',
  gender: '' as '' | 'MALE' | 'FEMALE' | 'OTHER',
  password: '',
  confirmPassword: '',
})

const errors = reactive({
  account: '',
  username: '',
  email: '',
  phone: '',
  identityType: '',
  school: '',
  studentNumber: '',
  gender: '',
  password: '',
  confirmPassword: '',
})

const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const schoolList = computed(() => schoolStore.schools)

const isStudent = computed(() => formData.identityType === 'STUDENT')
const isStaff = computed(() => formData.identityType === 'STAFF')
const isNormal = computed(() => formData.identityType === 'NORMAL')

const showSchool = computed(() => isStudent.value || isStaff.value)
const showCampusNumber = computed(() => isStudent.value || isStaff.value)

const campusNumberLabel = computed(() => (isStudent.value ? '学生号' : '教职工号'))
const campusNumberPlaceholder = computed(() =>
  isStudent.value ? '请输入学生号' : '请输入教职工号'
)

// 格式化数量显示
const formatCount = (count: number): string => {
  if (count >= 1000) {
    const k = Math.floor(count / 1000)
    const remainder = count % 1000
    if (remainder >= 100) {
      return `${k}.${Math.floor(remainder / 100)}k+`
    }
    return `${k}k+`
  }
  return count > 0 ? `${count}+` : '0'
}

// 场馆数量
const venueCount = computed(() => venueStore.venues.length)
const formattedVenueCount = computed(() => formatCount(venueCount.value))

// 用户数量（排除管理员）
const userCount = computed(() => {
  return userStore.users.filter((user) => !user.isAdmin).length
})
const formattedUserCount = computed(() => formatCount(userCount.value))

onMounted(async () => {
  try {
    await schoolStore.fetchEnabledNames()
    await venueStore.fetchVenues()
    await userStore.fetchUsers()
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})

const validateAccount = (account: string) => {
  // 验证账号是否为纯数字
  const re = /^\d+$/
  return re.test(account)
}

const validateEmail = (email: string) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

const validatePhone = (phone: string) => {
  const re = /^1[3-9]\d{9}$/
  return re.test(phone)
}

const validateStudentNumber = (value: string) => {
  // 允许纯数字，至少4位
  return /^\d{4,}$/.test(value)
}

const validate = () => {
  let isValid = true
  errors.account = ''
  errors.username = ''
  errors.email = ''
  errors.phone = ''
  errors.identityType = ''
  errors.school = ''
  errors.studentNumber = ''
  errors.gender = ''
  errors.password = ''
  errors.confirmPassword = ''

  if (!formData.account.trim()) {
    errors.account = '请输入账号'
    isValid = false
  } else if (!validateAccount(formData.account)) {
    errors.account = '账号必须为纯数字'
    isValid = false
  }

  if (!formData.username.trim()) {
    errors.username = '请输入姓名'
    isValid = false
  } else if (formData.username.length < 2) {
    errors.username = '姓名长度不能少于2位'
    isValid = false
  }

  if (!formData.email.trim()) {
    errors.email = '请输入邮箱'
    isValid = false
  } else if (!validateEmail(formData.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }

  if (!formData.phone.trim()) {
    errors.phone = '请输入手机号'
    isValid = false
  } else if (!validatePhone(formData.phone)) {
    errors.phone = '请输入有效的手机号'
    isValid = false
  }

  if (!formData.identityType) {
    errors.identityType = '请选择身份'
    isValid = false
  }

  if (showSchool.value && !formData.school.trim()) {
    errors.school = '请选择学校'
    isValid = false
  }

  if (showCampusNumber.value) {
    if (!formData.studentNumber.trim()) {
      errors.studentNumber = isStudent.value ? '请输入学生号' : '请输入教职工号'
      isValid = false
    } else if (!validateStudentNumber(formData.studentNumber)) {
      errors.studentNumber = isStudent.value ? '学生号需为至少4位数字' : '教职工号需为至少4位数字'
      isValid = false
    }
  }

  if (!formData.gender) {
    errors.gender = '请选择性别'
    isValid = false
  }

  if (!formData.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (formData.password.length < 6) {
    errors.password = '密码长度不能少于6位'
    isValid = false
  }

  if (!formData.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    isValid = false
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }

  return isValid
}

const handleSubmit = async () => {
  if (!validate()) {
    return
  }

  loading.value = true
  try {
    await userStore.userRegister({
      account: formData.account,
      username: formData.username,
      email: formData.email,
      phone: formData.phone,
      isStudent: isStudent.value,
      school: showSchool.value ? formData.school : '',
      studentNumber: showCampusNumber.value ? formData.studentNumber : undefined,
      gender: formData.gender,
      password: formData.password,
    })
    router.push('/')
  } catch (error: any) {
    alert(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-view {
  position: relative;
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(24px, 6vw, 80px);
  background: radial-gradient(circle at top, #e5f0ff 0%, #fdfaff 60%, #f1f6ff 100%);
  overflow: hidden;
}

.auth-view.register-view::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 10% 20%, rgba(255, 193, 248, 0.35), transparent 55%),
    radial-gradient(circle at 80% 80%, rgba(157, 212, 255, 0.35), transparent 55%);
  pointer-events: none;
  animation: gradientShift 16s ease-in-out infinite;
}

@keyframes gradientShift {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.85;
  }
}

.bg-glow {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.4;
  z-index: 0;
  animation: float 22s ease-in-out infinite;
}

.glow-one {
  top: -60px;
  right: 10%;
  background: #ffc1f8;
  animation-delay: 0s;
}

.glow-two {
  bottom: -80px;
  left: 5%;
  background: #9dd4ff;
  animation-delay: -7s;
}

.glow-three {
  top: 45%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 260px;
  height: 260px;
  background: #f6d365;
  opacity: 0.3;
  animation-delay: -14s;
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(18px, -18px) scale(1.05);
  }
  66% {
    transform: translate(-18px, 18px) scale(0.95);
  }
}

.animated-particles {
  position: absolute;
  inset: 0;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particleFloat 18s linear infinite;
}

.particle:nth-child(odd) {
  animation-duration: 22s;
}

.particle:nth-child(even) {
  animation-duration: 26s;
  background: rgba(255, 255, 255, 0.4);
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) translateX(0) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-80px) translateX(100px) scale(1);
    opacity: 0;
  }
}

.auth-container {
  position: relative;
  z-index: 1;
  width: min(1100px, 100%);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--spacing-2xl);
  align-items: stretch;
}

.auth-panel {
  border-radius: 26px;
  padding: clamp(24px, 4vw, 48px);
  box-shadow: 0 40px 80px rgba(20, 25, 53, 0.12);
  backdrop-filter: blur(12px);
}

.auth-illustration {
  background: linear-gradient(135deg, #fce7f3 0%, #fbcfe8 50%, #f9a8d4 100%);
  color: #1f2937;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  position: relative;
  overflow: hidden;
}

.auth-illustration::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.3), transparent 60%);
  pointer-events: none;
}

.logo-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 999px;
  padding: 8px 20px;
  font-size: 13px;
  letter-spacing: 0.2em;
  color: #ec4899;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(236, 72, 153, 0.2);
  transition: all 0.3s ease;
  font-weight: 600;
}

.logo-badge:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
  box-shadow: 0 10px 26px rgba(236, 72, 153, 0.25);
  border-color: rgba(236, 72, 153, 0.3);
}

.logo-icon {
  width: 18px;
  height: 18px;
  animation: logoPulse 2.6s ease-in-out infinite;
}

@keyframes logoPulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
  }
}

.auth-illustration h1 {
  font-size: clamp(28px, 3vw, 40px);
  margin: 0;
  line-height: 1.2;
}

.auth-illustration p {
  margin: 0;
  opacity: 0.85;
  line-height: 1.7;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
}

.stats-grid div {
  background: rgba(255, 255, 255, 0.85);
  padding: 14px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 10px 24px rgba(236, 72, 153, 0.15);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  color: #1f2937;
}

.stats-grid div:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 16px 32px rgba(236, 72, 153, 0.25);
}

.stats-grid strong {
  display: block;
  font-size: 26px;
  margin-bottom: 4px;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.feature-list li {
  display: flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.75);
  border-radius: 18px;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  color: #1f2937;
}

.feature-list li:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateX(4px);
  box-shadow: 0 8px 18px rgba(236, 72, 153, 0.2);
  border-color: rgba(236, 72, 153, 0.3);
}

.feature-icon {
  width: 36px;
  height: 36px;
  min-width: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f472b6 0%, #ec4899 100%);
  border-radius: 12px;
  font-size: 18px;
  transition: all 0.3s ease;
  color: white;
  box-shadow: 0 4px 12px rgba(236, 72, 153, 0.3);
}

.feature-icon svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
}

.feature-list li:hover .feature-icon {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 16px rgba(236, 72, 153, 0.4);
}

.auth-card {
  background: #fff;
  border: 1px solid rgba(38, 43, 72, 0.08);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  color: var(--text-primary);
  animation: cardFadeIn 0.6s ease-out;
}

.card-header {
  text-align: center;
  position: relative;
  padding-bottom: 8px;
}

.header-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(253, 160, 133, 0.55);
  transition: all 0.3s ease;
}

.header-icon svg {
  width: 26px;
  height: 26px;
  stroke: #fff;
}

.header-icon:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 30px rgba(253, 160, 133, 0.7);
}

.card-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
  color: var(--text-primary);
  font-weight: 700;
  background: linear-gradient(135deg, #4d2c91 0%, #f6d365 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header p {
  margin: 0;
  color: var(--text-secondary);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--spacing-md);
}

.register-actions {
  margin-top: var(--spacing-xl);
}

.register-actions .base-button {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(77, 44, 145, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-actions .base-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(77, 44, 145, 0.55);
}

.register-actions .base-button:active {
  transform: translateY(0);
}

.register-links {
  margin-top: var(--spacing-md);
  text-align: center;
}

.link {
  color: var(--primary-color);
  text-decoration: none;
  font-size: 14px;
}

.link:hover {
  text-decoration: underline;
}

.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.radio-item {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 999px;
  background: var(--bg-secondary);
  transition: all 0.25s ease;
  border: 1px solid transparent;
}

.radio-item input {
  accent-color: var(--primary-color);
}

.radio-item:hover {
  border-color: var(--primary-color);
  background: rgba(77, 44, 145, 0.05);
}

.error-message {
  color: var(--danger-color);
  font-size: 12px;
  margin-top: var(--spacing-xs);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  z-index: 1;
  width: 20px;
  height: 20px;
  color: var(--text-secondary);
  pointer-events: none;
  transition: color 0.3s ease;
}

.input-icon svg {
  width: 100%;
  height: 100%;
  stroke: currentColor;
}

.input-wrapper :deep(.base-input) {
  padding-left: 44px;
}

.input-wrapper:focus-within .input-icon {
  color: var(--primary-color);
}

.password-toggle {
  position: absolute;
  right: 12px;
  z-index: 1;
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: var(--primary-color);
  background: rgba(77, 44, 145, 0.08);
}

.password-toggle:active {
  transform: scale(0.95);
}

.password-toggle svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
}

.input-wrapper :deep(.base-input:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(77, 44, 145, 0.15);
}

@media (max-width: 960px) {
  .auth-container {
    grid-template-columns: minmax(0, 1fr);
  }

  .auth-illustration {
    min-height: 340px;
  }

  .animated-particles {
    display: none;
  }
}

/* 卡片进入动画 */
@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
