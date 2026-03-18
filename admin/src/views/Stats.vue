<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Analytics</p>
      <h1 class="page-title">用户、类目、订单与财务统计</h1>
      <p class="page-description">查看校区、类目、订单和财务。</p>
    </div>
    <div class="page-actions">
      <button class="button button-primary" @click="loadData">
        <span>刷新数据</span>
        <span>↻</span>
      </button>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in dashboard.metrics" :key="metric.label" class="metric-card">
      <p class="metric-label">{{ metric.label }}</p>
      <div class="metric-value-row">
        <h3 class="metric-value">{{ metric.value }}</h3>
        <span class="status-pill" :class="metric.tone">{{ metric.delta }}</span>
      </div>
      <p class="metric-foot">用于快速查看当前统计结果。</p>
    </article>
  </section>

  <section class="split-grid">
    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">校区分布</h2>
          <p class="panel-subtitle">查看校区活跃度。</p>
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
          <p class="panel-subtitle">查看类目热度。</p>
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
          <p class="panel-subtitle">查看订单状态分布。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="bar-list">
          <div v-for="item in dashboard.orderStatusDistribution" :key="item.name" class="bar-item">
            <div class="bar-head">
              <strong>{{ item.name }}</strong>
              <span>{{ item.value }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill violet" :style="{ width: `${ratio(item.value, maxOrderStatus)}%` }"></div>
            </div>
          </div>
        </div>
      </div>
    </article>

    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">财务汇总</h2>
          <p class="panel-subtitle">查看收入、退款和押金。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div class="list-item">
            <div>
              <p class="list-item-title">收入</p>
              <p class="list-item-text">已成功支付并计入平台流水的金额。</p>
            </div>
            <span class="status-pill green">¥{{ finance.income }}</span>
          </div>
          <div class="list-item">
            <div>
              <p class="list-item-title">退款</p>
              <p class="list-item-text">已进入退款链路或完成退款的金额总和。</p>
            </div>
            <span class="status-pill red">¥{{ finance.refund }}</span>
          </div>
          <div class="list-item">
            <div>
              <p class="list-item-title">押金</p>
              <p class="list-item-text">当前累计押金记录，用于观察高风险订单占比。</p>
            </div>
            <span class="status-pill yellow">¥{{ finance.deposit }}</span>
          </div>
        </div>
      </div>
    </article>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { statsApi, type DashboardData } from '../api'

const dashboard = ref<DashboardData>({
  hero: { title: '', subtitle: '', updatedAt: '' },
  metrics: [],
  campusDistribution: [],
  categoryRanking: [],
  orderStatusDistribution: [],
  watchList: []
})

const finance = ref({
  income: '0.00',
  refund: '0.00',
  deposit: '0.00'
})

const maxCampus = computed(() => Math.max(1, ...dashboard.value.campusDistribution.map((item) => item.value)))
const maxCategory = computed(() => Math.max(1, ...dashboard.value.categoryRanking.map((item) => item.value)))
const maxOrderStatus = computed(() => Math.max(1, ...dashboard.value.orderStatusDistribution.map((item) => item.value)))

const ratio = (value: number, max: number) => Math.max(12, Math.round((value / max) * 100))

const loadData = async () => {
  const [dashboardRes, reportRes] = await Promise.all([
    statsApi.getDashboard(),
    statsApi.getReport() as Promise<any>
  ])
  dashboard.value = dashboardRes
  finance.value = {
    income: Number(reportRes.finance?.income || 0).toFixed(2),
    refund: Number(reportRes.finance?.refund || 0).toFixed(2),
    deposit: Number(reportRes.finance?.deposit || 0).toFixed(2)
  }
}

onMounted(() => {
  loadData().catch(() => {
    finance.value = { income: '0.00', refund: '0.00', deposit: '0.00' }
  })
})
</script>
