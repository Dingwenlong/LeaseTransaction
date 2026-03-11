<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Users</p>
      <h1 class="page-title">用户信息、信用状态和操作入口放回同一视线。</h1>
      <p class="page-description">
        保留后台的未来感配色，但把列表视图改成更适合日常巡检的结构，先看用户状态，再看信用，再决定动作。
      </p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">
        <span>重置筛选</span>
      </button>
      <button class="button button-primary" @click="handleSearch">
        <span>刷新列表</span>
        <span>→</span>
      </button>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in userMetrics" :key="metric.label" class="metric-card">
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
        <p class="panel-subtitle">字段顺序与表格列顺序一致，减少筛选时的上下文切换。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid">
        <label class="field">
          <span class="field-label">关键词</span>
          <input
            v-model="searchKeyword"
            class="field-control"
            type="text"
            placeholder="用户名 / 昵称"
            @keyup.enter="handleSearch"
          />
        </label>
        <label class="field">
          <span class="field-label">用户状态</span>
          <select v-model="searchStatus" class="field-control">
            <option value="">全部状态</option>
            <option :value="1">正常</option>
            <option :value="0">禁用</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">信用分段</span>
          <select v-model="searchCredit" class="field-control">
            <option value="">全部信用</option>
            <option value="high">优秀 (≥80)</option>
            <option value="medium">良好 (60-79)</option>
            <option value="low">较差 (&lt;60)</option>
          </select>
        </label>
        <div class="field">
          <span class="field-label">当前页反馈</span>
          <div class="field-control" style="display: flex; align-items: center;">
            已展示 {{ visibleUsers.length }} 位用户
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">用户列表</h2>
        <p class="panel-subtitle">列表信息按“身份 - 信用 - 状态 - 操作”排列，更适合批量巡检。</p>
      </div>
      <span class="mini-chip">总计 {{ total }} 位</span>
    </div>

    <div class="table-scroll">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户信息</th>
            <th>校区</th>
            <th>信用分</th>
            <th>状态</th>
            <th>注册时间</th>
            <th style="text-align: right;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in visibleUsers" :key="user.id">
            <td>
              <span class="table-id">#{{ user.id }}</span>
            </td>
            <td>
              <div class="avatar-line">
                <div class="avatar-badge">👤</div>
                <div class="truncate">
                  <p class="table-primary truncate">{{ user.username }}</p>
                  <p class="table-secondary truncate">{{ user.nickname || '未设置昵称' }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="table-primary">{{ user.campus || '-' }}</p>
            </td>
            <td>
              <div style="display: flex; align-items: center; gap: 12px;">
                <div style="flex: 1; min-width: 72px; height: 10px; border-radius: 999px; background: rgba(148, 163, 184, 0.12); overflow: hidden;">
                  <div
                    :class="getCreditBarClass(user.creditScore)"
                    :style="{ width: `${Math.max(0, Math.min(100, user.creditScore))}%`, height: '100%', borderRadius: '999px' }"
                  ></div>
                </div>
                <strong :class="getCreditClass(user.creditScore)">{{ user.creditScore }}</strong>
              </div>
            </td>
            <td>
              <span class="status-pill" :class="user.status === 1 ? 'green' : 'red'">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>
              <p class="table-primary">{{ formatDate(user.createdAt) }}</p>
              <p class="table-secondary">最近更新 {{ formatDate(user.updatedAt) }}</p>
            </td>
            <td>
              <div class="table-actions" style="justify-content: flex-end;">
                <button class="button button-ghost button-sm" @click="viewUser(user)">
                  查看
                </button>
                <button
                  class="button button-sm"
                  :class="user.status === 1 ? 'button-danger' : 'button-success'"
                  @click="toggleStatus(user)"
                >
                  {{ user.status === 1 ? '禁用' : '启用' }}
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="loading">
            <td colspan="7">
              <div class="empty-state">
                <strong>正在加载用户数据</strong>
                <span>后台正在同步当前页用户信息。</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="visibleUsers.length === 0">
            <td colspan="7">
              <div class="empty-state">
                <strong>当前条件下没有用户</strong>
                <span>可以重置筛选条件后重新加载。</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-bar">
      <div class="pagination-info">
        当前页 {{ currentPage }}，显示 {{ visibleUsers.length }} 条，后台总记录 {{ total }} 条
      </div>
      <div class="pagination-controls">
        <button
          class="button button-ghost button-sm"
          :disabled="currentPage === 1"
          @click="prevPage"
        >
          上一页
        </button>
        <button
          v-for="page in displayedPages"
          :key="page"
          class="pagination-page"
          :class="{ active: currentPage === page }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
        <button
          class="button button-ghost button-sm"
          :disabled="currentPage === totalPages || totalPages === 0"
          @click="nextPage"
        >
          下一页
        </button>
      </div>
    </div>
  </section>

  <div v-if="showDetail && currentUser" class="modal-overlay" @click.self="showDetail = false">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">{{ currentUser.username }}</h2>
          <p class="modal-description">用户详情与信用概况</p>
        </div>
        <button class="modal-close" @click="showDetail = false">✕</button>
      </div>

      <div class="modal-body">
        <div class="status-banner" :class="currentUser.status === 1 ? 'green' : 'red'">
          <p class="detail-label">账号状态</p>
          <p class="detail-value">{{ currentUser.status === 1 ? '用户状态正常，可参与交易。' : '该用户已被限制使用，需要人工复核。' }}</p>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <p class="detail-label">用户 ID</p>
            <p class="detail-value">{{ currentUser.id }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">昵称</p>
            <p class="detail-value">{{ currentUser.nickname || '未设置昵称' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">手机号</p>
            <p class="detail-value">{{ currentUser.phone || '-' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">邮箱</p>
            <p class="detail-value">{{ currentUser.email || '-' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">校区</p>
            <p class="detail-value">{{ currentUser.campus || '-' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">信用分</p>
            <p class="detail-value">{{ currentUser.creditScore }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">注册时间</p>
            <p class="detail-value">{{ formatDate(currentUser.createdAt) }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">更新时间</p>
            <p class="detail-value">{{ formatDate(currentUser.updatedAt) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi, type User } from '../api'

const searchKeyword = ref('')
const searchStatus = ref<string | number>('')
const searchCredit = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const showDetail = ref(false)
const currentUser = ref<User | null>(null)

const users = ref<User[]>([])
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const displayedPages = computed(() => {
  const pages: number[] = []
  const maxDisplay = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxDisplay / 2))
  let end = Math.min(totalPages.value, start + maxDisplay - 1)

  if (end - start + 1 < maxDisplay) {
    start = Math.max(1, end - maxDisplay + 1)
  }

  for (let index = start; index <= end; index += 1) {
    pages.push(index)
  }

  return pages
})

const visibleUsers = computed(() => {
  return users.value.filter((user) => {
    if (searchCredit.value === 'high') return user.creditScore >= 80
    if (searchCredit.value === 'medium') return user.creditScore >= 60 && user.creditScore < 80
    if (searchCredit.value === 'low') return user.creditScore < 60
    return true
  })
})

const userMetrics = computed(() => {
  const list = visibleUsers.value
  const activeCount = list.filter((user) => user.status === 1).length
  const excellentCount = list.filter((user) => user.creditScore >= 80).length
  const averageCredit = list.length
    ? Math.round(list.reduce((sum, user) => sum + user.creditScore, 0) / list.length)
    : 0

  return [
    {
      label: '当前页用户',
      value: `${list.length}`,
      tag: '可巡检',
      tone: 'cyan',
      note: '当前页数据保持结构统一，支持快速横向比较。'
    },
    {
      label: '正常状态',
      value: `${activeCount}`,
      tag: activeCount === list.length && list.length > 0 ? '稳定' : '需关注',
      tone: activeCount === list.length && list.length > 0 ? 'green' : 'yellow',
      note: '用户状态标签与操作按钮颜色保持一致，避免误判。'
    },
    {
      label: '高信用用户',
      value: `${excellentCount}`,
      tag: '≥ 80',
      tone: 'magenta',
      note: '优秀信用用户单独聚焦，便于运营做激励和回访。'
    },
    {
      label: '平均信用分',
      value: `${averageCredit}`,
      tag: averageCredit >= 80 ? '优秀' : averageCredit >= 60 ? '良好' : '偏低',
      tone: averageCredit >= 80 ? 'green' : averageCredit >= 60 ? 'yellow' : 'red',
      note: '把信用柱和数值放在同一格，识别更直接。'
    }
  ]
})

const getCreditClass = (score: number) => {
  if (score >= 80) return 'credit-good'
  if (score >= 60) return 'credit-warn'
  return 'credit-danger'
}

const getCreditBarClass = (score: number) => {
  if (score >= 80) return 'credit-bar-good'
  if (score >= 60) return 'credit-bar-warn'
  return 'credit-bar-danger'
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value !== '' ? searchStatus.value : undefined
    }
    const res = await userApi.getList(params) as any
    users.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const resetFilters = () => {
  searchKeyword.value = ''
  searchStatus.value = ''
  searchCredit.value = ''
  currentPage.value = 1
  loadUsers()
}

const viewUser = (user: User) => {
  currentUser.value = user
  showDetail.value = true
}

const toggleStatus = async (user: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要${user.status === 1 ? '禁用' : '启用'}该用户吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await userApi.updateStatus(user.id, user.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const goToPage = (page: number) => {
  currentPage.value = page
  loadUsers()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value -= 1
    loadUsers()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value += 1
    loadUsers()
  }
}

onMounted(() => {
  loadUsers()
})
</script>
