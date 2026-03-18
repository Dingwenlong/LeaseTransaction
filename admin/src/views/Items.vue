<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Items</p>
      <h1 class="page-title">物品审核、价格结构和校区归属放在同一张卡里。</h1>
      <p class="page-description">管理审核、价格和校区信息。</p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">清空筛选</button>
      <button class="button button-primary" @click="loadItems">
        <span>刷新物品</span>
        <span>↻</span>
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
        <p class="panel-subtitle">按条件筛选后直接审核。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid">
        <label class="field">
          <span class="field-label">关键词</span>
          <input v-model="searchKeyword" class="field-control" type="text" placeholder="搜索物品名称或描述" />
        </label>
        <label class="field">
          <span class="field-label">物品类型</span>
          <select v-model="filterType" class="field-control">
            <option value="">全部类型</option>
            <option :value="1">租赁</option>
            <option :value="2">出售</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">物品状态</span>
          <select v-model="filterStatus" class="field-control">
            <option value="">全部状态</option>
            <option :value="0">待审核</option>
            <option :value="1">已上架</option>
            <option :value="2">租赁中</option>
            <option :value="3">已售出</option>
            <option :value="4">已下架</option>
            <option :value="5">已驳回</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">所在校区</span>
          <input v-model="filterCampus" class="field-control" type="text" placeholder="输入校区名称" />
        </label>
      </div>
      <div class="toolbar-actions" style="margin-top: 16px;">
        <button class="button button-primary button-sm" @click="handleSearch">应用筛选</button>
      </div>
    </div>
  </section>

  <section class="card-grid">
    <article v-for="item in items" :key="item.id" class="resource-card">
      <div class="resource-head">
        <div class="avatar-line" style="align-items: center;">
          <div class="resource-visual" :style="{ background: item.type === 1 ? 'rgba(34, 211, 238, 0.14)' : 'rgba(236, 72, 153, 0.14)' }">
            {{ item.type === 1 ? '📦' : '💰' }}
          </div>
          <div class="truncate">
            <h3 class="resource-title truncate">{{ item.title }}</h3>
            <p class="table-secondary truncate">{{ item.ownerName }} · {{ item.category || '未分类' }}</p>
          </div>
        </div>
        <span class="status-pill" :class="getStatusClass(item.status)">{{ item.statusText }}</span>
      </div>

      <div v-if="item.coverImage" class="image-strip">
        <img class="image-thumb" :src="item.coverImage" :alt="item.title" />
      </div>

      <div>
        <p class="resource-description">{{ item.description || '暂无描述' }}</p>
      </div>

      <div class="resource-meta">
        <div>
          <div class="resource-price">
            ¥{{ item.price }}
            <small>{{ item.type === 1 ? '/天' : '/件' }}</small>
          </div>
          <p class="table-secondary" style="margin-top: 8px;">押金 {{ item.deposit ? `¥${item.deposit}` : '无' }}</p>
        </div>
        <span class="status-pill" :class="item.type === 1 ? 'cyan' : 'magenta'">{{ item.typeText }}</span>
      </div>

      <div class="resource-meta-row">
        <span class="mini-chip">📍 {{ item.campus || '未设置校区' }}</span>
        <span class="mini-chip">👁 {{ item.viewCount }} 浏览</span>
        <span class="mini-chip">❤️ {{ item.favoriteCount }} 收藏</span>
      </div>

      <div class="resource-footer">
        <div class="table-secondary">审核建议：{{ item.reviewHint }}</div>
        <div class="table-actions">
          <button class="button button-ghost button-sm" @click="viewItem(item)">查看详情</button>
          <button v-if="item.status === 0" class="button button-success button-sm" @click="approveItem(item.id)">通过</button>
          <button v-if="item.status === 0" class="button button-danger button-sm" @click="rejectItem(item.id)">驳回</button>
        </div>
      </div>
    </article>

    <article v-if="!loading && items.length === 0" class="panel" style="grid-column: 1 / -1;">
      <div class="empty-state">
        <strong>没有匹配的物品</strong>
        <span>调整筛选条件后可重新查看。</span>
      </div>
    </article>
  </section>

  <div v-if="showDetail && currentItem" class="modal-overlay" @click.self="showDetail = false">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">{{ currentItem.title }}</h2>
          <p class="modal-description">{{ currentItem.typeText }} · {{ currentItem.statusText }}</p>
        </div>
        <button class="modal-close" @click="showDetail = false">✕</button>
      </div>
      <div class="modal-body">
        <div class="status-banner" :class="getStatusClass(currentItem.status)">
          <p class="detail-label">审核建议</p>
          <p class="detail-value">{{ currentItem.reviewHint }}</p>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <p class="detail-label">发布者</p>
            <p class="detail-value">{{ currentItem.ownerName }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">校区</p>
            <p class="detail-value">{{ currentItem.campus || '-' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">价格</p>
            <p class="detail-value">¥{{ currentItem.price }}{{ currentItem.type === 1 ? '/天' : '' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">押金</p>
            <p class="detail-value">{{ currentItem.deposit ? `¥${currentItem.deposit}` : '无' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">浏览量</p>
            <p class="detail-value">{{ currentItem.viewCount }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">收藏量</p>
            <p class="detail-value">{{ currentItem.favoriteCount }}</p>
          </div>
        </div>

        <div class="detail-card">
          <p class="detail-label">详细描述</p>
          <p class="detail-value">{{ currentItem.description || '暂无描述' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { itemApi, type Item } from '../api'

const searchKeyword = ref('')
const filterType = ref<string | number>('')
const filterStatus = ref<string | number>('')
const filterCampus = ref('')
const loading = ref(false)
const items = ref<Item[]>([])
const total = ref(0)
const showDetail = ref(false)
const currentItem = ref<Item | null>(null)

const itemMetrics = computed(() => {
  const list = items.value
  const pending = list.filter((item) => item.status === 0).length
  const leaseCount = list.filter((item) => item.type === 1).length
  const averagePrice = list.length
    ? Math.round(list.reduce((sum, item) => sum + Number(item.price), 0) / list.length)
    : 0

  return [
    {
      label: '当前物品数',
      value: `${list.length}`,
      tag: `总量 ${total.value}`,
      tone: 'cyan',
      note: '审核卡片和管理动作已经与后端接口联动。'
    },
    {
      label: '待审核',
      value: `${pending}`,
      tag: pending > 0 ? '需处理' : '已清空',
      tone: pending > 0 ? 'yellow' : 'green',
      note: '待审核状态保留通过与驳回两个直接动作。'
    },
    {
      label: '租赁物品',
      value: `${leaseCount}`,
      tag: '可循环',
      tone: 'magenta',
      note: '租赁和出售共用统一卡片结构，但价格单位清晰区分。'
    },
    {
      label: '平均价格',
      value: `¥${averagePrice}`,
      tag: '当前筛选',
      tone: 'green',
      note: '金额信息在不同页面统一使用高亮展示。'
    }
  ]
})

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'yellow',
    1: 'green',
    2: 'cyan',
    3: 'magenta',
    4: 'slate',
    5: 'red'
  }
  return classes[status] || 'slate'
}

const loadItems = async () => {
  loading.value = true
  try {
    const res = await itemApi.getList({
      page: 1,
      size: 12,
      adminView: true,
      keyword: searchKeyword.value || undefined,
      type: filterType.value !== '' ? filterType.value : undefined,
      status: filterStatus.value !== '' ? filterStatus.value : undefined,
      campus: filterCampus.value || undefined
    })
    items.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载物品列表失败:', error)
    ElMessage.error('加载物品列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadItems()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = ''
  filterStatus.value = ''
  filterCampus.value = ''
  loadItems()
}

const viewItem = async (item: Item) => {
  currentItem.value = await itemApi.getDetail(item.id)
  showDetail.value = true
}

const approveItem = async (id: number) => {
  await itemApi.approve(id)
  ElMessage.success('物品已审核通过')
  loadItems()
}

const rejectItem = async (id: number) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回物品', {
      confirmButtonText: '确认驳回',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：图片不清晰、描述不完整'
    })
    await itemApi.reject(id, value || '')
    ElMessage.success('物品已驳回')
    loadItems()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('驳回失败:', error)
      ElMessage.error('驳回失败')
    }
  }
}

onMounted(() => {
  loadItems()
})
</script>
