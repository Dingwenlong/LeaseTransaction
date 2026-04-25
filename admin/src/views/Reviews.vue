<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Reviews</p>
      <h1 class="page-title">查看平台所有评价记录，关注差评和纠纷反馈。</h1>
      <p class="page-description">按评分和关键词筛选评价。</p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">重置条件</button>
      <button class="button button-primary" @click="loadReviews">
        <span>刷新评价</span>
        <span>↻</span>
      </button>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in reviewMetrics" :key="metric.label" class="metric-card">
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
        <h2 class="panel-title">评价筛选</h2>
        <p class="panel-subtitle">按评分和关键词筛选评价记录。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid">
        <label class="field">
          <span class="field-label">评分筛选</span>
          <select v-model="searchRating" class="field-control">
            <option value="">全部评分</option>
            <option :value="5">5 星 - 好评</option>
            <option :value="4">4 星 - 较好</option>
            <option :value="3">3 星 - 中评</option>
            <option :value="2">2 星 - 较差</option>
            <option :value="1">1 星 - 差评</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">订单 ID</span>
          <input v-model="searchOrderId" class="field-control" type="text" placeholder="输入订单 ID" @keyup.enter="loadReviews" />
        </label>
        <label class="field">
          <span class="field-label">评价人 ID</span>
          <input v-model="searchReviewerId" class="field-control" type="text" placeholder="输入评价人 ID" @keyup.enter="loadReviews" />
        </label>
        <div class="field">
          <span class="field-label">当前结果</span>
          <div class="field-control field-static">共 {{ total }} 条评价</div>
        </div>
      </div>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">评价列表</h2>
        <p class="panel-subtitle">查看评分、内容和双方信息。</p>
      </div>
      <span class="mini-chip">平均 {{ avgRating }} 星</span>
    </div>

    <div class="table-scroll">
      <table class="data-table">
        <thead>
          <tr>
            <th>评价 ID</th>
            <th>订单 / 物品</th>
            <th>评价人</th>
            <th>被评价人</th>
            <th>评分</th>
            <th>评价内容</th>
            <th>时间</th>
            <th style="text-align: right;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in reviews" :key="review.id">
            <td><span class="table-id">{{ review.id }}</span></td>
            <td>
              <p class="table-primary">{{ review.itemTitle }}</p>
              <p class="table-secondary">订单 ID {{ review.orderId }}</p>
            </td>
            <td>
              <p class="table-primary">{{ review.reviewerName }}</p>
              <p class="table-secondary" v-if="review.isAnonymous === 1">匿名评价</p>
            </td>
            <td>
              <p class="table-primary">{{ review.revieweeName }}</p>
            </td>
            <td>
              <span class="status-pill" :class="getRatingClass(review.rating)">{{ review.rating }} 星</span>
            </td>
            <td>
              <p class="table-primary truncate-cell" :title="review.content">{{ review.content }}</p>
            </td>
            <td>
              <p class="table-primary">{{ formatDate(review.createdAt) }}</p>
            </td>
            <td>
              <div class="table-actions" style="justify-content: flex-end;">
                <button class="button button-ghost button-sm" @click="viewReview(review)">详情</button>
              </div>
            </td>
          </tr>
          <tr v-if="loading">
            <td colspan="8">
              <div class="empty-state">
                <strong>正在加载评价数据</strong>
                <span>后台正在同步评价信息。</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="reviews.length === 0">
            <td colspan="8">
              <div class="empty-state">
                <strong>暂无评价数据</strong>
                <span>当前筛选条件下没有评价记录。</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

  <div v-if="showDetail && currentReview" class="modal-overlay" @click.self="showDetail = false">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">评价详情</h2>
          <p class="modal-description">评价 ID {{ currentReview.id }}</p>
        </div>
        <button class="modal-close" @click="showDetail = false">✕</button>
      </div>
      <div class="modal-body">
        <div class="status-banner" :class="getRatingClass(currentReview.rating)">
          <p class="detail-label">评分</p>
          <p class="detail-value">{{ currentReview.rating }} 星 · {{ getRatingLabel(currentReview.rating) }}</p>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <p class="detail-label">物品名称</p>
            <p class="detail-value">{{ currentReview.itemTitle }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">订单 ID</p>
            <p class="detail-value">{{ currentReview.orderId }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">评价人</p>
            <p class="detail-value">{{ currentReview.reviewerName }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">被评价人</p>
            <p class="detail-value">{{ currentReview.revieweeName }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">是否匿名</p>
            <p class="detail-value">{{ currentReview.isAnonymous === 1 ? '是' : '否' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">评价时间</p>
            <p class="detail-value">{{ formatDate(currentReview.createdAt) }}</p>
          </div>
        </div>

        <div class="detail-card">
          <p class="detail-label">评价内容</p>
          <p class="detail-value">{{ currentReview.content }}</p>
        </div>

        <div class="detail-card" v-if="currentReview.images">
          <p class="detail-label">评价图片</p>
          <div class="review-images">
            <img v-for="(img, idx) in parseImages(currentReview.images)" :key="idx" :src="img" class="review-image" alt="评价图片" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { reviewApi, type Review } from '../api'

const searchRating = ref<string | number>('')
const searchOrderId = ref('')
const searchReviewerId = ref('')
const loading = ref(false)
const reviews = ref<Review[]>([])
const total = ref(0)
const showDetail = ref(false)
const currentReview = ref<Review | null>(null)

const avgRating = computed(() => {
  if (reviews.value.length === 0) return '-'
  const sum = reviews.value.reduce((s, r) => s + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const reviewMetrics = computed(() => {
  const list = reviews.value
  const good = list.filter((r) => r.rating >= 4).length
  const mid = list.filter((r) => r.rating === 3).length
  const bad = list.filter((r) => r.rating <= 2).length
  const anon = list.filter((r) => r.isAnonymous === 1).length
  return [
    {
      label: '评价总数',
      value: `${total.value}`,
      tag: '当前筛选',
      tone: 'cyan',
      note: '展示当前筛选条件下的评价总数。'
    },
    {
      label: '好评 (4-5星)',
      value: `${good}`,
      tag: good > 0 ? '优质' : '暂无',
      tone: 'green',
      note: '好评率反映平台服务质量。'
    },
    {
      label: '中差评 (1-3星)',
      value: `${mid + bad}`,
      tag: bad > 0 ? '需关注' : '正常',
      tone: bad > 0 ? 'red' : 'slate',
      note: '差评需及时关注并处理。'
    },
    {
      label: '匿名评价',
      value: `${anon}`,
      tag: anon > 0 ? '含匿名' : '全部实名',
      tone: anon > 0 ? 'violet' : 'green',
      note: '匿名评价不展示评价人信息。'
    }
  ]
})

const getRatingClass = (rating: number) => {
  if (rating >= 4) return 'green'
  if (rating === 3) return 'yellow'
  return 'red'
}

const getRatingLabel = (rating: number) => {
  if (rating === 5) return '非常满意'
  if (rating === 4) return '比较满意'
  if (rating === 3) return '一般'
  if (rating === 2) return '不太满意'
  return '非常不满意'
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

const parseImages = (images: string) => {
  if (!images) return []
  return images.split(',').filter((s) => s.trim())
}

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await reviewApi.getAdminList({
      page: 1,
      size: 50,
      rating: searchRating.value !== '' ? searchRating.value : undefined,
      orderId: searchOrderId.value ? searchOrderId.value : undefined,
      reviewerId: searchReviewerId.value ? searchReviewerId.value : undefined
    })
    reviews.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载评价失败:', error)
    ElMessage.error('加载评价失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  searchRating.value = ''
  searchOrderId.value = ''
  searchReviewerId.value = ''
  loadReviews()
}

const viewReview = (review: Review) => {
  currentReview.value = review
  showDetail.value = true
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.truncate-cell {
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 4px;
}

.review-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid var(--border-subtle);
}
</style>
