import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export interface UserInfo {
  id: number
  account: string
  username: string
  email: string
  phone?: string
  isStudent?: boolean
  school?: string
  studentNumber?: string
  nickname?: string
  avatar?: string
  bio?: string
  gender?: string
  isAdmin?: boolean // 添加管理员状态字段
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>('')
  const users = ref<UserInfo[]>([]) // 用于管理员查看所有用户

  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

  const userLogin = async (loginData: { account: string; password: string }) => {
    const response = await request.post<{ token: string; user: UserInfo }>('/auth/login', loginData)
    userInfo.value = response.user
    token.value = response.token
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  const userRegister = async (registerData: {
    account: string
    username: string
    password: string
    email: string
    phone?: string
    isStudent?: boolean
    school?: string
    studentNumber?: string
    gender?: string
  }) => {
    const response = await request.post<{ token: string; user: UserInfo }>('/auth/register', registerData)
    userInfo.value = response.user
    token.value = response.token
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  const logout = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const updateUserInfo = async (info: Partial<UserInfo>) => {
    if (!userInfo.value) return
    const updated = await request.put<UserInfo>(`/users/${userInfo.value.id}`, info)
    userInfo.value = updated
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    return updated
  }

  // 获取所有用户（管理员功能）
  const fetchUsers = async () => {
    const data = await request.get<UserInfo[]>('/users')
    users.value = data
  }

  // 添加用户（管理员功能）
  const addUser = async (userData: Omit<UserInfo, 'id'> & { password?: string }) => {
    const response = await request.post<{ token: string; user: UserInfo }>('/auth/register', {
      account: userData.account,
      username: userData.username,
      password: userData.password || '123456',
      email: userData.email,
      phone: userData.phone,
      isStudent: userData.isStudent,
      school: userData.school,
    })
    users.value.push(response.user)
  }

  // 更新用户（管理员功能）
  const updateUser = async (id: number, updateData: Partial<UserInfo>) => {
    const updated = await request.put<UserInfo>(`/users/${id}`, updateData)
    const index = users.value.findIndex((user) => user.id === id)
    if (index !== -1) {
      users.value[index] = updated
    } else {
      users.value.push(updated)
    }
    if (userInfo.value && userInfo.value.id === id) {
      userInfo.value = updated
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  // 重置密码（需要账号+手机号+图形验证码；若后端未校验可忽略验证码字段）
  const resetPassword = async (payload: {
    account: string
    phone: string
    captchaCode: string
    newPassword: string
  }) => {
    await request.post('/auth/reset-password', payload)
  }

  // 删除用户（管理员功能）
  const deleteUser = async (id: number) => {
    if (userInfo.value && userInfo.value.id === id) {
      throw new Error('不能删除当前登录用户')
    }
    await request.delete(`/users/${id}`)
    users.value = users.value.filter((user) => user.id !== id)
  }

  // 初始化时从localStorage恢复
  const init = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedToken && savedUserInfo) {
      token.value = savedToken
      try {
        userInfo.value = JSON.parse(savedUserInfo)
        
        // 只有管理员才需要初始化用户列表
        if (userInfo.value.isAdmin) {
          fetchUsers()
        }
      } catch (e) {
        // Failed to parse userInfo from localStorage
      }
    }
  }

  init()

  return {
    userInfo,
    token,
    users,
    isLoggedIn,
    userLogin,
    userRegister,
    logout,
    updateUserInfo,
    fetchUsers,
    addUser,
    updateUser,
    deleteUser,
    resetPassword,
  }
})
