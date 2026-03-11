<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Items</p>
      <h1 class="page-title">物品陈列、审核状态和价格信息需要同样整齐。</h1>
      <p class="page-description">
        物品页改成以卡片为主的审核视图：顶部先看状态和价格，中间看标题与描述，底部再安排动作，避免杂讯把重点冲散。
      </p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">清空筛选</button>
      <button class="button button-primary">
        <span>批量审核</span>
        <span>✓</span>
      </button>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in itemMetrics" :key="metric.label" class="metric-card">
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
        <h2 class="panel-title">筛选工具栏</h2>
        <p class="panel-subtitle">让筛选字段和卡片字段一一对应，审核时不需要来回切换视角。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid">
        <label class="field">
          <span class="field-label">关键词</span>
          <input v-model="searchKeyword" class="field-control" type="text" placeholder="搜索物品名称" />
        </label>
        <label class="field">
          <span class="field-label">物品类型</span>
          <select v-model="filterType" class="field-control">
            <option value="">全部类型</option>
            <option value="1">租赁</option>
            <option value="2">出售</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">物品状态</span>
          <select v-model="filterStatus" class="field-control">
            <option value="">全部状态</option>
            <option value="0">待审核</option>
            <option value="1">已上架</option>
            <option value="2">已租出</option>
            <option value="3">已售出</option>
          </select>
        </label>
        <div class="field">
          <span class="field-label">筛选结果</span>
          <div class="field-control" style="display: flex; align-items: center;">
            当前展示 {{ filteredItems.length }} 个物品
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="card-grid">
    <article v-for="item in filteredItems" :key="item.id" class="resource-card">
      <div class="resource-head">
        <div class="avatar-line" style="align-items: center;">
          <div class="resource-visual" :style="{ background: item.type === 1 ? 'rgba(34, 211, 238, 0.14)' : 'rgba(236, 72, 153, 0.14)' }">
            {{ item.icon }}
          </div>
          <div class="truncate">
            <h3 class="resource-title truncate">{{ item.title }}</h3>
            <p class="table-secondary truncate">{{ item.ownerName }} · {{ item.category }}</p>
          </div>
        </div>
        <span class="status-pill" :class="getStatusClass(item.status)">
          {{ getStatusText(item.status) }}
        </span>
      </div>

      <div>
        <p class="resource-description">{{ item.description }}</p>
      </div>

      <div class="resource-meta">
        <div>
          <div class="resource-price">
            ¥{{ item.price }}
            <small v-if="item.type === 1">/天</small>
          </div>
          <p class="table-secondary" style="margin-top: 8px;">押金 {{ item.deposit ? `¥${item.deposit}` : '无' }}</p>
        </div>
        <span class="status-pill" :class="item.type === 1 ? 'cyan' : 'magenta'">
          {{ item.type === 1 ? '租赁' : '出售' }}
        </span>
      </div>

      <div class="resource-meta-row">
        <span class="mini-chip">📍 {{ item.campus }}</span>
        <span class="mini-chip">👁 {{ item.viewCount }} 浏览</span>
        <span class="mini-chip">更新时间 {{ item.updatedAt }}</span>
      </div>

      <div class="resource-footer">
        <div class="table-secondary">审核建议：{{ item.reviewHint }}</div>
        <div class="table-actions">
          <button class="button button-ghost button-sm">查看详情</button>
          <button v-if="item.status === 0" class="button button-success button-sm">通过</button>
          <button v-if="item.status === 0" class="button button-danger button-sm">驳回</button>
        </div>
      </div>
    </article>

    <article v-if="filteredItems.length === 0" class="panel" style="grid-column: 1 / -1;">
      <div class="empty-state">
        <strong>没有匹配的物品</strong>
        <span>调整筛选条件后可重新查看。</span>
      </div>
    </article>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface ItemCard {
  id: number
  title: string
  description: string
  category: string
  type: number
  status: number
  price: number
  deposit: number | null
  campus: string
  ownerName: string
  viewCount: number
  updatedAt: string
  icon: string
  reviewHint: string
}

const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')

