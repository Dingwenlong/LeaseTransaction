<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Orders</p>
      <h1 class="page-title">订单页只保留与决策直接相关的信息。</h1>
      <p class="page-description">
        订单管理改成强状态导向的表格：先看状态与金额，再看物品与交易双方，详情弹窗也保持同样的阅读顺序。
      </p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">重置条件</button>
      <button class="button button-primary" @click="handleSearch">
        <span>应用筛选</span>
        <span>→</span>
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
        <p class="panel-subtitle">维持后台与客户端同样的状态语义，避免前后台文案错位。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid">
        <label class="field">
          <span class="field-label">订单号 / 关键词</span>
          <input
            v-model="searchKeyword"
            class="field-control"
            type="text"
            placeholder="输入订单号、物品名或用户"
            @keyup.enter="handleSearch"
          />
        </label>
        <label class="field">
          <span class="field-label">订单类型</span>
          <select v-model="searchType" class="field-control">
            <option value="">全部类型</option>
            <option value="lease">租赁订单</option>
            <option value="sale">交易订单</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">订单状态</span>
          <select v-model="searchStatus" class="field-control">
            <option value="">全部状态</option>
            <option value="pending">待付款</option>
            <option value="paid">已付款</option>
            <option value="in_progress">进行中</option>
            <option value="completed">已完成</option>
            <option value="cancelled">已取消</option>
            <option value="refunded">已退款</option>
          </select>
        </label>
        <div class="field">
          <span class="field-label">当前结果</span>
          <div class="field-control" style="display: flex; align-items: center;">
            当前展示 {{ filteredOrders.length }} 条订单
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">订单列表</h2>
        <p class="panel-subtitle">把订单号、物品名和交易双方拆成稳定列，避免一行信息过载。</p>
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
          <tr v-for="order in filteredOrders" :key="order.id">
            <td>
              <span class="table-id wrap-break">{{ order.orderNo }}</span>
            </td>
            <td>
              <div class="avatar-line">
                <div class="avatar-badge">📦</div>
                <div class="truncate">
                  <p class="table-primary truncate">{{ order.itemName }}</p>
                  <p class="table-secondary">物品 ID {{ order.itemId }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="table-primary">{{ order.buyerName }}</p>
              <p class="table-secondary">卖家 / 出租人 {{ order.sellerName }}</p>
            </td>
            <td>
              <span class="status-pill" :class="order.type === 'lease' ? 'cyan' : 'magenta'">
                {{ order.type === 'lease' ? '租赁' : '交易' }}
              </span>
            </td>
            <td>
              <p class="table-primary table-amount">¥{{ order.amount.toFixed(2) }}</p>
              <p class="table-secondary">{{ order.remark || '无额外备注' }}</p>
            </td>
            <td>
              <span class="status-pill" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td>
              <p class="table-primary">{{ order.createdAt }}</p>
              <p class="table-secondary">更新于 {{ order.updatedAt }}</p>
            </td>
            <td>
              <div class="table-actions" style="justify-content: flex-end;">
                <button class="button button-ghost button-sm" @click="viewOrder(order)">查看</button>
                <button
                  v-if="order.status === 'pending'"
                  class="button button-danger button-sm"
                  @click="cancelOrder(order)"
                >
                  取消
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredOrders.length === 0">
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
        <div class="status-banner" :class="getStatusBannerClass(currentOrder.status)">
          <p class="detail-label">订单状态</p>
          <p class="detail-value">{{ getStatusText(currentOrder.status) }} · {{ currentOrder.type === 'lease' ? '租赁流程' : '交易流程' }}</p>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <p class="detail-label">物品名称</p>
            <p class="detail-value">{{ currentOrder.itemName }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">订单金额</p>
            <p class="detail-value">¥{{ currentOrder.amount.toFixed(2) }}</p>
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
            <p class="detail-label">创建时间</p>
            <p class="detail-value">{{ currentOrder.createdAt }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">更新时间</p>
            <p class="detail-value">{{ currentOrder.updatedAt }}</p>
          </div>
          <div v-if="currentOrder.leaseStart" class="detail-card">
            <p class="detail-label">租赁开始</p>
            <p class="detail-value">{{ currentOrder.leaseStart }}</p>
          </div>
          <div v-if="currentOrder.leaseEnd" class="detail-card">
            <p class="detail-label">租赁结束</p>
            <p class="detail-value">{{ currentOrder.leaseEnd }}</p>
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
import { computed, ref } from 'vue'

interface Order {
  id: number
  orderNo: string
  itemId: number
  itemName: string
  buyerId: number
  buyerName: string
  sellerId: number
  sellerName: string
  type: 'lease' | 'sale'
  amount: number
  status: string
  leaseStart?: string
  leaseEnd?: string
  remark?: string
  createdAt: string
  updatedAt: string
}

const searchKeyword = ref('')
const searchType = ref('')
const searchStatus = ref('')
const showDetail = ref(false)
const currentOrder = ref<Order | null>(null)

const orders = ref<Order[]>([
  {
    id: 1,
    orderNo: 'ORD202412010001',
    itemId: 101,
    itemName: 'iPhone 15 Pro Max',
    buyerId: 2,
    buyerName: '李四同学',
    sellerId: 1,
    sellerName: '张三同学',
    type: 'lease',
    amount: 150,
    status: 'in_progress',
    leaseStart: '2024-12-01',
    leaseEnd: '2024-12-07',
    remark: '租期一周，押金 5000 元',
    createdAt: '2024-12-01 10:30:00',
    updatedAt: '2024-12-01 10:35:00'
  },
  {
    id: 2,
    orderNo: 'ORD202412010002',
    itemId: 102,
    itemName: '高等数学教材',
    buyerId: 3,
    buyerName: '王五同学',
    sellerId: 1,
    sellerName: '张三同学',
    type: 'sale',
    amount: 25,
    status: 'completed',
    remark: '当面交易',
    createdAt: '2024-12-01 11:20:00',
    updatedAt: '2024-12-02 15:00:00'
  },
  {
    id: 3,
    orderNo: 'ORD202412010003',
    itemId: 103,
    itemName: '羽毛球拍',
    buyerId: 1,
    buyerName: '张三同学',
    sellerId: 4,
    sellerName: '赵六同学',
    type: 'lease',
    amount: 30,
    status: 'pending',
    leaseStart: '2024-12-03',
    leaseEnd: '2024-12-05',
    remark: '等待买家付款',
    createdAt: '2024-12-01 14:45:00',
    updatedAt: '2024-12-01 14:45:00'
  },
  {
    id: 4,
    orderNo: 'ORD202412010004',
    itemId: 104,
    itemName: '露营帐篷',
    buyerId: 5,
    buyerName: '陈七同学',
    sellerId: 6,
    sellerName: '刘八同学',
    type: 'lease',
    amount: 80,
    status: 'refunded',
    remark: '因天气取消行程，已退款',
    createdAt: '2024-12-02 08:12:00',
    updatedAt: '2024-12-02 10:18:00'
  }
])

const filteredOrders = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  return orders.value.filter((order) => {
    const matchesKeyword =
      keyword.length === 0 ||
      order.orderNo.toLowerCase().includes(keyword) ||
      order.itemName.toLowerCase().includes(keyword) ||
      order.buyerName.toLowerCase().includes(keyword) ||
      order.sellerName.toLowerCase().includes(keyword)

    const matchesType = searchType.value === '' || order.type === searchType.value
    const matchesStatus = searchStatus.value === '' || order.status === searchStatus.value

    return matchesKeyword && matchesType && matchesStatus
  })
})

const totalAmount = computed(() => {
  return filteredOrders.value.reduce((sum, order) => sum + order.amount, 0).toFixed(2)
})

const orderMetrics = computed(() => {
  const list = filteredOrders.value
  const pending = list.filter((order) => order.status === 'pending').length
  const active = list.filter((order) => order.status === 'in_progress').length
  const completed = list.filter((order) => order.status === 'completed').length

  return [
    {
      label: '订单总数',
      value: `${list.length}`,
      tag: '当前筛选',
      tone: 'cyan',
      note: '状态、金额和双方信息在一行内完成主扫描。'
    },
    {
      label: '待付款',
      value: `${pending}`,
      tag: pending > 0 ? '待跟进' : '正常',
      tone: pending > 0 ? 'yellow' : 'green',
      note: '待付款单提供快速取消入口，缩短处理路径。'
    },
    {
      label: '进行中',
      value: `${active}`,
      tag: '履约中',
      tone: 'magenta',
      note: '进行中订单在弹窗内突出时间区间与备注。'
    },
    {
      label: '已完成',
      value: `${completed}`,
      tag: '已收口',
      tone: 'green',
      note: '完成态降噪展示，避免影响处理中订单识别。'
    }
  ]
})

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'yellow',
    paid: 'cyan',
    in_progress: 'magenta',
    completed: 'green',
    cancelled: 'slate',
    refunded: 'red'
  }
  return classes[status] || 'slate'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待付款',
    paid: '已付款',
    in_progress: '进行中',
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退款'
  }
  return texts[status] || status
}

const getStatusBannerClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'yellow',
    paid: 'cyan',
    in_progress: 'cyan',
    completed: 'green',
    cancelled: 'slate',
    refunded: 'red'
  }
  return classes[status] || 'slate'
}

const handleSearch = () => {
  // 当前示例数据采用本地筛选，保留入口以便后续接入 API。
}

const resetFilters = () => {
  searchKeyword.value = ''
  searchType.value = ''
  searchStatus.value = ''
}

const viewOrder = (order: Order) => {
  currentOrder.value = order
  showDetail.value = true
}

const cancelOrder = (order: Order) => {
  if (window.confirm('确定要取消这个订单吗？')) {
    order.status = 'cancelled'
  }
}
</script>
