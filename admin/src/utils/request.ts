import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { clearAdminSession, getAdminToken } from './auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8081/api',
  timeout: 15000
})

let redirectingToLogin = false

const handleUnauthorized = (message = '登录状态已失效，请重新登录') => {
  clearAdminSession()
  ElMessage.error(message)

  if (redirectingToLogin) {
    return
  }

  redirectingToLogin = true
  const currentRoute = router.currentRoute.value
  const redirect = currentRoute.meta.public ? undefined : currentRoute.fullPath

  if (currentRoute.path === '/login') {
    redirectingToLogin = false
    return
  }

  router
    .replace({
      path: '/login',
      query: redirect ? { redirect } : undefined
    })
    .finally(() => {
      redirectingToLogin = false
    })
}

http.interceptors.request.use(
  (config) => {
    const token = getAdminToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    if (res.code === 401) {
      handleUnauthorized(res.message)
      return Promise.reject(new Error(res.message || '未授权访问'))
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized()
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

const request = {
  get<T>(url: string, config?: AxiosRequestConfig) {
    return http.get<any, T>(url, config)
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return http.post<any, T>(url, data, config)
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return http.put<any, T>(url, data, config)
  },
  delete<T>(url: string, config?: AxiosRequestConfig) {
    return http.delete<any, T>(url, config)
  }
}

export default request