const items = ref<ItemCard[]>([
  {
    id: 1,
    title: '考研复习资料全套',
    description: '包含数学、英语、政治全套复习资料，笔记详细，重点突出，适合考前冲刺和阶段复盘。',
    category: '书籍资料',
    type: 2,
    status: 1,
    price: 150,
    deposit: null,
    campus: '东校区',
    ownerName: '张同学',
    viewCount: 256,
    updatedAt: '03-08 14:20',
    icon: '📚',
    reviewHint: '封面齐全、描述完整，可以继续保持曝光。'
  },
  {
    id: 2,
    title: '山地自行车',
    description: '美利达山地车，9 成新，适合校园骑行和周末短途出游，车况稳定。',
    category: '运动器材',
    type: 1,
    status: 1,
    price: 30,
    deposit: 500,
    campus: '西校区',
    ownerName: '李同学',
    viewCount: 189,
    updatedAt: '03-09 09:10',
    icon: '🚲',
    reviewHint: '押金与租金展示清楚，建议补充更近的车况照片。'
  },
  {
    id: 3,
    title: '专业计算器',
    description: '卡西欧 fx-991CN X，考试常用型号，功能完好，适合考前短期交易。',
    category: '电子产品',
    type: 2,
    status: 0,
    price: 120,
    deposit: null,
    campus: '南校区',
    ownerName: '王同学',
    viewCount: 87,
    updatedAt: '03-10 16:40',
    icon: '🧮',
    reviewHint: '待补充真实图片和成色说明。'
  },
  {
    id: 4,
    title: '露营帐篷',
    description: '双人帐篷，防水防风，适合 2-3 人露营使用，租期灵活。',
    category: '生活用品',
    type: 1,
    status: 2,
    price: 40,
    deposit: 300,
    campus: '东校区',
    ownerName: '赵同学',
    viewCount: 312,
    updatedAt: '03-10 11:25',
    icon: '⛺',
    reviewHint: '正在租赁中，建议维持当前排序。'
  },
  {
    id: 5,
    title: '数码相机',
    description: '佳能 EOS M50，入门级微单，拍照清晰，配件齐全，适合社团活动和旅行记录。',
    category: '电子产品',
    type: 1,
    status: 1,
    price: 80,
    deposit: 1500,
    campus: '西校区',
    ownerName: '陈同学',
    viewCount: 445,
    updatedAt: '03-11 08:35',
    icon: '📷',
    reviewHint: '高价值物品，建议在详情里突出押金与交接规则。'
  },
  {
    id: 6,
    title: '羽毛球拍套装',
    description: '尤尼克斯羽毛球拍 2 支，含球包和羽毛球，适合新手直接使用。',
    category: '运动器材',
    type: 2,
    status: 3,
    price: 280,
    deposit: null,
    campus: '东校区',
    ownerName: '林同学',
    viewCount: 178,
    updatedAt: '03-06 19:20',
    icon: '🏸',
    reviewHint: '已完成售出，可降级排序但保留案例价值。'
  }
])

const filteredItems = computed(() => {
  return items.value.filter((item) => {
    const keyword = searchKeyword.value.trim().toLowerCase()
    const matchesKeyword =
      keyword.length === 0 ||
      item.title.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword)
    const matchesType = filterType.value === '' || String(item.type) === filterType.value
    const matchesStatus = filterStatus.value === '' || String(item.status) === filterStatus.value
    return matchesKeyword && matchesType && matchesStatus
  })
})

const itemMetrics = computed(() => {
  const list = filteredItems.value
  const pending = list.filter((item) => item.status === 0).length
  const leaseCount = list.filter((item) => item.type === 1).length
  const averagePrice = list.length
    ? Math.round(list.reduce((sum, item) => sum + item.price, 0) / list.length)
    : 0

  return [
    {
      label: '当前物品数',
      value: `${list.length}`,
      tag: '已过滤',
      tone: 'cyan',
      note: '卡片间距和信息顺序已经统一，适合批量审核。'
    },
    {
      label: '待审核',
      value: `${pending}`,
      tag: pending > 0 ? '需处理' : '已清空',
      tone: pending > 0 ? 'yellow' : 'green',
      note: '待审核状态只保留关键动作，避免误操作。'
    },
    {
      label: '租赁物品',
      value: `${leaseCount}`,
      tag: '可循环',
      tone: 'magenta',
      note: '租赁和出售共用同一套卡片，但价格单位区别展示。'
    },
    {
      label: '平均价格',
      value: `¥${averagePrice}`,
      tag: '当前筛选',
      tone: 'green',
      note: '金额信息采用统一强调色，跨页面识别更稳定。'
    }
  ]
})

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'yellow',
    1: 'green',
    2: 'cyan',
    3: 'violet'
  }
  return classes[status] || 'slate'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待审核',
    1: '已上架',
    2: '已租出',
    3: '已售出'
  }
  return texts[status] || '未知状态'
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = ''
  filterStatus.value = ''
}
</script>
