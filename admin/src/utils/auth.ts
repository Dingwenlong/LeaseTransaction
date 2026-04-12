import { reactive } from 'vue'

export interface AdminSessionUser {
  id: number
  username: string
  displayName: string
  role: string
  status: number
  lastLoginTime?: string
  createdAt?: string
  updatedAt?: string
}

const TOKEN_KEY = 'admin_token'
const USER_KEY = 'admin_user'

const parseUser = () => {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as AdminSessionUser
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const authState = reactive<{
  token: string
  user: AdminSessionUser | null
}>({
  token: localStorage.getItem(TOKEN_KEY) || '',
  user: parseUser()
})

export const getAdminToken = () => authState.token

export const setAdminSession = (token: string, user: AdminSessionUser) => {
  authState.token = token
  authState.user = user
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export const updateAdminUser = (user: AdminSessionUser) => {
  authState.user = user
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export const clearAdminSession = () => {
  authState.token = ''
  authState.user = null
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export const isAdminAuthenticated = () => Boolean(authState.token)
