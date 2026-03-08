<template>
  <div class="p-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold mb-2 text-cyan-400">
        <span class="text-pink-500">◆</span> 订单管理 <span class="text-pink-500">◆</span>
      </h1>
      <p class="text-gray-400">管理所有租赁与交易订单</p>
    </div>

    <div class="bg-gray-900 border-2 border-cyan-500 p-4 mb-6 shadow-[4px_4px_0px_0px_rgba(0,255,255,0.5)]">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-cyan-400 mb-1 text-sm font-bold">订单号/关键词</label>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="输入订单号查询..."
            class="w-full bg-gray-800 border-2 border-pink-500 text-white px-3 py-2 focus:outline-none focus:border-cyan-400"
          />
        </div>
        <div>
          <label class="block text-cyan-400 mb-1 text-sm font-bold">订单类型</label>
          <select
            v-model="searchType"
            class="w-full bg-gray-800 border-2 border-pink-500 text-white px-3 py-2 focus:outline-none focus:border-cyan-400"
          >
            <option value="">全部类型</option>
            <option value="lease">租赁订单</option>
            <option value="sale">交易订单</option>
          </select>
        </div>
        <div>
          <label class="block text-cyan-400 mb-1 text-sm font-bold">订单状态</label>
          <select
            v-model="searchStatus"
            class="w-full bg-gray-800 border-2 border-pink-500 text-white px-3 py-2 focus:outline-none focus:border-cyan-400"
          >
            <option value="">全部状态</option>
            <option value="pending">待付款</option>
            <option value="paid">已付款</option>
            <option value="in_progress">进行中</option>
            <option value="completed">已完成</option>
            <option value="cancelled">已取消</option>
            <option value="refunded">已退款</option>
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
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">订单号</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">物品</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">买家/承租人</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">卖家/出租人</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">类型</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">金额</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">状态</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">创建时间</th>
              <th class="px-4 py-3 text-left text-cyan-400 font-bold">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id" class="border-t border-gray-700 hover:bg-gray-800 transition-colors">
              <td class="px-4 py-3 text-white font-mono">{{ order.orderNo }}</td>
              <td class="px-4 py-3 text-white">{{ order.itemName }}</td>
              <td class="px-4 py-3 text-white">{{ order.buyerName }}</td>
              <td class="px-4 py-3 text-white">{{ order.sellerName }}</td>
              <td class="px-4 py-3">
                <span :class="order.type === 'lease' ? 'text-cyan-400' : 'text-pink-400'" class="font-bold">
                  {{ order.type === 'lease' ? '租赁' : '交易' }}
                </span>
              </td>
              <td class="px-4 py-3 text-yellow-400 font-bold">¥{{ order.amount.toFixed(2) }}</td>
              <td class="px-4 py-3">
                <span :class="getStatusClass(order.status)" class="px-2 py-1 text-xs font-bold border-2">
                  {{ getStatusText(order.status) }}
                </span>
              </td>
              <td class="px-4 py-3 text-gray-400 text-sm">{{ order.createdAt }}</td>
              <td class="px-4 py-3">
                <div class="flex gap-2">
                  <button
                    @click="viewOrder(order)"
                    class="bg-cyan-600 text-white px-3 py-1 text-sm font-bold border-2 border-cyan-400 hover:bg-cyan-500 transition-colors"
                  >
                    查看
                  </button>
                  <button
                    v-if="order.status === 'pending'"
                    @click="cancelOrder(order)"
                    class="bg-red-600 text-white px-3 py-1 text-sm font-bold border-2 border-red-400 hover:bg-red-500 transition-colors"
                  >
                    取消
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="orders.length === 0">
              <td colspan="9" class="px-4 py-8 text-center text-gray-400">
                <div class="text-4xl mb-2">📦</div>
                <p>暂无订单数据</p>
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
            <h2 class="text-xl font-bold text-cyan-400">◆ 订单详情 ◆</h2>
            <button @click="showDetail = false" class="text-pink-500 text-2xl font-bold hover:text-pink-400">✕</button>
          </div>
        </div>
        <div class="p-6" v-if="currentOrder">
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <label class="text-cyan-400 font-bold text-sm">订单号</label>
              <p class="text-white font-mono">{{ currentOrder.orderNo }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">订单状态</label>
              <p><span :class="getStatusClass(currentOrder.status)" class="px-2 py-1 text-sm font-bold border-2">{{ getStatusText(currentOrder.status) }}</span></p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">订单类型</label>
              <p class="text-white">{{ currentOrder.type === 'lease' ? '租赁订单' : '交易订单' }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">订单金额</label>
              <p class="text-yellow-400 font-bold text-lg">¥{{ currentOrder.amount.toFixed(2) }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">物品名称</label>
              <p class="text-white">{{ currentOrder.itemName }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">物品ID</label>
              <p class="text-white">{{ currentOrder.itemId }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">买家/承租人</label>
              <p class="text-white">{{ currentOrder.buyerName }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">卖家/出租人</label>
              <p class="text-white">{{ currentOrder.sellerName }}</p>
            </div>
            <div v-if="currentOrder.type === 'lease'">
              <label class="text-cyan-400 font-bold text-sm">租赁开始时间</label>
              <p class="text-white">{{ currentOrder.leaseStart }}</p>
            </div>
            <div v-if="currentOrder.type === 'lease'">
              <label class="text-cyan-400 font-bold text-sm">租赁结束时间</label>
              <p class="text-white">{{ currentOrder.leaseEnd }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">创建时间</label>
              <p class="text-white">{{ currentOrder.createdAt }}</p>
            </div>
            <div>
              <label class="text-cyan-400 font-bold text-sm">更新时间</label>
              <p class="text-white">{{ currentOrder.updatedAt }}</p>
            </div>
          </div>
          <div class="border-t border-gray-700 pt-4">
            <label class="text-cyan-400 font-bold text-sm block mb-2">备注信息</label>
            <p class="text-gray-300 bg-gray-800 p-3 border-2 border-pink-500">{{ currentOrder.remark || '暂无备注' }}</p>
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

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-yellow-900 text-yellow-400 border-yellow-500',
    paid: 'bg-blue-900 text-blue-400 border-blue-500',
    in_progress: 'bg-cyan-900 text-cyan-400 border-cyan-500',
    completed: 'bg-green-900 text-green-400 border-green-500',
    cancelled: 'bg-gray-700 text-gray-400 border-gray-500',
    refunded: 'bg-pink-900 text-pink-400 border-pink-500'
  }
  return classes[status] || 'bg-gray-700 text-gray-400 border-gray-500'
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
