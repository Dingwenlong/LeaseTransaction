<template>
  <div class="p-8">
    <!-- Page Header -->
    <div class="mb-8">
      <div class="flex items-center gap-3 mb-2">
        <div class="w-10 h-10 rounded-xl bg-cyan-neon/10 flex items-center justify-center">
          <span class="text-xl">👥</span>
        </div>
        <h1 class="text-3xl font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon to-cyan-400">
          用户管理
        </h1>
      </div>
      <p class="text-slate-400 ml-13">管理和审核平台用户，维护校园交易环境</p>
    </div>

    <!-- Search Filter Card -->
    <div class="card-base p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-cyan-neon mb-2 text-sm font-bold flex items-center gap-2">
            <span>🔍</span>
            <span>关键词搜索</span>
          </label>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="输入用户名/昵称查询..."
            class="input-base w-full"
            @keyup.enter="handleSearch"
          />
        </div>
        <div>
          <label class="block text-cyan-neon mb-2 text-sm font-bold flex items-center gap-2">
            <span>📊</span>
            <span>用户状态</span>
          </label>
          <select
            v-model="searchStatus"
            class="input-base w-full appearance-none cursor-pointer"
          >
            <option value="" class="bg-slate-900">全部状态</option>
            <option :value="1" class="bg-slate-900">正常</option>
            <option :value="0" class="bg-slate-900">禁用</option>
          </select>
        </div>
        <div>
          <label class="block text-cyan-neon mb-2 text-sm font-bold flex items-center gap-2">
            <span>⭐</span>
            <span>信用分筛选</span>
          </label>
          <select
            v-model="searchCredit"
            class="input-base w-full appearance-none cursor-pointer"
          >
            <option value="" class="bg-slate-900">全部信用</option>
            <option value="high" class="bg-slate-900">优秀(≥80)</option>
            <option value="medium" class="bg-slate-900">良好(60-79)</option>
            <option value="low" class="bg-slate-900">较差(&lt;60)</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="handleSearch"
            class="btn-primary w-full flex items-center justify-center gap-2"
          >
            <span>搜索</span>
            <span>→</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Data Table Card -->
    <div class="card-base overflow-hidden">
      <!-- Table Header -->
      <div class="p-5 border-b border-white/10 bg-gradient-to-r from-cyan-neon/5 to-fuchsia-500/5">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-bold text-white flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-cyan-neon animate-pulse"></span>
            用户列表
          </h3>
          <span class="text-sm text-slate-400">共 {{ total }} 位用户</span>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="text-left border-b border-white/10">
              <th class="p-5 font-bold text-cyan-neon">用户ID</th>
              <th class="p-5 font-bold text-cyan-neon">用户名</th>
              <th class="p-5 font-bold text-cyan-neon">昵称</th>
              <th class="p-5 font-bold text-cyan-neon">校区</th>
              <th class="p-5 font-bold text-cyan-neon">信用分</th>
              <th class="p-5 font-bold text-cyan-neon">状态</th>
              <th class="p-5 font-bold text-cyan-neon text-right">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-for="user in users" :key="user.id" class="table-row">
              <td class="p-5">
                <span class="text-white font-mono bg-white/5 px-3 py-1.5 rounded-lg text-sm">{{ user.id }}</span>
              </td>
              <td class="p-5">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-full bg-gradient-to-br from-cyan-neon/20 to-fuchsia-500/20 flex items-center justify-center">
                    <span class="text-sm">👤</span>
                  </div>
                  <span class="text-white font-medium">{{ user.username }}</span>
                </div>
              </td>
              <td class="p-5 text-slate-300">{{ user.nickname || '-' }}</td>
              <td class="p-5">
                <span class="flex items-center gap-1 text-slate-300">
                  <span>📍</span>
                  <span>{{ user.campus || '-' }}</span>
                </span>
              </td>
              <td class="p-5">
                <div class="flex items-center gap-2">
                  <div class="w-16 h-2 rounded-full bg-white/10 overflow-hidden">
                    <div 
                      class="h-full rounded-full transition-all duration-500"
                      :class="getCreditBarClass(user.creditScore)"
                      :style="{ width: user.creditScore + '%' }"
                    ></div>
                  </div>
                  <span :class="getCreditClass(user.creditScore)" class="font-bold text-sm">
                    {{ user.creditScore }}
                  </span>
                </div>
              </td>
              <td class="p-5">
                <span :class="getStatusClass(user.status)" class="tag-base">
                  <span class="flex items-center gap-1">
                    <span class="w-1.5 h-1.5 rounded-full" :class="user.status === 1 ? 'bg-acid-green' : 'bg-red-400'"></span>
                    {{ user.status === 1 ? '正常' : '禁用' }}
                  </span>
                </span>
              </td>
              <td class="p-5 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button
                    @click="viewUser(user)"
                    class="px-3 py-1.5 rounded-lg bg-cyan-neon/10 text-cyan-neon text-sm font-semibold border border-cyan-neon/30 hover:bg-cyan-neon/20 transition-all duration-150 flex items-center gap-1"
                  >
                    <span>👁</span>
                    <span>查看</span>
                  </button>
                  <button
                    @click="toggleStatus(user)"
                    :class="user.status === 1 
                      ? 'bg-red-500/10 text-red-400 border-red-500/30 hover:bg-red-500/20' 
                      : 'bg-acid-green/10 text-acid-green border-acid-green/30 hover:bg-acid-green/20'"
                    class="px-3 py-1.5 rounded-lg text-sm font-semibold border transition-all duration-150 flex items-center gap-1"
                  >
                    <span>{{ user.status === 1 ? '🚫' : '✓' }}</span>
                    <span>{{ user.status === 1 ? '禁用' : '启用' }}</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="loading">
              <td colspan="7" class="p-12 text-center">
                <div class="flex flex-col items-center gap-4">
                  <div class="w-12 h-12 rounded-full border-4 border-cyan-neon/20 border-t-cyan-neon animate-spin"></div>
                  <p class="text-cyan-neon">加载中...</p>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && users.length === 0">
              <td colspan="7" class="p-12 text-center">
                <div class="flex flex-col items-center gap-4">
                  <div class="w-16 h-16 rounded-2xl bg-white/5 flex items-center justify-center">
                    <span class="text-4xl">👥</span>
                  </div>
                  <p class="text-slate-400">暂无用户数据</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex items-center justify-between p-5 border-t border-white/10">
        <div class="text-slate-400 text-sm">
          显示 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, total) }} 条，共 {{ total }} 条
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="px-4 py-2 rounded-lg bg-white/5 text-slate-300 font-medium border border-white/10 hover:bg-white/10 hover:text-white transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1"
          >
            <span>←</span>
            <span>上一页</span>
          </button>
          <div class="flex items-center gap-1">
            <button
              v-for="page in displayedPages"
              :key="page"
              @click="goToPage(page)"
              :class="currentPage === page 
                ? 'bg-gradient-to-r from-cyan-neon to-cyan-400 text-navy-deep font-bold' 
                : 'bg-white/5 text-slate-300 hover:bg-white/10 hover:text-white'"
              class="w-10 h-10 rounded-lg border border-white/10 transition-all duration-150"
            >
              {{ page }}
            </button>
          </div>
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="px-4 py-2 rounded-lg bg-white/5 text-slate-300 font-medium border border-white/10 hover:bg-white/10 hover:text-white transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1"
          >
            <span>下一页</span>
            <span>→</span>
          </button>
        </div>
      </div>
    </div>

    <!-- User Detail Modal -->
    <div v-if="showDetail" class="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div class="card-base w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-fade-in">
        <!-- Modal Header -->
        <div class="p-6 border-b border-white/10 bg-gradient-to-r from-cyan-neon/10 to-fuchsia-500/10">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-cyan-neon/20 flex items-center justify-center">
                <span class="text-xl">👤</span>
              </div>
              <h2 class="text-xl font-bold text-white">用户详情</h2>
            </div>
            <button 
              @click="showDetail = false" 
              class="w-10 h-10 rounded-xl bg-white/5 hover:bg-white/10 flex items-center justify-center text-slate-400 hover:text-white transition-all duration-150"
            >
              <span class="text-xl">✕</span>
            </button>
          </div>
        </div>
        
        <!-- Modal Content -->
        <div class="p-6" v-if="currentUser">
          <!-- User Avatar & Basic Info -->
          <div class="flex items-center gap-4 mb-6 pb-6 border-b border-white/10">
            <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-cyan-neon to-fuchsia-500 flex items-center justify-center text-4xl">
              👤
            </div>
            <div>
              <h3 class="text-2xl font-bold text-white mb-1">{{ currentUser.username }}</h3>
              <p class="text-slate-400">{{ currentUser.nickname || '暂无昵称' }}</p>
              <div class="flex items-center gap-2 mt-2">
                <span :class="getStatusClass(currentUser.status)" class="tag-base">
                  {{ currentUser.status === 1 ? '正常' : '禁用' }}
                </span>
                <span :class="getCreditTagClass(currentUser.creditScore)" class="tag-base">
                  信用 {{ currentUser.creditScore }}
                </span>
              </div>
            </div>
          </div>

          <!-- Detail Grid -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">用户ID</label>
              <p class="text-white font-mono">{{ currentUser.id }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">手机号</label>
              <p class="text-white">{{ currentUser.phone || '-' }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">邮箱</label>
              <p class="text-white">{{ currentUser.email || '-' }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">校区</label>
              <p class="text-white flex items-center gap-1">
                <span>📍</span>
                <span>{{ currentUser.campus || '-' }}</span>
              </p>
            </div>
            <div class="col-span-2 p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">注册时间</label>
              <p class="text-white">{{ currentUser.createdAt }}</p>
            </div>
          </div>

          <!-- Credit Score Bar -->
          <div class="p-4 rounded-xl bg-white/5 border border-white/10 mb-6">
            <label class="text-slate-400 text-sm mb-3 block">信用评分</label>
            <div class="flex items-center gap-4">
              <div class="flex-1 h-3 rounded-full bg-white/10 overflow-hidden">
                <div 
                  class="h-full rounded-full transition-all duration-500"
                  :class="getCreditBarClass(currentUser.creditScore)"
                  :style="{ width: currentUser.creditScore + '%' }"
                ></div>
              </div>
              <span :class="getCreditClass(currentUser.creditScore)" class="font-bold text-xl">
                {{ currentUser.creditScore }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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

// Display page numbers
const displayedPages = computed(() => {
  const pages: number[] = []
  const maxDisplay = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxDisplay / 2))
  let end = Math.min(totalPages.value, start + maxDisplay - 1)
  
  if (end - start + 1 < maxDisplay) {
    start = Math.max(1, end - maxDisplay + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const getCreditClass = (score: number) => {
  if (score >= 80) return 'text-acid-green'
  if (score >= 60) return 'text-yellow-400'
  return 'text-red-400'
}

const getCreditTagClass = (score: number) => {
  if (score >= 80) return 'bg-acid-green/10 text-acid-green border-acid-green/30'
  if (score >= 60) return 'bg-yellow-400/10 text-yellow-400 border-yellow-400/30'
  return 'bg-red-400/10 text-red-400 border-red-400/30'
}

const getCreditBarClass = (score: number) => {
  if (score >= 80) return 'bg-gradient-to-r from-acid-green to-green-400'
  if (score >= 60) return 'bg-gradient-to-r from-yellow-400 to-yellow-500'
  return 'bg-gradient-to-r from-red-400 to-red-500'
}

const getStatusClass = (status: number) => {
  return status === 1 
    ? 'bg-acid-green/10 text-acid-green border-acid-green/30' 
    : 'bg-red-400/10 text-red-400 border-red-400/30'
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
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
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
    }
  }
}

const goToPage = (page: number) => {
  currentPage.value = page
  loadUsers()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadUsers()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadUsers()
  }
}

onMounted(() => {
  loadUsers()
})
</script>
