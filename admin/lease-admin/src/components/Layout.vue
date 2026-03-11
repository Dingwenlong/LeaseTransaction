<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <div class="brand-mark">⚡</div>
        <div class="brand-copy">
          <p class="brand-kicker">Campus Lease</p>
          <h1 class="brand-title">租赁交易后台</h1>
          <p class="brand-subtitle">统一后台与客户端视觉秩序</p>
        </div>
      </div>

      <div class="sidebar-card">
        <p class="sidebar-card-title">本轮设计基线</p>
        <p class="sidebar-card-value">Y2K / Neon</p>
        <p class="sidebar-card-meta">
          统一卡片圆角、信息层级、按钮反馈和霓虹边框密度，让管理端与小程序共享一套视觉语言。
        </p>
      </div>

      <nav class="admin-nav">
        <router-link
          v-for="item in menuItems"
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
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div>
          <p class="topbar-kicker">Campus Lease Console</p>
          <h2 class="topbar-title">{{ currentPage.name }}</h2>
          <p class="topbar-text">{{ currentPage.description }}</p>
        </div>
        <div class="topbar-meta">
          <div class="meta-pill">统一后台 / 小程序</div>
          <div class="meta-pill accent">{{ todayLabel }}</div>
        </div>
      </header>

      <main class="admin-page">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

interface MenuItem {
  path: string
  name: string
  short: string
  icon: string
  hint: string
  description: string
}

const menuItems: MenuItem[] = [
  {
    path: '/',
    name: '运营总览',
    short: '总览',
    icon: '🏠',
    hint: '平台概况与节奏',
    description: '查看系统关键指标、近期动作和模块联动状态。'
  },
  {
    path: '/users',
    name: '用户管理',
    short: '用户',
    icon: '👥',
    hint: '账号、信用与活跃度',
    description: '统一用户信息、信用分与状态管理，让数据更容易横向比较。'
  },
  {
    path: '/items',
    name: '物品管理',
    short: '物品',
    icon: '📦',
    hint: '发布、审核与陈列',
    description: '梳理物品卡片、价格区和状态信息，让审核与巡检更清晰。'
  },
  {
    path: '/orders',
    name: '订单管理',
    short: '订单',
    icon: '📋',
    hint: '流程、金额与异常',
    description: '聚焦订单状态、交易金额和详情弹窗，提升处理效率。'
  },
  {
    path: '/stats',
    name: '数据统计',
    short: '统计',
    icon: '📈',
    hint: '趋势、分布与财务',
    description: '查看用户活跃、类目热度、订单结构和财务概览，支持运营决策。'
  },
  {
    path: '/config',
    name: '系统配置',
    short: '配置',
    icon: '🛠',
    hint: '公告、分类与风控',
    description: '统一管理首页内容、分类字典、校区信息和风控规则。'
  }
]

const currentPage = computed(() => {
  return menuItems.find((item) => item.path === route.path) || menuItems[0]!
})

const todayLabel = new Intl.DateTimeFormat('zh-CN', {
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())
</script>
