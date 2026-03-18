<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Console Home</p>
      <h1 class="page-title">校园租赁与交易统一后台。</h1>
      <p class="page-description">
        首页直接呈现活跃用户、物品审核、订单履约和财务概况，并把校区、类目和状态分布收束到同一视图。
      </p>
    </div>
    <div class="page-actions">
      <router-link to="/stats" class="button button-primary">
        <span>查看统计大盘</span>
        <span>↗</span>
      </router-link>
      <router-link to="/config" class="button button-secondary">
        <span>维护系统配置</span>
        <span>⚙</span>
      </router-link>
    </div>
  </section>

  <section class="hero-panel">
    <div class="hero-grid">
      <div class="hero-copy">
        <p class="page-eyebrow">Y2K Control Deck</p>
        <h2 class="hero-title">{{ dashboard.hero.title }}</h2>
        <p class="hero-note">{{ dashboard.hero.subtitle }}</p>
        <div class="badge-group">
          <span class="status-pill cyan">租赁与交易双模式</span>
          <span class="status-pill magenta">订单与消息联动</span>
          <span class="status-pill green">信用与押金闭环</span>
        </div>
      </div>

      <div class="hero-stack">
        <div class="info-card">
          <p class="info-card-label">最新同步时间</p>
          <p class="info-card-value">{{ formatDateTime(dashboard.hero.updatedAt) }}</p>
          <p class="info-card-text">数据概览、物品审核和订单状态按同一时间基准刷新。</p>
        </div>
        <div class="info-card">
          <p class="info-card-label">运营提示</p>
          <p class="info-card-value">{{ primaryWatch.title }}</p>
          <p class="info-card-text">{{ primaryWatch.text }}</p>
        </div>
      </div>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in dashboard.metrics" :key="metric.label" class="metric-card">
      <p class="metric-label">{{ metric.label }}</p>
      <div class="metric-value-row">
        <h3 class="metric-value">{{ metric.value }}</h3>
        <span class="status-pill" :class="metric.tone">{{ metric.delta }}</span>
      </div>
      <p class="metric-foot">后台直接锚定关键数字，减少进入二级页面前的信息损耗。</p>
    </article>
  </section>

  <section class="split-grid">
    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">校区活跃分布</h2>
          <p class="panel-subtitle">用于校区附近推荐、物品投放和线下交接策略判断。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="bar-list">
          <div v-for="item in dashboard.campusDistribution" :key="item.name" class="bar-item">
            <div class="bar-head">
              <strong>{{ item.name }}</strong>
              <span>{{ item.value }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill cyan" :style="{ width: `${ratio(item.value, maxCampus)}%` }"></div>
            </div>
          </div>
        </div>
      </div>
    </article>

    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">热门类目</h2>
          <p class="panel-subtitle">审核优先级、首页陈列和活动专题优先围绕高热度类目展开。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="bar-list">
          <div v-for="item in dashboard.categoryRanking" :key="item.name" class="bar-item">
            <div class="bar-head">
              <strong>{{ item.name }}</strong>
              <span>{{ item.value }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill magenta" :style="{ width: `${ratio(item.value, maxCategory)}%` }"></div>
            </div>
          </div>
        </div>
      </div>
    </article>
  </section>

  <section class="card-grid two">
    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">订单状态结构</h2>
          <p class="panel-subtitle">及时识别积压状态，把处理注意力给到最需要的流程节点。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="item in dashboard.orderStatusDistribution" :key="item.name" class="list-item">
            <div>
              <p class="list-item-title">{{ item.name }}</p>
              <p class="list-item-text">订单流程节点数量直接影响客服、仲裁和履约跟进负载。</p>
            </div>
            <span class="mini-chip">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </article>

    <article class="panel quote-panel">
      <div class="panel-body">
        <p class="quote-kicker">Signal</p>
        <p class="quote-copy">“后台先给出判断依据，再给操作入口。未来感是表层，效率才是底层。”</p>
        <div class="quote-line"></div>
        <p class="quote-meta">Y2K 风格 Hero、数据条和 CTA 已统一到管理端主视图。</p>
      </div>
    </article>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">运营观察点</h2>
        <p class="panel-subtitle">重点看待审物品、履约中订单与校区覆盖度。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="list-stack">
        <div v-for="item in dashboard.watchList" :key="item.title" class="list-item">
          <div>
            <p class="list-item-title">{{ item.title }}</p>
            <p class="list-item-text">{{ item.text }}</p>
          </div>
          <span class="status-pill cyan">{{ item.value }}</span>
        </div>
      </div>
    </div>
  </section>

  <section class="panel cta-panel">
    <div class="panel-body cta-body">
      <div>
        <p class="page-eyebrow">Next Move</p>
        <h2 class="panel-title">继续处理物品审核、订单监控和系统公告。</h2>
        <p class="panel-subtitle">首页只做总览，真正的管理动作继续落到用户、物品、订单、统计和配置页。</p>
      </div>
      <div class="page-actions">
        <router-link to="/items" class="button button-primary">进入物品审核</router-link>
        <router-link to="/orders" class="button button-ghost">处理订单监控</router-link>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { statsApi, type DashboardData } from '../api'

const fallbackDashboard: DashboardData = {
  hero: {
    title: 'Campus Lease Console',
    subtitle: '租赁、交易、审核、消息与风控统一收束到一个后台视图。',
    updatedAt: new Date().toISOString()
  },
  metrics: [
    { label: '活跃用户', value: '0', delta: '等待同步', tone: 'slate' },
    { label: '上架物品', value: '0', delta: '等待同步', tone: 'slate' },
    { label: '进行中订单', value: '0', delta: '等待同步', tone: 'slate' },
    { label: '累计交易额', value: '¥0.00', delta: '等待同步', tone: 'slate' }
  ],
  campusDistribution: [],
  categoryRanking: [],
  orderStatusDistribution: [],
  watchList: []
}

const dashboard = ref<DashboardData>(fallbackDashboard)

const primaryWatch = computed(() => {
  return dashboard.value.watchList[0] || {
    title: '等待数据',
    text: '后端服务启动后会在这里显示当前最值得优先处理的运营事项。'
  }
})

const maxCampus = computed(() => {
  return Math.max(1, ...dashboard.value.campusDistribution.map((item) => item.value))
})

const maxCategory = computed(() => {
  return Math.max(1, ...dashboard.value.categoryRanking.map((item) => item.value))
})

const ratio = (value: number, max: number) => {
  return Math.max(12, Math.round((value / max) * 100))
}

const formatDateTime = (value: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

const loadDashboard = async () => {
  dashboard.value = await statsApi.getDashboard()
}

onMounted(() => {
  loadDashboard().catch(() => {
    dashboard.value = fallbackDashboard
  })
})
</script>
