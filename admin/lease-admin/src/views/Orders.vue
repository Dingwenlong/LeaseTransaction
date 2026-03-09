<template>
  <div class="p-8">
    <!-- Page Header -->
    <div class="mb-8">
      <div class="flex items-center gap-3 mb-2">
        <div class="w-10 h-10 rounded-xl bg-violet-neon/10 flex items-center justify-center">
          <span class="text-xl">📋</span>
        </div>
        <h1 class="text-3xl font-black text-transparent bg-clip-text bg-gradient-to-r from-violet-neon to-violet-400">
          订单管理
        </h1>
      </div>
      <p class="text-slate-400 ml-13">管理所有租赁与交易订单，跟踪订单状态</p>
    </div>

    <!-- Search Filter Card -->
    <div class="card-base p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-violet-neon mb-2 text-sm font-bold flex items-center gap-2">
            <span>🔍</span>
            <span>订单号/关键词</span>
          </label>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="输入订单号查询..."
            class="input-base w-full"
          />
        </div>
        <div>
          <label class="block text-violet-neon mb-2 text-sm font-bold flex items-center gap-2">
            <span>📋</span>
            <span>订单类型</span>
          </label>
          <select
            v-model="searchType"
            class="input-base w-full appearance-none cursor-pointer"
          >
            <option value="" class="bg-slate-900">全部类型</option>
            <option value="lease" class="bg-slate-900">租赁订单</option>
            <option value="sale" class="bg-slate-900">交易订单</option>
          </select>
        </div>
        <div>
          <label class="block text-violet-neon mb-2 text-sm font-bold flex items-center gap-2">
            <span>📊</span>
            <span>订单状态</span>
          </label>
          <select
            v-model="searchStatus"
            class="input-base w-full appearance-none cursor-pointer"
          >
            <option value="" class="bg-slate-900">全部状态</option>
            <option value="pending" class="bg-slate-900">待付款</option>
            <option value="paid" class="bg-slate-900">已付款</option>
            <option value="in_progress" class="bg-slate-900">进行中</option>
            <option value="completed" class="bg-slate-900">已完成</option>
            <option value="cancelled" class="bg-slate-900">已取消</option>
            <option value="refunded" class="bg-slate-900">已退款</option>
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
      <div class="p-5 border-b border-white/10 bg-gradient-to-r from-violet-neon/5 to-fuchsia-500/5">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-bold text-white flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-violet-neon animate-pulse"></span>
            订单列表
          </h3>
          <span class="text-sm text-slate-400">共 {{ total }} 条订单</span>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="text-left border-b border-white/10">
              <th class="p-5 font-bold text-violet-neon">订单号</th>
              <th class="p-5 font-bold text-violet-neon">物品</th>
              <th class="p-5 font-bold text-violet-neon">买家/承租人</th>
              <th class="p-5 font-bold text-violet-neon">卖家/出租人</th>
              <th class="p-5 font-bold text-violet-neon">类型</th>
              <th class="p-5 font-bold text-violet-neon">金额</th>
              <th class="p-5 font-bold text-violet-neon">状态</th>
              <th class="p-5 font-bold text-violet-neon">创建时间</th>
              <th class="p-5 font-bold text-violet-neon text-right">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-for="order in orders" :key="order.id" class="table-row">
              <td class="p-5">
                <span class="text-white font-mono bg-white/5 px-3 py-1.5 rounded-lg text-sm">{{ order.orderNo }}</span>
              </td>
              <td class="p-5">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-neon/20 to-fuchsia-500/20 flex items-center justify-center">
                    <span class="text-sm">📦</span>
                  </div>
                  <span class="text-white font-medium line-clamp-1 max-w-[150px]">{{ order.itemName }}</span>
                </div>
              </td>
              <td class="p-5">
                <div class="flex items-center gap-2">
                  <div class="w-6 h-6 rounded-full bg-cyan-neon/20 flex items-center justify-center">
                    <span class="text-xs">👤</span>
                  </div>
                  <span class="text-slate-300">{{ order.buyerName }}</span>
                </div>
              </td>
              <td class="p-5">
                <div class="flex items-center gap-2">
                  <div class="w-6 h-6 rounded-full bg-fuchsia-500/20 flex items-center justify-center">
                    <span class="text-xs">👤</span>
                  </div>
                  <span class="text-slate-300">{{ order.sellerName }}</span>
                </div>
              </td>
              <td class="p-5">
                <span :class="order.type === 'lease' ? 'bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30' : 'bg-fuchsia-500/10 text-fuchsia-500 border-fuchsia-500/30'" class="tag-base">
                  {{ order.type === 'lease' ? '租赁' : '交易' }}
                </span>
              </td>
              <td class="p-5">
                <span class="text-fuchsia-400 font-bold">¥{{ order.amount.toFixed(2) }}</span>
              </td>
              <td class="p-5">
                <span :class="getStatusClass(order.status)" class="tag-base">
                  <span class="flex items-center gap-1">
                    <span class="w-1.5 h-1.5 rounded-full" :class="getStatusDotClass(order.status)"></span>
                    {{ getStatusText(order.status) }}
                  </span>
                </span>
              </td>
              <td class="p-5 text-slate-400 text-sm">{{ order.createdAt }}</td>
              <td class="p-5 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button
                    @click="viewOrder(order)"
                    class="px-3 py-1.5 rounded-lg bg-violet-neon/10 text-violet-neon text-sm font-semibold border border-violet-neon/30 hover:bg-violet-neon/20 transition-all duration-150 flex items-center gap-1"
                  >
                    <span>👁</span>
                    <span>查看</span>
                  </button>
                  <button
                    v-if="order.status === 'pending'"
                    @click="cancelOrder(order)"
                    class="px-3 py-1.5 rounded-lg bg-red-500/10 text-red-400 text-sm font-semibold border border-red-500/30 hover:bg-red-500/20 transition-all duration-150 flex items-center gap-1"
                  >
                    <span>✕</span>
                    <span>取消</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="orders.length === 0">
              <td colspan="9" class="p-12 text-center">
                <div class="flex flex-col items-center gap-4">
                  <div class="w-16 h-16 rounded-2xl bg-white/5 flex items-center justify-center">
                    <span class="text-4xl">📦</span>
                  </div>
                  <p class="text-slate-400">暂无订单数据</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex items-center justify-between p-5 border-t border-white/10">
        <div class="text-slate-400 text-sm">
          共 {{ total }} 条记录
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
                ? 'bg-gradient-to-r from-violet-neon to-violet-400 text-navy-deep font-bold' 
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

    <!-- Order Detail Modal -->
    <div v-if="showDetail" class="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div class="card-base w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-fade-in">
        <!-- Modal Header -->
        <div class="p-6 border-b border-white/10 bg-gradient-to-r from-violet-neon/10 to-fuchsia-500/10">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-violet-neon/20 flex items-center justify-center">
                <span class="text-xl">📋</span>
              </div>
              <div>
                <h2 class="text-xl font-bold text-white">订单详情</h2>
                <p class="text-slate-400 text-sm">{{ currentOrder?.orderNo }}</p>
              </div>
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
        <div class="p-6" v-if="currentOrder">
          <!-- Status Banner -->
          <div class="mb-6 p-4 rounded-xl border" :class="getStatusBannerClass(currentOrder.status)">
            <div class="flex items-center gap-3">
              <div class="w-12 h-12 rounded-xl flex items-center justify-center" :class="getStatusIconBgClass(currentOrder.status)">
                <span class="text-2xl">{{ getStatusIcon(currentOrder.status) }}</span>
              </div>
              <div>
                <p class="text-sm opacity-80">订单状态</p>
                <p class="text-lg font-bold">{{ getStatusText(currentOrder.status) }}</p>
              </div>
            </div>
          </div>

          <!-- Order Info Grid -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">订单类型</label>
              <p class="text-white flex items-center gap-2">
                <span :class="currentOrder.type === 'lease' ? 'text-cyan-neon' : 'text-fuchsia-500'">
                  {{ currentOrder.type === 'lease' ? '🔁' : '💱' }}
                </span>
                <span>{{ currentOrder.type === 'lease' ? '租赁订单' : '交易订单' }}</span>
              </p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">订单金额</label>
              <p class="text-fuchsia-400 font-bold text-xl">¥{{ currentOrder.amount.toFixed(2) }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">物品名称</label>
              <p class="text-white">{{ currentOrder.itemName }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">物品ID</label>
              <p class="text-white font-mono">{{ currentOrder.itemId }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">买家/承租人</label>
              <p class="text-white flex items-center gap-2">
                <span class="w-6 h-6 rounded-full bg-cyan-neon/20 flex items-center justify-center text-xs">👤</span>
                <span>{{ currentOrder.buyerName }}</span>
              </p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">卖家/出租人</label>
              <p class="text-white flex items-center gap-2">
                <span class="w-6 h-6 rounded-full bg-fuchsia-500/20 flex items-center justify-center text-xs">👤</span>
                <span>{{ currentOrder.sellerName }}</span>
              </p>
            </div>
            <template v-if="currentOrder.type === 'lease'">
              <div class="p-4 rounded-xl bg-white/5 border border-white/10">
                <label class="text-slate-400 text-sm mb-1 block">租赁开始</label>
                <p class="text-white">{{ currentOrder.leaseStart }}</p>
              </div>
              <div class="p-4 rounded-xl bg-white/5 border border-white/10">
                <label class="text-slate-400 text-sm mb-1 block">租赁结束</label>
                <p class="text-white">{{ currentOrder.leaseEnd }}</p>
              </div>
            </template>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">创建时间</label>
              <p class="text-white">{{ currentOrder.createdAt }}</p>
            </div>
            <div class="p-4 rounded-xl bg-white/5 border border-white/10">
              <label class="text-slate-400 text-sm mb-1 block">更新时间</label>
              <p class="text-white">{{ currentOrder.updatedAt }}</p>
            </div>
          </div>

          <!-- Remark -->
          <div class="p-4 rounded-xl bg-white/5 border border-white/10">
            <label class="text-slate-400 text-sm mb-2 block">备注信息</label>
            <p class="text-white">{{ currentOrder.remark || '暂无备注' }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

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
const currentPage = ref(1)
const pageSize = ref(10)
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
    amount: 150.00,
    status: 'in_progress',
    leaseStart: '2024-12-01',
    leaseEnd: '2024-12-07',
    remark: '租期一周，押金5000元',
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
    amount: 25.00,
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
    amount: 30.00,
    status: 'pending',
    leaseStart: '2024-12-03',
    leaseEnd: '2024-12-05',
    createdAt: '2024-12-01 14:45:00',
    updatedAt: '2024-12-01 14:45:00'
  }
])

const total = ref(orders.value.length)
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

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-yellow-500/10 text-yellow-400 border-yellow-400/30',
    paid: 'bg-blue-500/10 text-blue-400 border-blue-400/30',
    in_progress: 'bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30',
    completed: 'bg-acid-green/10 text-acid-green border-acid-green/30',
    cancelled: 'bg-slate-500/10 text-slate-400 border-slate-400/30',
    refunded: 'bg-red-500/10 text-red-400 border-red-400/30'
  }
  return classes[status] || 'bg-white/10 text-slate-400 border-white/20'
}

const getStatusDotClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-yellow-400',
    paid: 'bg-blue-400',
    in_progress: 'bg-cyan-neon',
    completed: 'bg-acid-green',
    cancelled: 'bg-slate-400',
    refunded: 'bg-red-400'
  }
  return classes[status] || 'bg-slate-400'
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
    pending: 'bg-yellow-500/10 border-yellow-500/30 text-yellow-400',
    paid: 'bg-blue-500/10 border-blue-500/30 text-blue-400',
    in_progress: 'bg-cyan-neon/10 border-cyan-neon/30 text-cyan-neon',
    completed: 'bg-acid-green/10 border-acid-green/30 text-acid-green',
    cancelled: 'bg-slate-500/10 border-slate-500/30 text-slate-400',
    refunded: 'bg-red-500/10 border-red-500/30 text-red-400'
  }
  return classes[status] || 'bg-white/10 border-white/20 text-slate-400'
}

const getStatusIconBgClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-yellow-500/20',
    paid: 'bg-blue-500/20',
    in_progress: 'bg-cyan-neon/20',
    completed: 'bg-acid-green/20',
    cancelled: 'bg-slate-500/20',
    refunded: 'bg-red-500/20'
  }
  return classes[status] || 'bg-white/10'
}

const getStatusIcon = (status: string) => {
  const icons: Record<string, string> = {
    pending: '⏳',
    paid: '💳',
    in_progress: '📦',
    completed: '✓',
    cancelled: '✕',
    refunded: '↩'
  }
  return icons[status] || '❓'
}

const handleSearch = () => {
  console.log('搜索订单:', { keyword: searchKeyword.value, type: searchType.value, status: searchStatus.value })
}

const viewOrder = (order: Order) => {
  currentOrder.value = order
  showDetail.value = true
}

const cancelOrder = (order: Order) => {
  if (confirm('确定要取消这个订单吗？')) {
    console.log('取消订单:', order.id)
  }
}

const goToPage = (page: number) => {
  currentPage.value = page
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

onMounted(() => {
  console.log('订单管理页面已加载')
})
</script>
