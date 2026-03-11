<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Orders</p>
      <h1 class="page-title">订单流程、金额和异常状态直接挂钩处理动作。</h1>
      <p class="page-description">
        订单页按状态驱动处理，待付款、履约中、待验收和纠纷态都可在列表或详情中快速推进。
      </p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">重置条件</button>
      <button class="button button-primary" @click="loadOrders">
        <span>刷新订单</span>
        <span>↻</span>
      </button>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in orderMetrics" :key="metric.label" class="metric-card">
      <p class="metric-label">{{ metric.label }}</p>
      <div class="metric-value-row">
        <h3 class="metric-value">{{ metric.value }}</h3>
        <span class="status-pill" :class="metric.tone">{{ metric.tag }}</span>
      </div>
      <p class="metric-foot">{{ metric.note }}</p>
    </article>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">订单筛选</h2>
        <p class="panel-subtitle">状态和类型均与后端枚举保持一致，避免前后台文案错位。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid">
        <label class="field">
          <span class="field-label">订单号 / 关键词</span>
          <input v-model="searchKeyword" class="field-control" type="text" placeholder="订单号、物品、买家、卖家" @keyup.enter="loadOrders" />
        </label>
        <label class="field">
          <span class="field-label">订单类型</span>
          <select v-model="searchType" class="field-control">
            <option value="">全部类型</option>
            <option :value="1">租赁订单</option>
            <option :value="2">交易订单</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">订单状态</span>
          <select v-model="searchStatus" class="field-control">
            <option value="">全部状态</option>
            <option :value="1">待付款</option>
            <option :value="2">待交付</option>
            <option :value="3">进行中</option>
            <option :value="4">待归还验收</option>
            <option :value="5">已完成</option>
            <option :value="6">已取消</option>
            <option :value="7">纠纷中</option>
            <option :value="8">退款中</option>
          </select>
        </label>
        <div class="field">
          <span class="field-label">当前结果</span>
          <div class="field-control field-static">当前展示 {{ orders.length }} 条订单</div>
        </div>
      </div>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">订单列表</h2>
        <p class="panel-subtitle">优先突出订单状态、交易双方和金额，详情弹窗再展开时间区间与备注。</p>
      </div>
      <span class="mini-chip">总额 ¥{{ totalAmount }}</span>
    </div>

    <div class="table-scroll">
      <table class="data-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>物品信息</th>
            <th>交易双方</th>
            <th>类型</th>
            <th>金额</th>
            <th>状态</th>
            <th>时间</th>
            <th style="text-align: right;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td><span class="table-id wrap-break">{{ order.orderNo }}</span></td>
            <td>
              <div class="avatar-line">
                <div class="avatar-badge">📦</div>
                <div class="truncate">
                  <p class="table-primary truncate">{{ order.itemTitle }}</p>
                  <p class="table-secondary">物品 ID {{ order.itemId }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="table-primary">{{ order.buyerName }}</p>
              <p class="table-secondary">卖家 / 出租人 {{ order.sellerName }}</p>
            </td>
            <td>
              <span class="status-pill" :class="order.type === 1 ? 'cyan' : 'magenta'">{{ order.typeText }}</span>
            </td>
            <td>
              <p class="table-primary table-amount">¥{{ Number(order.totalAmount).toFixed(2) }}</p>
              <p class="table-secondary">{{ order.remark || '无额外备注' }}</p>
            </td>
            <td>
              <span class="status-pill" :class="getStatusClass(order.status)">{{ order.statusText }}</span>
            </td>
            <td>
              <p class="table-primary">{{ formatDate(order.createdAt) }}</p>
              <p class="table-secondary">更新于 {{ formatDate(order.updatedAt) }}</p>
            </td>
            <td>
              <div class="table-actions" style="justify-content: flex-end;">
                <button class="button button-ghost button-sm" @click="viewOrder(order)">查看</button>
                <button
                  v-for="action in getActions(order)"
                  :key="action.label"
                  class="button button-sm"
                  :class="action.className"
                  @click="updateStatus(order, action.status, action.remark)"
                >
                  {{ action.label }}
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="loading">
            <td colspan="8">
              <div class="empty-state">
                <strong>正在加载订单数据</strong>
                <span>后台正在同步订单流程信息。</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="orders.length === 0">
            <td colspan="8">
              <div class="empty-state">
                <strong>暂无订单数据</strong>
                <span>当前筛选条件下没有订单。</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

  <div v-if="showDetail && currentOrder" class="modal-overlay" @click.self="showDetail = false">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">订单详情</h2>
          <p class="modal-description">{{ currentOrder.orderNo }}</p>
        </div>
        <button class="modal-close" @click="showDetail = false">✕</button>
      </div>
      <div class="modal-body">
        <div class="status-banner" :class="getStatusClass(currentOrder.status)">
          <p class="detail-label">订单状态</p>
          <p class="detail-value">{{ currentOrder.statusText }} · {{ currentOrder.typeText }}</p>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <p class="detail-label">物品名称</p>
            <p class="detail-value">{{ currentOrder.itemTitle }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">总金额</p>
            <p class="detail-value">¥{{ Number(currentOrder.totalAmount).toFixed(2) }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">买家 / 承租人</p>
            <p class="detail-value">{{ currentOrder.buyerName }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">卖家 / 出租人</p>
            <p class="detail-value">{{ currentOrder.sellerName }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">交付方式</p>
            <p class="detail-value">{{ currentOrder.deliveryMethod || '校内面交' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">押金</p>
            <p class="detail-value">{{ currentOrder.deposit ? `¥${currentOrder.deposit}` : '无' }}</p>
          </div>
          <div class="detail-card" v-if="currentOrder.startDate">
            <p class="detail-label">租赁开始</p>
            <p class="detail-value">{{ formatDate(currentOrder.startDate) }}</p>
          </div>
          <div class="detail-card" v-if="currentOrder.endDate">
            <p class="detail-label">租赁结束</p>
            <p class="detail-value">{{ formatDate(currentOrder.endDate) }}</p>
          </div>
        </div>

        <div class="detail-card">
          <p class="detail-label">备注信息</p>
          <p class="detail-value">{{ currentOrder.remark || '暂无备注' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type Order } from '../api'

const searchKeyword = ref('')
const searchType = ref<string | number>('')
const searchStatus = ref<string | number>('')
const loading = ref(false)
const orders = ref<Order[]>([])
const showDetail = ref(false)
const currentOrder = ref<Order | null>(null)

const totalAmount = computed(() => {
  return orders.value.reduce((sum, order) => sum + Number(order.totalAmount), 0).toFixed(2)
})

const orderMetrics = computed(() => {
  const list = orders.value
  const pending = list.filter((order) => order.status === 1).length
  const active = list.filter((order) => order.status === 3 || order.status === 4).length
  const dispute = list.filter((order) => order.status === 7).length
  return [
    {
      label: '订单总数',
      value: `${list.length}`,
      tag: '当前筛选',
      tone: 'cyan',
      note: '真实订单数据已接入后台列表。'
    },
    {
      label: '待付款',
      value: `${pending}`,
      tag: pending > 0 ? '待跟进' : '稳定',
      tone: pending > 0 ? 'yellow' : 'green',
      note: '待付款单支持直接取消或推进支付。'
    },
    {
      label: '履约中',
      value: `${active}`,
      tag: '进行中',
      tone: active > 0 ? 'magenta' : 'slate',
      note: '重点关注租赁结束前的提醒与验收。'
    },
    {
      label: '纠纷中',
      value: `${dispute}`,
      tag: dispute > 0 ? '需仲裁' : '正常',
      tone: dispute > 0 ? 'red' : 'green',
      note: '纠纷单单独高亮，避免被其他单据淹没。'
    }
  ]
})

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    1: 'yellow',
    2: 'cyan',
    3: 'magenta',
    4: 'violet',
    5: 'green',
    6: 'slate',
    7: 'red',
    8: 'red'
  }
  return classes[status] || 'slate'
}

const getActions = (order: Order) => {
  if (order.status === 1) {
    return [
      { label: '设为已支付', status: 2, className: 'button-success', remark: '后台确认支付成功' },
      { label: '取消', status: 6, className: 'button-danger', remark: '后台取消订单' }
    ]
  }
  if (order.status === 2) {
    return [{ label: '开始履约', status: 3, className: 'button-primary', remark: '已完成交付' }]
  }
  if (order.status === 3) {
    return [{ label: '待验收', status: 4, className: 'button-secondary', remark: '等待归还验收' }]
  }
  if (order.status === 4) {
    return [{ label: '完成', status: 5, className: 'button-success', remark: '订单已验收完成' }]
  }
  if (order.status === 7) {
    return [{ label: '转退款中', status: 8, className: 'button-danger', remark: '进入退款流程' }]
  }
  return []
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getList({
      page: 1,
      size: 20,
      adminView: true,
      keyword: searchKeyword.value || undefined,
      type: searchType.value !== '' ? searchType.value : undefined,
      status: searchStatus.value !== '' ? searchStatus.value : undefined
    })
    orders.value = res.records || []
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  searchType.value = ''
  searchStatus.value = ''
  loadOrders()
}

const viewOrder = async (order: Order) => {
  currentOrder.value = await orderApi.getDetail(order.id)
  showDetail.value = true
}

const updateStatus = async (order: Order, status: number, remark: string) => {
  try {
    await ElMessageBox.confirm(`确定要将订单更新为“${status}”对应流程吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await orderApi.updateStatus(order.id, status, remark)
    ElMessage.success('订单状态已更新')
    if (currentOrder.value?.id === order.id) {
      currentOrder.value = await orderApi.getDetail(order.id)
    }
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
      ElMessage.error('更新状态失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>
