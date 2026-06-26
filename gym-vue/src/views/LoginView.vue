<template>
  <div class="auth-view login-view">
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
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
          </svg>
          GYM CAMPUS
        </div>
        <h1>欢迎回来，开启你的校园运动计划</h1>
        <p>
          一键预约·好友互动·数据洞察。统一入口即可连接校园所有体育资源，学生与管理员都能高效协作。
        </p>
        <ul class="feature-list">
          <li>
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div>
              <strong>智能预约</strong>
              <p>实时同步空闲场地与订单状态</p>
            </div>
          </li>
          <li>
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div>
              <strong>社交互动</strong>
              <p>好友消息、动态、话题一站式呈现</p>
            </div>
          </li>
          <li>
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 3v18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18 7L12 13L8 9L3 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div>
              <strong>后台洞察</strong>
              <p>管理员掌握场馆利用率与收入趋势</p>
            </div>
          </li>
        </ul>
        <div class="support-hint">
          没有账号？<RouterLink to="/register">立即注册</RouterLink> 或联系管理员开通后台
        </div>
      </section>

      <section class="auth-panel auth-card">
        <div class="card-header">
          <div class="header-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h2>登录账户</h2>
          <p>使用学号/工号登录，管理员请选择管理员模式</p>
        </div>

        <BaseForm @submit="handleSubmit">
          <BaseFormItem label="账号" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0zM12 14a7 7 0 0 0-7 7h14a7 7 0 0 0-7-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <BaseInput
                v-model="formData.account"
                type="text"
                placeholder="请输入账号"
                :error="errors.account"
                class="input-with-icon"
              />
            </div>
          </BaseFormItem>
          <BaseFormItem label="密码" required>
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <BaseInput
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
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
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </BaseFormItem>

          <div class="login-type-toggle">
            <button
              type="button"
              :class="['toggle-pill', { active: isUserLogin }]"
              @click="isUserLogin = true"
            >
              <span>普通用户</span>
            </button>
            <button
              type="button"
              :class="['toggle-pill', { active: !isUserLogin }]"
              @click="isUserLogin = false"
            >
              <span>管理员</span>
            </button>
          </div>

          <div class="login-actions">
            <BaseButton type="primary" htmlType="submit" :loading="loading">
              {{ isUserLogin ? '登录并开始锻炼' : '进入管理后台' }}
            </BaseButton>
            <div class="login-links">
              <RouterLink to="/register" class="link" v-if="isUserLogin">
                还没有账号？立即注册
              </RouterLink>
              <button type="button" class="link link-button" @click="showForgotModal = true">
                忘记密码？
              </button>
            </div>
          </div>
        </BaseForm>
      </section>
    </div>

    <!-- 忘记密码弹窗 -->
    <Transition name="modal">
      <div v-if="showForgotModal" class="modal-overlay" @click.self="closeForgotModal">
        <div class="modal-card" @click.stop>
          <div class="modal-header">
            <h3>
              <svg class="modal-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              重置密码
            </h3>
            <button class="modal-close" @click="closeForgotModal" aria-label="关闭">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        <div class="modal-body">
          <div class="form-row">
            <label>账号</label>
            <BaseInput
              v-model="forgotForm.account"
              placeholder="请输入账号"
              :error="forgotErrors.account"
            />
          </div>
          <div class="form-row">
            <label>手机号</label>
            <BaseInput
              v-model="forgotForm.phone"
              placeholder="请输入注册时的手机号"
              :error="forgotErrors.phone"
            />
          </div>
          <div class="form-row code-row">
            <div class="code-field">
              <label>验证码</label>
              <BaseInput
                v-model="forgotForm.captchaCode"
                placeholder="输入图片中的字母数字"
                :error="forgotErrors.captchaCode"
              />
            </div>
            <div class="captcha-box">
              <div v-if="captchaImage" class="captcha-image" @click="fetchCaptcha">
                <img :src="captchaImage" alt="验证码" />
              </div>
              <BaseButton v-else type="secondary" size="small" class="code-button" :loading="codeLoading" @click="fetchCaptcha">
                获取验证码
              </BaseButton>
              <div class="captcha-hint">看不清？点击图片刷新</div>
            </div>
          </div>
          <div class="form-row">
            <label>新密码</label>
            <BaseInput
              v-model="forgotForm.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位）"
              :error="forgotErrors.newPassword"
            />
          </div>
        </div>
        <div class="modal-footer">
          <BaseButton type="secondary" @click="closeForgotModal">取消</BaseButton>
          <BaseButton type="primary" :loading="forgotLoading" @click="handleForgotSubmit">
            提交
          </BaseButton>
        </div>
      </div>
    </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { BaseForm, BaseFormItem, BaseInput, BaseButton } from '@/components/common'

