import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const request = axios.create({
  baseURL,
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data && typeof data === 'object') {
      if (data.success === false) {
        return Promise.reject(new Error(data.message || '请求失败'))
      }
      // 直接返回ApiResponse中的data字段，即实际业务数据
      return data.data
    }
    return data
  },
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || '网络错误'

    // 401 统一处理：清理本地缓存并跳转登录
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      // 避免在服务端渲染环境下报错
      if (typeof window !== 'undefined') {
        window.location.href = '/login'
      }
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }

    return Promise.reject(new Error(message))
  },
)

export default request
