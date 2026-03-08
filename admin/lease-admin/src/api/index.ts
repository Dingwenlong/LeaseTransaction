import request from '../utils/request'

export interface User {
  id: number
  username: string
  nickname: string
  avatar?: string
  phone?: string
  email?: string
  campus?: string
  creditScore: number
  status: number
  createdAt: string
  updatedAt: string
}

export interface Item {
  id: number
  title: string
  description?: string
  category: string
  type: 'lease' | 'sale'
  price: number
  deposit?: number
  images?: string[]
  location?: string
  ownerId: number
  ownerName?: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface Order {
  id: number
  orderNo: string
  itemId: number
  itemTitle?: string
  buyerId: number
  buyerName?: string
  sellerId: number
  sellerName?: string
  type: 'lease' | 'sale'
  amount: number
  deposit?: number
  status: string
  leaseStart?: string
  leaseEnd?: string
  remark?: string
  createdAt: string
  updatedAt: string
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
  
  getList: (params?: any) => 
    request.get<PageResult<User>>('/user/list', { params }),
  
  updateStatus: (id: number, status: number) => 
    request.post(`/user/status/${id}`, { status }),
  
  delete: (id: number) => 
    request.delete(`/user/${id}`)
}

export const itemApi = {
  getList: (params?: any) => 
    request.get<PageResult<Item>>('/item/list', { params }),
  
  getDetail: (id: number) => 
    request.get<Item>(`/item/detail/${id}`),
  
  approve: (id: number) => 
    request.post(`/item/approve/${id}`),
  
  reject: (id: number, reason: string) => 
    request.post(`/item/reject/${id}`, { reason }),
  
  delete: (id: number) => 
    request.delete(`/item/${id}`)
}

export const orderApi = {
  getList: (params?: any) => 
    request.get<PageResult<Order>>('/order/list', { params }),
  
  getDetail: (id: number) => 
    request.get<Order>(`/order/detail/${id}`),
  
  updateStatus: (id: number, status: string) => 
    request.post(`/order/status/${id}`, { status })
}

export const statsApi = {
  getDashboard: () => 
    request.get('/stats/dashboard')
}