const router = useRouter()
const userStore = useUserStore()

const formData = reactive({
  account: '',
  password: '',
})

const errors = reactive({
  account: '',
  password: '',
})

const loading = ref(false)
// 添加登录类型切换状态
const isUserLogin = ref(true)
// 密码显示/隐藏状态
const showPassword = ref(false)

// 忘记密码弹窗状态
const showForgotModal = ref(false)
const forgotLoading = ref(false)
const codeLoading = ref(false)
const captchaImage = ref('')
const captchaText = ref('')
const forgotForm = reactive({
  account: '',
  phone: '',
  captchaCode: '',
  newPassword: '',
})
const forgotErrors = reactive({
  account: '',
  phone: '',
  captchaCode: '',
  newPassword: '',
})

const validateAccount = (account: string) => {
  // 验证账号是否为纯数字
  const re = /^\d+$/
  return re.test(account)
}

const validate = () => {
  let isValid = true
  errors.account = ''
  errors.password = ''

  if (!formData.account.trim()) {
    errors.account = '请输入账号'
    isValid = false
  } else if (!validateAccount(formData.account)) {
    errors.account = '账号必须为纯数字'
    isValid = false
  }

  if (!formData.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (formData.password.length < 6) {
    errors.password = '密码长度不能少于6位'
    isValid = false
  }

  return isValid
}

const validateForgot = () => {
  let ok = true
  forgotErrors.account = ''
  forgotErrors.phone = ''
  forgotErrors.captchaCode = ''
  forgotErrors.newPassword = ''

  // 归一化手机号（移除空格和符号）
  const normalizedPhone = forgotForm.phone.replace(/\D/g, '')

  if (!forgotForm.account.trim()) {
    forgotErrors.account = '请输入账号'
    ok = false
  }
  if (!forgotForm.phone.trim()) {
    forgotErrors.phone = '请输入手机号'
    ok = false
  } else if (normalizedPhone.length !== 11 || !/^1[3-9]/.test(normalizedPhone)) {
    forgotErrors.phone = '请输入有效手机号'
    ok = false
  }
  if (!forgotForm.captchaCode.trim()) {
    forgotErrors.captchaCode = '请输入验证码'
    ok = false
  } else if (forgotForm.captchaCode.trim().toLowerCase() !== captchaText.value.toLowerCase()) {
    forgotErrors.captchaCode = '验证码错误'
    ok = false
    generateCaptcha()
  }
  if (!forgotForm.newPassword || forgotForm.newPassword.length < 6) {
    forgotErrors.newPassword = '新密码至少6位'
    ok = false
  }
  return ok
}

const generateCaptcha = () => {
  const canvas = document.createElement('canvas')
  canvas.width = 120
  canvas.height = 40
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  ctx.fillStyle = '#f2f3f7'
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let text = ''
  for (let i = 0; i < 4; i++) {
    text += chars[Math.floor(Math.random() * chars.length)]
  }
  captchaText.value = text
  ctx.font = '22px sans-serif'
  ctx.fillStyle = '#333'
  ctx.textBaseline = 'middle'
  for (let i = 0; i < text.length; i++) {
    const x = 18 + i * 24
    const y = canvas.height / 2
    const rotate = (Math.random() - 0.5) * 0.4
    ctx.save()
    ctx.translate(x, y)
    ctx.rotate(rotate)
    ctx.fillText(text[i], 0, 0)
    ctx.restore()
  }
  for (let i = 0; i < 4; i++) {
    ctx.strokeStyle = `rgba(64,158,255,${0.3 + Math.random() * 0.3})`
    ctx.beginPath()
    ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height)
    ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height)
    ctx.stroke()
  }
  captchaImage.value = canvas.toDataURL('image/png')
}

const fetchCaptcha = () => {
  codeLoading.value = true
  generateCaptcha()
  codeLoading.value = false
}

const handleForgotSubmit = async () => {
  if (!validateForgot()) return
  forgotLoading.value = true
  try {
    const normalizedPhone = forgotForm.phone.replace(/\D/g, '')
    await userStore.resetPassword({
      account: forgotForm.account,
      phone: normalizedPhone,
      captchaCode: forgotForm.captchaCode,
      newPassword: forgotForm.newPassword,
    })
    alert('密码重置成功，请使用新密码登录')
    closeForgotModal()
  } catch (error: any) {
    alert(error.message || '重置失败，请稍后再试')
  } finally {
    forgotLoading.value = false
  }
}

