<template>
  <div class="p-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold mb-2 text-cyan-400">
        <span class="text-pink-500">◆</span> 用户管理 <span class="text-pink-500">◆</span>
      </h1>
      <p class="text-gray-400">管理和审核平台用户</p>
    </div>

    <div class="bg-gray-900 border-2 border-cyan-500 p-4 mb-6 shadow-[4px_4px_0px_0px_rgba(0,255,255,0.5)]">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-cyan-400 mb-1 text-sm font-bold">关键词搜索</label>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="输入用户名/昵称查询..."
            class="w-full bg-gray-800 border-2 border-pink-500 text-white px-3 py-2 focus:outline-none focus:border-cyan-400"
            @keyup.enter="handleSearch"
          />
        </div>
        <div>
          <label class="block text-cyan-400 mb-1 text-sm font-bold">用户状态</label>
          <select
            v-model="searchStatus"
            class="w-full bg-gray-800 border-2 border-pink-500 text-white px-3 py-2 focus:outline-none focus:border-cyan-400"
          >
            <option value="">全部状态</option>
            <option :value="1">正常</option>
            <option :value="0">禁用</option>
          </select>
        </div>
        <div>
          <label class="block text-cyan-400 mb-1 text-sm font-bold">信用分筛选</label>
          <select
            v-model="searchCredit"
            class="w-full bg-gray-800 border-2 border-pink-500 text-white px-3 py-2 focus:outline-none focus:border-cyan-400"
          >
            <option value="">全部信用</option>
            <option value="high">优秀(≥80)</option>
            <option value="medium">良好(60-79)</option>
            <option value="low">较差(<60)</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="handleSearch"
            class="w-full bg-gradient-to-r from-pink-500 to-purple-500 text-white font-bold py-2 px-4 border-2 border-white hover:from-cyan-500 hover:to-blue-500 transition-all shadow-[2px_2px_0px_0px_#fff]"
          >
            🔍 搜索
          </button>
        </div>
      </div>
    </div>

    <div class="bg-gray-900 border-2 border-pink-500 shadow-[4px_4px_0px_0px_rgba(255,0,255,0.5)]">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gradient-to-r from-cyan-900 to-purple-900">
            <tr>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">用户ID</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">用户名</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">昵称</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">校区</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">信用分</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">状态</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" class="border-t border-gray-700 hover:bg-gray-800 transition-colors">
              <td class="px-4 py-3 text-white font-mono">{{ user.id }}</td>
              <td class="px-4 py-3 text-white">{{ user.username }}</td>
              <td class="px-4 py-3 text-white">{{ user.nickname || '-' }}</td>
              <td class="px-4 py-3 text-white">{{ user.campus || '-' }}</td>
              <td class="px-4 py-3">
                <span :class="getCreditClass(user.creditScore)" class="font-bold">
                  {{ user.creditScore }}
                </span>
              </td>
              <td class="px-4 py-3">
                <span :class="getStatusClass(user.status)" class="px-2 py-1 text-xs font-bold border-2">
                  {{ user.status === 1 ? '正常' : '禁用' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex gap-2">
                  <button
                    @click="viewUser(user)"
                    class="bg-cyan-600 text-white px-3 py-1 text-sm font-bold border-2 border-cyan-400 hover:bg-cyan-500 transition-colors"
                  >
                    查看
                  </button>
                  <button
                    @click="toggleStatus(user)"
                    :class="user.status === 1 ? 'bg-red-600 border-red-400 hover:bg-red-500' : 'bg-green-600 border-green-400 hover:bg-green-500'"
                    class="text-white px-3 py-1 text-sm font-bold border-2 transition-colors"
                  >
                    {{ user.status === 1 ? '禁用' : '启用' }}
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="loading">
              <td colspan="7" class="px-4 py-8 text-center text-cyan-400">
                <div class="text-4xl mb-2 animate-spin">⏳</div>
                <p>加载中...</p>
              </td>
            </tr>
            <tr v-if="!loading && users.length === 0">
              <td colspan="7" class="px-4 py-8 text-center text-gray-400">
                <div class="text-4xl mb-2">👥</div>
                <p>暂无用户数据</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between p-4 border-t border-gray-700">
        <div class="text-gray-400 text-sm">
          共 {{ total }} 条记录
        </div>
        <div class="flex gap-2">
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="px-4 py-2 bg-gray-800 text-cyan-400 font-bold border-2 border-cyan-500 hover:bg-cyan-900 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ← 上一页
          </button>
          <span class="px-4 py-2 bg-pink-900 text-pink-400 font-bold border-2 border-pink-500">
            {{ currentPage }} / {{ totalPages }}
          </span>
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="px-4 py-2 bg-gray-800 text-cyan-400 font-bold border-2 border-cyan-500 hover:bg-cyan-900 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            下一页 →
          </button>
        </div>
      </div>
    </div>

    <div v-if="showDetail" class="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 p-4">
      <div class="bg-gray-900 border-4 border-cyan-500 max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-[8px_8px_0px_0px_rgba(0,255,255,0.5)]">
        <div class="bg-gradient-to-r from-cyan-900 to-purple-900 p-4 border-b-2 border-cyan-500">
          <div class="flex justify-between items-center">
            <h2 class="text-xl font-bold text-cyan-400">◆ 用户详情 ◆</h2>
            <button @click="showDetail = false" class="text-pink-500 text-2xl font-bold hover:text-pink-400">✕</button>
          </div>
        </div>
        <div class="p-6" v-if="currentUser">
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <label class="text-cyan-400 font-bold text-sm">用户ID</label>
              <p class="text-white font-mono">{{ currentUser.id }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">用户名</label>
              <p class="text-white">{{ currentUser.username }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">昵称</label>
              <p class="text-white">{{ currentUser.nickname || '-' }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">信用分</label>
              <p :class="getCreditClass(currentUser.creditScore)" class="font-bold text-lg">{{ currentUser.creditScore }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">手机号</label>
              <p class="text-white">{{ currentUser.phone || '-' }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">邮箱</label>
              <p class="text-white">{{ currentUser.email || '-' }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">校区</label>
              <p class="text-white">{{ currentUser.campus || '-' }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">状态</label>
              <p :class="getStatusClass(currentUser.status)" class="px-2 py-1 text-xs font-bold border-2 inline-block">
                {{ currentUser.status === 1 ? '正常' : '禁用' }}
              </p>
            </div>
            <div class="col-span-2">
              <label class="text-cyan-400 font-bold text-sm">注册时间</label>
              <p class="text-white">{{ currentUser.createdAt }}</p>
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

const getCreditClass = (score: number) => {
  if (score >= 80) return 'text-green-400'
  if (score >= 60) return 'text-yellow-400'
  return 'text-red-400'
}

const getStatusClass = (status: number) => {
  return status === 1 
    ? 'bg-green-900 text-green-400 border-green-500' 
    : 'bg-red-900 text-red-400 border-red-500'
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
    const res = await userApi.getList(params)
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
