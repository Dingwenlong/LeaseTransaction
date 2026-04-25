<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <div class="brand-mark">⚡</div>
        <div class="brand-copy">
          <p class="brand-kicker">Campus Lease</p>
          <h1 class="brand-title">租赁交易后台</h1>
        </div>
      </div>

      <nav class="admin-nav">
        <router-link
          v-for="item in visibleMenuItems"
          :key="item.path"
          :to="item.path"
          class="admin-nav-link"
          :class="{ 'is-active': route.path === item.path }"
        >
          <div class="admin-nav-icon">{{ item.icon }}</div>
          <div class="admin-nav-copy">
            <strong>{{ item.name }}</strong>
            <span>{{ item.hint }}</span>
          </div>
        </router-link>
      </nav>

      <div class="sidebar-card">
        <p class="sidebar-card-title">当前焦点</p>
        <p class="sidebar-card-value">{{ currentPage.short }}</p>
        <p class="sidebar-card-meta">{{ currentPage.description }}</p>
      </div>

      <div class="sidebar-card sidebar-card-accent">
        <p class="sidebar-card-title">登录账号</p>
        <p class="sidebar-card-value sidebar-card-user">{{ currentAdminName }}</p>
        <p class="sidebar-card-meta">{{ currentAdminRole }}</p>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div>
          <p class="topbar-kicker">Campus Lease Console</p>
          <h2 class="topbar-title">{{ currentPage.name }}</h2>
          <p class="topbar-text">{{ currentPage.description }}</p>
        </div>
        <div class="topbar-actions">
          <div class="topbar-meta">
            <div class="meta-pill accent">{{ todayLabel }}</div>
          </div>
          <div class="topbar-user">
            <div class="topbar-user-copy">
              <strong>{{ currentAdminName }}</strong>
              <span>{{ currentAdminRole }}</span>
            </div>
            <button class="button button-ghost button-sm" @click="logout">退出登录</button>
          </div>
        </div>
      </header>

      <main class="admin-page">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adminAuthApi } from '../api'
import { authState, clearAdminSession, updateAdminUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()

interface MenuItem {
  path: string
  name: string
  short: string
  icon: string
  hint: string
  description: string
  superAdminOnly?: boolean
}

const menuItems: MenuItem[] = [
  {
    path: '/',
    name: '运营总览',
    short: '总览',
    icon: '🏠',
    hint: '关键指标与待办',
    description: '查看关键指标和待办。'
  },
  {
    path: '/users',
    name: '用户管理',
    short: '用户',
    icon: '👥',
    hint: '账号、信用与状态',
    description: '管理用户状态和认证。'
  },
  {
    path: '/system-users',
    name: '系统用户',
    short: '系统账号',
    icon: '🛡',
    hint: '后台账号、角色与授权',
    description: '管理后台账号和角色。',
    superAdminOnly: true
  },
  {
    path: '/items',
    name: '物品管理',
    short: '物品',
    icon: '📦',
    hint: '发布、审核与状态',
    description: '审核物品和上架状态。'
  },
  {
    path: '/orders',
    name: '订单管理',
    short: '订单',
    icon: '📋',
    hint: '流程、金额与异常',
    description: '跟进订单状态和异常。'
  },
  {
    path: '/reviews',
    name: '评价管理',
    short: '评价',
    icon: '⭐',
    hint: '评分、反馈与差评',
    description: '查看评价记录和差评反馈。'
  },
  {
    path: '/stats',
    name: '数据统计',
    short: '统计',
    icon: '📈',
    hint: '趋势、分布与财务',
    description: '查看趋势和财务统计。'
  },
  {
    path: '/config',
    name: '系统配置',
    short: '配置',
    icon: '🛠',
    hint: '公告、分类与风控',
    description: '维护公告、分类和风控。'
  }
]

const visibleMenuItems = computed(() => {
  return menuItems.filter((item) => !item.superAdminOnly || authState.user?.role === 'SUPER_ADMIN')
})

const currentPage = computed(() => {
  return menuItems.find((item) => item.path === route.path) || menuItems[0]!
})

const currentAdminName = computed(() => authState.user?.displayName || authState.user?.username || '后台账号')

const currentAdminRole = computed(() => {
  if (authState.user?.role === 'SUPER_ADMIN') {
    return '超级管理员'
  }
  if (authState.user?.role === 'OPERATOR') {
    return '运营账号'
  }
  return '未识别角色'
})

const todayLabel = new Intl.DateTimeFormat('zh-CN', {
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())

const syncCurrentAdmin = async () => {
  if (!authState.token) {
    return
  }

  try {
    const userInfo = await adminAuthApi.getMe()
    updateAdminUser(userInfo)
  } catch (error) {
    console.error('同步后台账号信息失败:', error)
  }
}

const logout = () => {
  clearAdminSession()
  router.replace('/login')
}

onMounted(() => {
  syncCurrentAdmin()
})
</script>