const closeForgotModal = () => {
  showForgotModal.value = false
  forgotForm.account = ''
  forgotForm.phone = ''
  forgotForm.captchaCode = ''
  forgotForm.newPassword = ''
  forgotErrors.account = ''
  forgotErrors.phone = ''
  forgotErrors.captchaCode = ''
  forgotErrors.newPassword = ''
  captchaImage.value = ''
  captchaText.value = ''
}

// 初始生成验证码
fetchCaptcha()

const handleSubmit = async () => {
  if (!validate()) {
    return
  }

  loading.value = true
  try {
    await userStore.userLogin({
      account: formData.account,
      password: formData.password,
    })

    // 管理员账号只能通过管理员登录方式登录
    if (isUserLogin.value && userStore.userInfo?.isAdmin) {
      alert('管理员账号请选择管理员登录方式')
      userStore.logout()
      return
    }

    // 非管理员账号不能通过管理员登录方式登录
    if (!isUserLogin.value && !userStore.userInfo?.isAdmin) {
      alert('该账号没有管理员权限')
      userStore.logout()
      return
    }

    if (isUserLogin.value) {
      router.push('/')
    } else {
      router.push('/admin')
    }
  } catch (error: any) {
    alert(error.message || '登录失败')
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
  background: radial-gradient(circle at top, #d6e4ff 0%, #f8f9ff 55%, #eef2ff 100%);
  overflow: hidden;
}

.auth-view.login-view {
  color: #fff;
  background: radial-gradient(circle at 15% 20%, #131b3a 0%, #0c1733 35%, #050b1f 75%);
}

.auth-view.login-view::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 70% 30%, rgba(96, 183, 255, 0.25), transparent 55%),
    radial-gradient(circle at 20% 80%, rgba(139, 92, 246, 0.3), transparent 50%);
  pointer-events: none;
  animation: gradientShift 15s ease-in-out infinite;
}

@keyframes gradientShift {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

.bg-glow {
  position: absolute;
  width: 380px;
  height: 380px;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.45;
  z-index: 0;
  animation: float 20s ease-in-out infinite;
}

.glow-one {
  top: -80px;
  left: -60px;
  background: #9aa9ff;
  animation-delay: 0s;
}

.glow-two {
  bottom: -60px;
  right: 0;
  background: #90e0ff;
  animation-delay: -7s;
}

.glow-three {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #b794f6;
  width: 280px;
  height: 280px;
  opacity: 0.3;
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(20px, -20px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
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
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: particleFloat 15s linear infinite;
}

.particle:nth-child(odd) {
  animation-duration: 20s;
}

.particle:nth-child(even) {
  animation-duration: 25s;
  background: rgba(150, 200, 255, 0.4);
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
    transform: translateY(-100px) translateX(100px) scale(1);
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
  box-shadow: 0 40px 80px rgba(20, 25, 53, 0.15);
  backdrop-filter: blur(12px);
}

.auth-illustration {
  background: linear-gradient(135deg, #121c52 0%, #2d4d96 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.logo-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 999px;
  padding: 8px 18px;
  font-size: 13px;
  letter-spacing: 0.2em;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.logo-badge:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.logo-icon {
  width: 18px;
  height: 18px;
  animation: logoRotate 3s linear infinite;
}

@keyframes logoRotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.auth-illustration h1 {
  font-size: clamp(28px, 3vw, 40px);
  line-height: 1.2;
  margin: 0;
}

.auth-illustration p {
  opacity: 0.8;
  margin: 0;
  line-height: 1.7;
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
  background: rgba(255, 255, 255, 0.07);
  border-radius: 18px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.feature-list li:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateX(4px);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 36px;
  height: 36px;
  min-width: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 18px;
  transition: all 0.3s ease;
}

.feature-icon svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
}

.feature-list li:hover .feature-icon {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1) rotate(5deg);
}

.support-hint {
  margin-top: auto;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

.support-hint a {
  color: #fff;
  text-decoration: underline;
}

.link-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: var(--primary-color);
  font: inherit;
}

.link-button:hover {
  text-decoration: underline;
}

.auth-card {
  background: #fff;
  border: 1px solid rgba(38, 43, 72, 0.08);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.auth-card {
  background: rgba(255, 255, 255, 0.96);
  color: var(--text-primary);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.header-icon:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.header-icon svg {
  width: 28px;
  height: 28px;
  stroke: white;
}

.card-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
  color: var(--text-primary);
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.login-type-toggle {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin: var(--spacing-lg) 0;
}

.toggle-pill {
  border: 1px solid var(--border-light);
  border-radius: 999px;
  padding: 12px 0;
  background: transparent;
  color: var(--text-secondary);
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.toggle-pill::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, #5f7bff, #89b3ff);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.toggle-pill span {
  position: relative;
  z-index: 1;
  display: block;
}

.toggle-pill:hover {
  border-color: #5f7bff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(95, 123, 255, 0.2);
}

.toggle-pill.active {
  background: linear-gradient(120deg, #5f7bff, #89b3ff);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 10px 25px rgba(95, 123, 255, 0.35);
  transform: translateY(-2px);
}

.toggle-pill.active span {
  color: #fff;
}

.toggle-pill.active::before {
  opacity: 1;
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
  background: rgba(102, 126, 234, 0.1);
}

.password-toggle:active {
  transform: scale(0.95);
}

.password-toggle svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
}

.login-actions {
  margin-top: var(--spacing-xl);
}

.login-actions .base-button {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login-actions .base-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-actions .base-button:active {
  transform: translateY(0);
}

.login-links {
  margin-top: var(--spacing-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
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

.link-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: var(--primary-color);
  font: inherit;
}

.link-button:hover {
  text-decoration: underline;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3000;
  padding: 16px;
  backdrop-filter: blur(4px);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-active .modal-card,
.modal-leave-active .modal-card {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-card,
.modal-leave-to .modal-card {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}

.modal-card {
  width: min(480px, 100%);
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.06);
  animation: modalSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes modalSlideIn {
  from {
    transform: scale(0.9) translateY(20px);
    opacity: 0;
  }
  to {
    transform: scale(1) translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid var(--border-lighter);
  background: linear-gradient(135deg, #f7f9ff, #eef3ff);
}

.modal-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-icon {
  width: 20px;
  height: 20px;
  stroke: var(--primary-color);
}

.modal-close {
  border: none;
  background: transparent;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--text-primary);
  transform: rotate(90deg);
}

.modal-close svg {
  width: 18px;
  height: 18px;
  stroke: currentColor;
  stroke-width: 2;
}

.modal-body {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #fafbff;
}

.form-row label {
  display: block;
  margin-bottom: 8px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 600;
}

.code-row {
  display: flex;
  gap: 14px;
  align-items: flex-end;
}

.code-field {
  flex: 1;
}

.code-button {
  min-width: 120px;
}

.captcha-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef3ff, #fdfdff);
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-hint {
  font-size: 12px;
  color: var(--text-secondary);
  padding-left: 4px;
}

.modal-footer {
  padding: 16px 22px;
  border-top: 1px solid var(--border-lighter);
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  background: #f7f9ff;
}

/* 为粒子添加随机位置 */
.particle:nth-child(1) { left: 10%; animation-delay: 0s; }
.particle:nth-child(2) { left: 20%; animation-delay: 1s; }
.particle:nth-child(3) { left: 30%; animation-delay: 2s; }
.particle:nth-child(4) { left: 40%; animation-delay: 3s; }
.particle:nth-child(5) { left: 50%; animation-delay: 4s; }
.particle:nth-child(6) { left: 60%; animation-delay: 5s; }
.particle:nth-child(7) { left: 70%; animation-delay: 6s; }
.particle:nth-child(8) { left: 80%; animation-delay: 7s; }
.particle:nth-child(9) { left: 90%; animation-delay: 8s; }
.particle:nth-child(10) { left: 15%; animation-delay: 9s; }
.particle:nth-child(11) { left: 25%; animation-delay: 10s; }
.particle:nth-child(12) { left: 35%; animation-delay: 11s; }
.particle:nth-child(13) { left: 45%; animation-delay: 12s; }
.particle:nth-child(14) { left: 55%; animation-delay: 13s; }
.particle:nth-child(15) { left: 65%; animation-delay: 14s; }
.particle:nth-child(16) { left: 75%; animation-delay: 15s; }
.particle:nth-child(17) { left: 85%; animation-delay: 16s; }
.particle:nth-child(18) { left: 95%; animation-delay: 17s; }
.particle:nth-child(19) { left: 5%; animation-delay: 18s; }
.particle:nth-child(20) { left: 12%; animation-delay: 19s; }

@media (max-width: 960px) {
  .auth-container {
    grid-template-columns: minmax(0, 1fr);
  }

  .auth-illustration {
    min-height: 320px;
  }

  .animated-particles {
    display: none;
  }
}

/* 添加输入框焦点动画 */
.input-wrapper :deep(.base-input:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 添加卡片进入动画 */
.auth-card {
  animation: cardFadeIn 0.6s ease-out;
}

.auth-illustration {
  animation: cardFadeIn 0.6s ease-out 0.1s backwards;
}

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
