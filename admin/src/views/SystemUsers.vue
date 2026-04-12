<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">System Users</p>
      <h1 class="page-title">后台登录账号、角色权限和运维状态收回统一控制台。</h1>
      <p class="page-description">管理后台账号、角色和状态。</p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="resetFilters">重置筛选</button>
      <button class="button button-secondary" @click="loadSystemUsers">刷新列表</button>
      <button class="button button-primary" @click="openCreateModal">新增账号</button>
    </div>
  </section>

  <section class="metric-grid">
    <article v-for="metric in metrics" :key="metric.label" class="metric-card">
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
        <p class="panel-subtitle">按账号和状态筛选。</p>
      </div>
    </div>
    <div class="panel-body">
      <div class="toolbar-grid three">
        <label class="field">
          <span class="field-label">关键词</span>
          <input
            v-model.trim="searchKeyword"
            class="field-control"
            type="text"
            placeholder="用户名 / 显示名"
            @keyup.enter="handleSearch"
          />
        </label>
        <label class="field">
          <span class="field-label">账号状态</span>
          <select v-model="searchStatus" class="field-control">
            <option value="">全部状态</option>
            <option :value="1">正常</option>
            <option :value="0">禁用</option>
          </select>
        </label>
        <div class="field">
          <span class="field-label">当前结果</span>
          <div class="field-control field-static">已展示 {{ systemUsers.length }} 个系统账号</div>
        </div>
      </div>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <div>
        <h2 class="panel-title">系统用户列表</h2>
        <p class="panel-subtitle">查看角色、状态和登录时间。</p>
      </div>
      <span class="mini-chip">总计 {{ total }} 个</span>
    </div>

    <div class="table-scroll">
      <table class="data-table">
        <thead>
          <tr>
            <th>账号ID</th>
            <th>登录账号</th>
            <th>角色</th>
            <th>状态</th>
            <th>最近登录</th>
            <th>创建 / 更新</th>
            <th style="text-align: right;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in systemUsers" :key="user.id">
            <td><span class="table-id">#{{ user.id }}</span></td>
            <td>
              <div class="avatar-line">
                <div class="avatar-badge">🧩</div>
                <div class="truncate">
                  <p class="table-primary truncate">{{ user.displayName }}</p>
                  <p class="table-secondary truncate">{{ user.username }}</p>
                </div>
              </div>
            </td>
            <td>
              <span class="status-pill" :class="getRoleTone(user.role)">
                {{ getRoleLabel(user.role) }}
              </span>
            </td>
            <td>
              <div class="badge-group">
                <span class="status-pill" :class="user.status === 1 ? 'green' : 'red'">
                  {{ user.status === 1 ? '正常' : '禁用' }}
                </span>
                <span v-if="user.id === authState.user?.id" class="status-pill violet">当前账号</span>
              </div>
            </td>
            <td>
              <p class="table-primary">{{ formatDate(user.lastLoginTime) }}</p>
              <p class="table-secondary">{{ user.lastLoginTime ? '已记录最近登录' : '尚未登录' }}</p>
            </td>
            <td>
              <p class="table-primary">{{ formatDate(user.createdAt) }}</p>
              <p class="table-secondary">更新 {{ formatDate(user.updatedAt) }}</p>
            </td>
            <td>
              <div class="table-actions" style="justify-content: flex-end;">
                <button class="button button-ghost button-sm" @click="openEditModal(user)">编辑</button>
                <button class="button button-secondary button-sm" @click="openResetPasswordModal(user)">重置密码</button>
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
                <strong>正在加载系统用户</strong>
                <span>后台正在同步账号列表和授权状态。</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="systemUsers.length === 0">
            <td colspan="7">
              <div class="empty-state">
                <strong>当前筛选条件下没有系统账号</strong>
                <span>可以重置筛选或新增后台账号。</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-bar">
      <div class="pagination-info">
        当前页 {{ currentPage }}，显示 {{ systemUsers.length }} 条，后台总记录 {{ total }} 条
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

  <div v-if="showFormModal" class="modal-overlay" @click.self="closeFormModal">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">{{ formMode === 'create' ? '新增系统用户' : '编辑系统用户' }}</h2>
          <p class="modal-description">
            {{ formMode === 'create' ? '创建后台登录账号并配置初始角色与密码。' : '调整显示名称和角色，不影响现有登录态。' }}
          </p>
        </div>
        <button class="modal-close" @click="closeFormModal">✕</button>
      </div>

      <div class="modal-body">
        <div class="form-grid">
          <label class="field">
            <span class="field-label">用户名</span>
            <input
              v-model.trim="form.username"
              class="field-control"
              type="text"
              :disabled="formMode === 'edit'"
              placeholder="例如 admin.ops"
            />
            <span class="field-hint">用户名用于后台登录，编辑模式下不可修改。</span>
          </label>

          <label class="field">
            <span class="field-label">显示名称</span>
            <input
              v-model.trim="form.displayName"
              class="field-control"
              type="text"
              placeholder="例如 运营主管"
            />
          </label>

          <label class="field">
            <span class="field-label">角色</span>
            <select v-model="form.role" class="field-control">
              <option value="SUPER_ADMIN">超级管理员</option>
              <option value="OPERATOR">运营账号</option>
            </select>
          </label>

          <label v-if="formMode === 'create'" class="field">
            <span class="field-label">初始密码</span>
            <input
              v-model="form.password"
              class="field-control"
              type="password"
              placeholder="至少 6 位"
            />
          </label>
        </div>

        <div class="inline-actions">
          <span class="muted">建议将超级管理员控制在少数账号内，日常运营可使用运营账号。</span>
          <div class="table-actions">
            <button class="button button-ghost" @click="closeFormModal">取消</button>
            <button class="button button-primary" :disabled="submitting" @click="submitForm">
              {{ submitting ? '提交中...' : formMode === 'create' ? '创建账号' : '保存修改' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showPasswordModal" class="modal-overlay" @click.self="closePasswordModal">
    <div class="modal-container">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">重置系统用户密码</h2>
          <p class="modal-description">重置后请通过安全渠道告知账号持有人新密码。</p>
        </div>
        <button class="modal-close" @click="closePasswordModal">✕</button>
      </div>

      <div class="modal-body">
        <div class="status-banner cyan">
          <p class="detail-label">目标账号</p>
          <p class="detail-value">
            {{ passwordTarget?.displayName }} / {{ passwordTarget?.username }}
          </p>
        </div>

        <div class="form-grid">
          <label class="field">
            <span class="field-label">新密码</span>
            <input
              v-model="passwordForm.password"
              class="field-control"
              type="password"
              placeholder="请输入新密码"
            />
          </label>
          <label class="field">
            <span class="field-label">确认密码</span>
            <input
              v-model="passwordForm.confirmPassword"
              class="field-control"
              type="password"
              placeholder="再次输入新密码"
            />
          </label>
        </div>

        <div class="inline-actions">
          <span class="muted">重置密码不会自动踢下已登录浏览器，必要时请手动通知重新登录。</span>
          <div class="table-actions">
            <button class="button button-ghost" @click="closePasswordModal">取消</button>
            <button class="button button-primary" :disabled="passwordSubmitting" @click="submitPasswordReset">
              {{ passwordSubmitting ? '提交中...' : '确认重置' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { systemUserApi, type SystemUser } from '../api'
import { authState } from '../utils/auth'

const searchKeyword = ref('')
const searchStatus = ref<string | number>('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const submitting = ref(false)
const passwordSubmitting = ref(false)

const systemUsers = ref<SystemUser[]>([])
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const showFormModal = ref(false)
const showPasswordModal = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const editingUser = ref<SystemUser | null>(null)
const passwordTarget = ref<SystemUser | null>(null)

const form = reactive({
  username: '',
  displayName: '',
  role: 'OPERATOR',
  password: ''
})

const passwordForm = reactive({
  password: '',
  confirmPassword: ''
})

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

const metrics = computed(() => {
  const list = systemUsers.value
  const activeCount = list.filter((user) => user.status === 1).length
  const superAdminCount = list.filter((user) => user.role === 'SUPER_ADMIN').length
  const loggedInCount = list.filter((user) => Boolean(user.lastLoginTime)).length

  return [
    {
      label: '当前页账号',
      value: `${list.length}`,
      tag: '已接入',
      tone: 'cyan',
      note: '列表走独立系统用户接口，和前台用户账号彻底分离。'
    },
    {
      label: '正常状态',
      value: `${activeCount}`,
      tag: activeCount === list.length && list.length > 0 ? '稳定' : '需关注',
      tone: activeCount === list.length && list.length > 0 ? 'green' : 'yellow',
      note: '禁用账号会被后台授权校验直接拦截。'
    },
    {
      label: '超级管理员',
      value: `${superAdminCount}`,
      tag: superAdminCount > 0 ? '核心权限' : '缺失',
      tone: superAdminCount > 0 ? 'magenta' : 'red',
      note: '建议仅保留少量超级管理员账号处理高权限配置。'
    },
    {
      label: '有登录记录',
      value: `${loggedInCount}`,
      tag: loggedInCount > 0 ? '活跃' : '未登录',
      tone: loggedInCount > 0 ? 'violet' : 'slate',
      note: '最近登录时间可辅助判断账号是否长期闲置。'
    }
  ]
})

const getRoleLabel = (role: string) => {
  if (role === 'SUPER_ADMIN') return '超级管理员'
  if (role === 'OPERATOR') return '运营账号'
  return role || '未知角色'
}

const getRoleTone = (role: string) => {
  if (role === 'SUPER_ADMIN') return 'magenta'
  if (role === 'OPERATOR') return 'cyan'
  return 'slate'
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

const resetFormState = () => {
  form.username = ''
  form.displayName = ''
  form.role = 'OPERATOR'
  form.password = ''
}

const closeFormModal = () => {
  showFormModal.value = false
  editingUser.value = null
  resetFormState()
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  passwordTarget.value = null
  passwordForm.password = ''
  passwordForm.confirmPassword = ''
}

const loadSystemUsers = async () => {
  loading.value = true
  try {
    const res = await systemUserApi.getList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value !== '' ? searchStatus.value : undefined
    })
    systemUsers.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载系统用户失败:', error)
    ElMessage.error('加载系统用户失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadSystemUsers()
}

const resetFilters = () => {
  searchKeyword.value = ''
  searchStatus.value = ''
  currentPage.value = 1
  loadSystemUsers()
}

const openCreateModal = () => {
  formMode.value = 'create'
  resetFormState()
  showFormModal.value = true
}

const openEditModal = (user: SystemUser) => {
  formMode.value = 'edit'
  editingUser.value = user
  form.username = user.username
  form.displayName = user.displayName
  form.role = user.role
  form.password = ''
  showFormModal.value = true
}

const submitForm = async () => {
  if (!form.username || !form.displayName || !form.role) {
    ElMessage.warning('请完整填写系统用户信息')
    return
  }

  if (formMode.value === 'create' && form.password.length < 6) {
    ElMessage.warning('初始密码至少 6 位')
    return
  }

  submitting.value = true
  try {
    if (formMode.value === 'create') {
      await systemUserApi.create({
        username: form.username,
        displayName: form.displayName,
        role: form.role,
        password: form.password
      })
      ElMessage.success('系统用户创建成功')
    } else if (editingUser.value) {
      await systemUserApi.update(editingUser.value.id, {
        displayName: form.displayName,
        role: form.role
      })
      ElMessage.success('系统用户更新成功')
    }

    closeFormModal()
    loadSystemUsers()
  } catch (error) {
    console.error('提交系统用户失败:', error)
  } finally {
    submitting.value = false
  }
}

const openResetPasswordModal = (user: SystemUser) => {
  passwordTarget.value = user
  passwordForm.password = ''
  passwordForm.confirmPassword = ''
  showPasswordModal.value = true
}

const submitPasswordReset = async () => {
  if (!passwordTarget.value) {
    return
  }

  if (passwordForm.password.length < 6) {
    ElMessage.warning('新密码至少 6 位')
    return
  }

  if (passwordForm.password !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  passwordSubmitting.value = true
  try {
    await systemUserApi.resetPassword(passwordTarget.value.id, passwordForm.password)
    ElMessage.success('密码重置成功')
    closePasswordModal()
    loadSystemUsers()
  } catch (error) {
    console.error('重置系统用户密码失败:', error)
  } finally {
    passwordSubmitting.value = false
  }
}

const toggleStatus = async (user: SystemUser) => {
  if (user.id === authState.user?.id && user.status === 1) {
    ElMessage.warning('当前登录账号不能禁用')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要${user.status === 1 ? '禁用' : '启用'}该系统用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await systemUserApi.updateStatus(user.id, user.status === 1 ? 0 : 1)
    ElMessage.success('状态更新成功')
    loadSystemUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新系统用户状态失败:', error)
    }
  }
}

const goToPage = (page: number) => {
  currentPage.value = page
  loadSystemUsers()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value -= 1
    loadSystemUsers()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value += 1
    loadSystemUsers()
  }
}

onMounted(() => {
  loadSystemUsers()
})
</script>
