/**
 * 表单验证工具函数
 */

/**
 * 用户名验证
 */
export function validateUsername(username: string): string {
  if (!username || !username.trim()) {
    return '请输入用户名'
  }
  if (username.length < 3) {
    return '用户名长度不能少于3位'
  }
  if (username.length > 20) {
    return '用户名长度不能超过20位'
  }
  if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(username)) {
    return '用户名只能包含字母、数字、下划线和中文'
  }
  return ''
}

/**
 * 密码验证
 */
export function validatePassword(password: string): string {
  if (!password) {
    return '请输入密码'
  }
  if (password.length < 6) {
    return '密码长度不能少于6位'
  }
  if (password.length > 20) {
    return '密码长度不能超过20位'
  }
  return ''
}

/**
 * 确认密码验证
 */
export function validateConfirmPassword(
  password: string,
  confirmPassword: string
): string {
  if (!confirmPassword) {
    return '请确认密码'
  }
  if (password !== confirmPassword) {
    return '两次输入的密码不一致'
  }
  return ''
}

/**
 * 昵称验证
 */
export function validateNickname(nickname: string): string {
  if (nickname && nickname.length > 20) {
    return '昵称长度不能超过20位'
  }
  return ''
}

/**
 * 邮箱验证
 */
export function validateEmail(email: string): string {
  if (!email || !email.trim()) {
    return '请输入邮箱'
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) {
    return '请输入有效的邮箱地址'
  }
  return ''
}

/**
 * 手机号验证
 */
export function validatePhone(phone: string): string {
  if (!phone || !phone.trim()) {
    return '请输入手机号'
  }
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(phone)) {
    return '请输入有效的手机号'
  }
  return ''
}

/**
 * 登录表单验证
 */
export interface LoginFormData {
  username: string
  password: string
}

export function validateLoginForm(data: LoginFormData): Record<string, string> {
  const errors: Record<string, string> = {}
  
  const usernameError = validateUsername(data.username)
  if (usernameError) {
    errors.username = usernameError
  }
  
  const passwordError = validatePassword(data.password)
  if (passwordError) {
    errors.password = passwordError
  }
  
  return errors
}

/**
 * 注册表单验证
 */
export interface RegisterFormData {
  username: string
  email: string
  password: string
  confirmPassword: string
}

export function validateRegisterForm(
  data: RegisterFormData
): Record<string, string> {
  const errors: Record<string, string> = {}
  
  const usernameError = validateUsername(data.username)
  if (usernameError) {
    errors.username = usernameError
  }
  
  const emailError = validateEmail(data.email)
  if (emailError) {
    errors.email = emailError
  }
  
  const passwordError = validatePassword(data.password)
  if (passwordError) {
    errors.password = passwordError
  }
  
  const confirmPasswordError = validateConfirmPassword(
    data.password,
    data.confirmPassword
  )
  if (confirmPasswordError) {
    errors.confirmPassword = confirmPasswordError
  }
  
  return errors
}

