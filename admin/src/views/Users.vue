<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Users</p>
      <h1 class="page-title">用户审核、校区身份和信用状态放回同一视线。</h1>
      <p class="page-description">管理用户状态、认证和信用。</p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">重置筛选</button>
      <button class="button button-primary" @click="handleSearch">
        <span>刷新列表</span>
        <span>↻</span>
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
        <p class="panel-subtitle">按状态和认证筛选。</p>
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
            placeholder="学号 / 昵称 / 校区"
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
          <span class="field-label">认证状态</span>
          <select v-model="searchVerified" class="field-control">
            <option value="">全部</option>
            <option :value="1">已认证</option>
            <option :value="0">未认证</option>
          </select>
        </label>
        <div class="field">
          <span class="field-label">当前结果</span>
          <div class="field-control field-static">已展示 {{ users.length }} 位用户</div>
        </div>
      </div>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">用户列表</h2>
        <p class="panel-subtitle">查看校区、认证和信用。</p>
      </div>
      <span class="mini-chip">总计 {{ total }} 位</span>
    </div>

    <div class="table-scroll">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>身份信息</th>
            <th>校区 / 院系</th>
            <th>信用分</th>
            <th>认证</th>
            <th>状态</th>
            <th>注册时间</th>
            <th style="text-align: right;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td><span class="table-id">#{{ user.id }}</span></td>
            <td>
              <div class="avatar-line">
                <div class="avatar-badge">👤</div>
                <div class="truncate">
                  <p class="table-primary truncate">{{ user.nickname || '校园用户' }}</p>
                  <p class="table-secondary truncate">{{ user.studentId || user.username }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="table-primary">{{ user.campus || '-' }}</p>
              <p class="table-secondary">{{ user.department || '未填写院系' }}</p>
            </td>
            <td>
              <div class="credit-line">
                <div class="credit-track">
                  <div class="credit-fill" :class="getCreditBarClass(user.creditScore)" :style="{ width: `${Math.min(100, user.creditScore)}%` }"></div>
                </div>
                <strong :class="getCreditClass(user.creditScore)">{{ user.creditScore }}</strong>
              </div>
            </td>
            <td>
              <span class="status-pill" :class="user.isVerified === 1 ? 'green' : 'yellow'">
                {{ user.isVerified === 1 ? '已认证' : '未认证' }}
              </span>
            </td>
            <td>
              <span class="status-pill" :class="user.status === 1 ? 'cyan' : 'red'">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>
              <p class="table-primary">{{ formatDate(user.createdAt) }}</p>
              <p class="table-secondary">更新 {{ formatDate(user.updatedAt) }}</p>
            </td>
            <td>
              <div class="table-actions" style="justify-content: flex-end;">
                <button class="button button-ghost button-sm" @click="viewUser(user)">查看</button>
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
            <td colspan="8">
              <div class="empty-state">
                <strong>正在加载用户数据</strong>
                <span>后台正在同步当前页信息。</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="users.length === 0">
            <td colspan="8">
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
        当前页 {{ currentPage }}，显示 {{ users.length }} 条，后台总记录 {{ total }} 条
      </div>
      <div class="pagination-controls">
        <button class="button button-ghost button-sm" :disabled="currentPage === 1" @click="prevPage">上一页</button>
        <button
          v-for="page in displayedPages"
          :key="page"
          class="pagination-page"
          :class="{ active: currentPage === page }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
        <button class="button button-ghost button-sm" :disabled="currentPage === totalPages || totalPages === 0" @click="nextPage">下一页</button>
      </div>
    </div>
  </section>

  <div v-if="showDetail && currentUser" class="modal-overlay" @click.self="showDetail = false">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">{{ currentUser.nickname || currentUser.username }}</h2>
          <p class="modal-description">用户详情与校园身份信息</p>
        </div>
        <button class="modal-close" @click="showDetail = false">✕</button>
      </div>

      <div class="modal-body">
        <div class="status-banner" :class="currentUser.status === 1 ? 'cyan' : 'red'">
          <p class="detail-label">账号状态</p>
          <p class="detail-value">
            {{ currentUser.status === 1 ? '账号可正常参与租赁与交易流程。' : '账号已被限制使用，需要人工复核。' }}
          </p>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <p class="detail-label">学号</p>
            <p class="detail-value">{{ currentUser.studentId || currentUser.username }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">昵称</p>
            <p class="detail-value">{{ currentUser.nickname || '未设置昵称' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">校区</p>
            <p class="detail-value">{{ currentUser.campus || '-' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">院系</p>
            <p class="detail-value">{{ currentUser.department || '-' }}</p>
          </div>
          <div class="detail-card">
            <p class="detail-label">校园认证</p>
            <p class="detail-value">{{ currentUser.isVerified === 1 ? '已完成实名认证和校园核验' : '尚未完成校园核验' }}</p>
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
const searchVerified = ref<string | number>('')
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

const userMetrics = computed(() => {
  const list = users.value
  const activeCount = list.filter((user) => user.status === 1).length
  const verifiedCount = list.filter((user) => user.isVerified === 1).length
  const averageCredit = list.length
    ? Math.round(list.reduce((sum, user) => sum + user.creditScore, 0) / list.length)
    : 0

  return [
    {
      label: '当前页用户',
      value: `${list.length}`,
      tag: '可巡检',
      tone: 'cyan',
      note: '当前页数据已接入真实接口，支持快速横向比较。'
    },
    {
      label: '正常状态',
      value: `${activeCount}`,
      tag: activeCount === list.length && list.length > 0 ? '稳定' : '需关注',
      tone: activeCount === list.length && list.length > 0 ? 'green' : 'yellow',
      note: '将禁用账号及时与正常账号区分，减少误操作。'
    },
    {
      label: '已认证用户',
      value: `${verifiedCount}`,
      tag: '校园核验',
      tone: verifiedCount > 0 ? 'magenta' : 'slate',
      note: '校园认证数据直接决定信用免押与风控策略。'
    },
    {
      label: '平均信用分',
      value: `${averageCredit}`,
      tag: averageCredit >= 100 ? '优秀' : averageCredit >= 80 ? '良好' : '偏低',
      tone: averageCredit >= 100 ? 'green' : averageCredit >= 80 ? 'yellow' : 'red',
      note: '信用条和分值同位呈现，更利于巡检。'
    }
  ]
})

const getCreditClass = (score: number) => {
  if (score >= 100) return 'credit-good'
  if (score >= 80) return 'credit-warn'
  return 'credit-danger'
}

const getCreditBarClass = (score: number) => {
  if (score >= 100) return 'credit-bar-good'
  if (score >= 80) return 'credit-bar-warn'
  return 'credit-bar-danger'
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value !== '' ? searchStatus.value : undefined,
      verified: searchVerified.value !== '' ? searchVerified.value : undefined
    })
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
  searchVerified.value = ''
  currentPage.value = 1
  loadUsers()
}

const viewUser = (user: User) => {
  currentUser.value = user
  showDetail.value = true
}

const toggleStatus = async (user: User) => {
  try {
    await ElMessageBox.confirm(`确定要${user.status === 1 ? '禁用' : '启用'}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
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
