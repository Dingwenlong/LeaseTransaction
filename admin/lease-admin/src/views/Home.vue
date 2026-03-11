<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Overview</p>
      <h1 class="page-title">让后台和客户端看起来像同一个产品。</h1>
      <p class="page-description">
        以统一色板、卡片框架和信息密度重建主界面节奏。后台负责高效扫描，小程序负责轻量浏览，但两者的视觉语言保持一致。
      </p>
    </div>
    <div class="page-actions">
      <router-link to="/items" class="button button-primary">
        <span>查看物品管理</span>
        <span>→</span>
      </router-link>
      <router-link to="/orders" class="button button-secondary">
        <span>处理订单流程</span>
        <span>↗</span>
      </router-link>
    </div>
  </section>

  <section class="hero-panel">
    <div class="hero-grid">
      <div class="hero-copy">
        <p class="page-eyebrow">Design Sync</p>
        <h2 class="hero-title">Y2K 霓虹质感被保留，但排版先回到秩序。</h2>
        <p class="hero-note">
          页面头部、工具栏、表格和详情弹窗都采用同一套结构规则：先看状态，再看对象，再看动作。这样既能保住项目的未来感，也能让管理动作更快落点。
        </p>
        <div class="badge-group">
          <span class="status-pill cyan">统一色板</span>
          <span class="status-pill magenta">双端卡片</span>
          <span class="status-pill green">安全区适配</span>
        </div>
      </div>

      <div class="hero-stack">
        <div class="info-card">
          <p class="info-card-label">当前视觉状态</p>
          <p class="info-card-value">主路径已统一</p>
          <p class="info-card-text">后台仪表盘、表格视图和小程序主页面共享同一套圆角、边框、按钮和标签风格。</p>
        </div>
        <div class="info-card">
          <p class="info-card-label">最后刷新时间</p>
          <p class="info-card-value">{{ currentTime }}</p>
          <p class="info-card-text">用于确认数据概览区和运营视图在同一时间基线上更新。</p>
        </div>
      </div>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="stat in stats" :key="stat.label" class="metric-card">
      <p class="metric-label">{{ stat.label }}</p>
      <div class="metric-value-row">
        <h3 class="metric-value">{{ stat.value }}</h3>
        <span class="status-pill" :class="stat.tone">{{ stat.delta }}</span>
      </div>
      <p class="metric-foot">{{ stat.note }}</p>
    </article>
  </section>

  <section class="split-grid">
    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">本轮页面优化重点</h2>
          <p class="panel-subtitle">先把常用页面的扫描效率拉齐，再细化二级流程。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="focus in focusList" :key="focus.title" class="list-item">
            <div>
              <p class="list-item-title">{{ focus.title }}</p>
              <p class="list-item-text">{{ focus.description }}</p>
            </div>
            <span class="status-pill" :class="focus.tone">{{ focus.tag }}</span>
          </div>
        </div>
      </div>
    </article>

    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">同步记录</h2>
          <p class="panel-subtitle">后台与小程序统一时最容易失真的位置。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="timeline">
          <div v-for="item in timeline" :key="item.title" class="timeline-item">
            <div class="timeline-marker">{{ item.icon }}</div>
            <div>
              <p class="timeline-title">{{ item.title }}</p>
              <p class="timeline-meta">{{ item.meta }}</p>
              <p class="timeline-text">{{ item.text }}</p>
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
          <h2 class="panel-title">运营观察点</h2>
          <p class="panel-subtitle">首页只保留最关键的监控内容，不堆积无效装饰。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="item in watchList" :key="item.title" class="list-item">
            <div>
              <p class="list-item-title">{{ item.title }}</p>
              <p class="list-item-text">{{ item.text }}</p>
            </div>
            <span class="mini-chip">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </article>

    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">视觉约束</h2>
          <p class="panel-subtitle">确保新页面继续沿着同一套系统扩展。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="rule in designRules" :key="rule.title" class="list-item">
            <div>
              <p class="list-item-title">{{ rule.title }}</p>
              <p class="list-item-text">{{ rule.text }}</p>
            </div>
            <span class="status-pill slate">{{ rule.tag }}</span>
          </div>
        </div>
      </div>
    </article>
  </section>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const currentTime = ref('')
let timer: number | null = null

const stats = [
  { label: '活跃用户', value: '3,247', delta: '本周 +12%', tone: 'cyan', note: '状态和信用信息在同一张表里呈现，减少来回跳转。' },
  { label: '上架物品', value: '1,089', delta: '待审 38', tone: 'yellow', note: '物品卡片统一价格区、状态标签和操作位，批量审核更直观。' },
  { label: '进行中订单', value: '156', delta: '异常 4', tone: 'magenta', note: '订单表聚焦状态、金额和交易双方，异常单更容易被识别。' },
  { label: '月度交易额', value: '¥125,678', delta: '环比 +18%', tone: 'green', note: '后台与小程序共享同一套金额强调方式，强化交易心智。' }
]

const focusList = [
  {
    title: '统一页面头部与工具栏',
    description: '后台四个主视图全部采用一致的标题、说明、操作区和筛选结构，浏览路径不再跳跃。',
    tag: '基础层',
    tone: 'cyan'
  },
  {
    title: '收束卡片和表格密度',
    description: '减少重复描边和装饰，保留霓虹高光，但让正文、次要信息和动作按钮有稳定层级。',
    tag: '信息层级',
    tone: 'magenta'
  },
  {
    title: '修正固定底栏与安全区',
    description: '小程序的详情页、发布页和订单页补齐底部安全区与内容留白，避免操作区遮挡内容。',
    tag: '客户端',
    tone: 'green'
  }
]

const timeline = [
  {
    icon: '01',
    title: '后台主壳层重排',
    meta: 'Sidebar / Topbar / Page Shell',
    text: '把侧栏、顶部说明和页面主区域拆成统一容器，避免每个页面单独撑版。'
  },
  {
    icon: '02',
    title: '表格与详情视图统一',
    meta: 'Users / Items / Orders',
    text: '用户、物品、订单都采用同样的统计卡、筛选区、表格外壳和详情卡片。'
  },
  {
    icon: '03',
    title: '小程序主路径收束',
    meta: 'Index / Detail / Order / Profile',
    text: '首页、详情、发布、消息和我的统一卡片结构、标签体系和底部动作栏。'
  }
]

const watchList = [
  { title: '订单异常', text: '重点跟踪待付款超时、退款中和沟通异常场景。', value: '4 条待处理' },
  { title: '审核积压', text: '物品审核区保持紧凑卡片布局，减少在列表里来回扫描。', value: '38 条待审' },
  { title: '视觉一致性', text: '后台和小程序保持同一色板、同一标签语义和同一级别的主按钮。', value: '已同步' }
]

const designRules = [
  { title: '主按钮只承担关键动作', text: '一个区域只保留一个高亮主动作，其他操作降级为次级按钮或标签。', tag: 'Action' },
  { title: '辅助信息压低对比度', text: '时间、校区、备注这类辅助信息使用统一次级文本，不与金额和状态抢焦点。', tag: 'Hierarchy' },
  { title: '弹窗保持双列详情卡片', text: '详情弹窗统一使用状态区 + 双列详情卡，扫描速度更稳定。', tag: 'Modal' }
]

const updateTime = () => {
  currentTime.value = new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(new Date())
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer !== null) {
    clearInterval(timer)
  }
})
</script>
