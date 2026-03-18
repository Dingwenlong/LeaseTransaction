import request from '../utils/request'
import type { AdminSessionUser } from '../utils/auth'

export interface User {
  id: number
  username: string
  nickname: string
  avatar?: string
  studentId?: string
  department?: string
  campus?: string
  creditScore: number
  isVerified: number
  status: number
  createdAt: string
  updatedAt: string
}

export interface Item {
  id: number
  title: string
  description?: string
  category?: string
  type: number
  typeText: string
  price: number
  deposit?: number
  campus?: string
  status: number
  statusText: string
  ownerId: number
  ownerName: string
  ownerVerified: number
  viewCount: number
  favoriteCount: number
  images: string[]
  coverImage: string
  createdAt: string
  updatedAt: string
  reviewHint: string
}

export interface Order {
  id: number
  orderNo: string
  itemId: number
  itemTitle: string
  itemImage: string
  buyerId: number
  buyerName: string
  sellerId: number
  sellerName: string
  type: number
  typeText: string
  status: number
  statusText: string
  amount: number
  deposit?: number
  totalAmount: number
  rentalPrice?: number
  rentalDays?: number
  startDate?: string
  endDate?: string
  deliveryMethod?: string
  remark?: string
  createdAt: string
  updatedAt: string
}

export interface Metric {
  label: string
  value: string
  delta: string
  tone: string
}

export interface DashboardData {
  hero: {
    title: string
    subtitle: string
    updatedAt: string
  }
  metrics: Metric[]
  campusDistribution: Array<{ name: string; value: number }>
  categoryRanking: Array<{ name: string; value: number }>
  orderStatusDistribution: Array<{ name: string; value: number }>
  watchList: Array<{ title: string; value: number; text: string }>
}

export interface SystemConfig {
  banners: Array<{ title: string; subtitle: string; active: boolean }>
  announcements: Array<{ title: string; content: string }>
  categories: string[]
  campuses: string[]
  riskRules: Array<{ name: string; enabled: boolean }>
}

export interface AdminLoginResponse {
  token: string
  userInfo: AdminSessionUser
}

export interface SystemUser {
  id: number
  username: string
  displayName: string
  role: string
  status: number
  lastLoginTime?: string
  createdAt?: string
  updatedAt?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export const userApi = {
  login: (data: { username: string; password: string }) =>
    request.post('/user/login', data),

  getList: (params?: Record<string, unknown>) =>
    request.get<PageResult<User>>('/user/list', { params }),

  getInfo: () =>
    request.get<User>('/user/info'),

  verify: (data: { studentId: string; department: string; campus: string }) =>
    request.post('/user/verify', data),

  updateStatus: (id: number, status: number) =>
    request.post(`/user/status/${id}`, { status })
}

export const adminAuthApi = {
  login: (data: { username: string; password: string }) =>
    request.post<AdminLoginResponse>('/admin/auth/login', data),

  getMe: () =>
    request.get<AdminSessionUser>('/admin/auth/me')
}

export const systemUserApi = {
  getList: (params?: Record<string, unknown>) =>
    request.get<PageResult<SystemUser>>('/admin/system-users/list', { params }),

  create: (data: { username: string; displayName: string; password: string; role: string }) =>
    request.post<SystemUser>('/admin/system-users', data),

  update: (id: number, data: { displayName?: string; role?: string; status?: number }) =>
    request.put<SystemUser>(`/admin/system-users/${id}`, data),

  updateStatus: (id: number, status: number) =>
    request.post<void>(`/admin/system-users/${id}/status`, { status }),

  resetPassword: (id: number, password: string) =>
    request.post<void>(`/admin/system-users/${id}/reset-password`, { password })
}

export const itemApi = {
  getList: (params?: Record<string, unknown>) =>
    request.get<PageResult<Item>>('/item/list', { params }),

  getDetail: (id: number) =>
    request.get<Item>(`/item/detail/${id}`),

  getNearby: (params?: Record<string, unknown>) =>
    request.get<Item[]>('/item/nearby', { params }),

  approve: (id: number) =>
    request.post(`/item/approve/${id}`),

  reject: (id: number, reason = '') =>
    request.post(`/item/reject/${id}`, { reason })
}

export const orderApi = {
  getList: (params?: Record<string, unknown>) =>
    request.get<PageResult<Order>>('/order/list', { params }),

  getDetail: (id: number) =>
    request.get<Order>(`/order/detail/${id}`),

  updateStatus: (id: number, status: number, remark = '') =>
    request.post(`/order/status/${id}`, { status, remark })
}

export const statsApi = {
  getDashboard: () =>
    request.get<DashboardData>('/stats/dashboard'),

  getReport: () =>
    request.get('/stats/report')
}

export const configApi = {
  getSystem: () =>
    request.get<SystemConfig>('/config/system'),

  saveSystem: (data: Partial<SystemConfig>) =>
    request.post<SystemConfig>('/config/system', data)
}
