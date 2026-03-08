<template>
  <div class="p-8">
    <div class="mb-8">
      <h1 class="text-3xl font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon to-fuchsia-500 mb-2">
        物品管理
      </h1>
      <p class="text-slate-400">审核和管理平台物品</p>
    </div>

    <div class="mb-6 flex flex-wrap gap-4 items-center justify-between">
      <div class="flex gap-4 flex-wrap">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索物品..."
          class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white placeholder-slate-500 focus:outline-none focus:border-cyan-neon/50 transition-all duration-150"
        />
        <select
          v-model="filterType"
          class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-cyan-neon/50 transition-all duration-150"
        >
          <option value="">全部类型</option>
          <option value="1">租赁</option>
          <option value="2">出售</option>
        </select>
        <select
          v-model="filterStatus"
          class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-cyan-neon/50 transition-all duration-150"
        >
          <option value="">全部状态</option>
          <option value="0">待审核</option>
          <option value="1">已上架</option>
          <option value="2">已租出</option>
          <option value="3">已售出</option>
        </select>
      </div>
      <button class="px-6 py-2 rounded-xl bg-gradient-to-r from-cyan-neon to-fuchsia-500 font-semibold text-white hover:opacity-90 transition-opacity transition-all duration-150 hover:scale-105">
        批量审核
      </button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="item in items"
        :key="item.id"
        class="group relative overflow-hidden rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm p-5 transition-all duration-150 hover:-translate-y-2 hover:border-cyan-neon/50 hover:bg-white/10"
      >
        <div class="absolute inset-0 bg-gradient-to-br from-cyan-neon/5 via-fuchsia-500/5 to-violet-neon/5 opacity-0 transition-opacity duration-150 group-hover:opacity-100"></div>
        <div class="relative z-10">
          <div class="flex items-start justify-between mb-4">
            <span
              :class="getTypeClass(item.type)"
              class="px-3 py-1 rounded-full text-xs font-bold border"
            >
              {{ item.type === 1 ? '租赁' : '出售' }}
            </span>
            <span
              :class="getStatusClass(item.status)"
              class="px-3 py-1 rounded-full text-xs font-bold border"
            >
              {{ getStatusText(item.status) }}
            </span>
          </div>

          <h3 class="text-lg font-bold text-white mb-2 truncate">{{ item.title }}</h3>
          <p class="text-slate-400 text-sm mb-4 line-clamp-2">{{ item.description }}</p>

          <div class="flex items-center justify-between mb-4">
            <div class="flex items-baseline gap-1">
              <span class="text-cyan-neon text-xl font-black">¥{{ item.price }}</span>
              <span v-if="item.type === 1" class="text-slate-500 text-sm">/天</span>
            </div>
            <span v-if="item.deposit" class="text-fuchsia-500 text-sm">押金: ¥{{ item.deposit }}</span>
          </div>

          <div class="flex items-center justify-between text-sm text-slate-500 mb-4">
            <span>浏览: {{ item.viewCount }}</span>
            <span>校区: {{ item.campus }}</span>
          </div>

          <div class="flex gap-2">
            <button class="flex-1 px-3 py-2 rounded-lg bg-cyan-neon/20 text-cyan-neon text-sm font-semibold hover:bg-cyan-neon/30 transition-colors">
              查看详情
            </button>
            <button
              v-if="item.status === 0"
              class="px-3 py-2 rounded-lg bg-acid-green/20 text-acid-green text-sm font-semibold hover:bg-acid-green/30 transition-colors"
            >
              通过
            </button>
            <button
              v-if="item.status === 0"
              class="px-3 py-2 rounded-lg bg-red-500/20 text-red-400 text-sm font-semibold hover:bg-red-500/30 transition-colors"
            >
              拒绝
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-8 flex justify-between items-center">
      <span class="text-slate-400">显示 1-9 共 {{ items.length }} 条</span>
      <div class="flex gap-2">
        <button class="px-4 py-2 rounded-lg bg-white/5 border border-white/10 text-white hover:bg-white/10 transition-all duration-150">
          上一页
        </button>
        <button class="px-4 py-2 rounded-lg bg-gradient-to-r from-cyan-neon to-fuchsia-500 text-white font-semibold">
          1
        </button>
        <button class="px-4 py-2 rounded-lg bg-white/5 border border-white/10 text-white hover:bg-white/10 transition-all duration-150">
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')

const items = ref([
  { id: 1, title: '考研复习资料全套', description: '包含数学、英语、政治全套复习资料，笔记详细，重点突出', type: 2, status: 1, price: 150, deposit: null, campus: '东校区', viewCount: 256 },
  { id: 2, title: '山地自行车', description: '美利达山地车，9成新，适合校园骑行和周末出游', type: 1, status: 1, price: 30, deposit: 500, campus: '西校区', viewCount: 189 },
  { id: 3, title: '专业计算器', description: '卡西欧fx-991CN X，考试必备，功能完好', type: 2, status: 0, price: 120, deposit: null, campus: '南校区', viewCount: 87 },
  { id: 4, title: '露营帐篷', description: '双人帐篷，防水防风，适合2-3人露营使用', type: 1, status: 2, price: 40, deposit: 300, campus: '东校区', viewCount: 312 },
  { id: 5, title: '数码相机', description: '佳能EOS M50，入门级微单，拍照清晰，配件齐全', type: 1, status: 1, price: 80, deposit: 1500, campus: '西校区', viewCount: 445 },
  { id: 6, title: '羽毛球拍套装', description: '尤尼克斯羽毛球拍2支，含球包和羽毛球', type: 2, status: 3, price: 280, deposit: null, campus: '东校区', viewCount: 178 }
])

const getTypeClass = (type: number) => {
  return type === 1 ? 'bg-cyan-neon/20 text-cyan-neon border-cyan-neon/50' : 'bg-fuchsia-500/20 text-fuchsia-500 border-fuchsia-500/50'
}

const getStatusClass = (status: number) => {
  const classes = {
    0: 'bg-yellow-500/20 text-yellow-400 border-yellow-400/50',
    1: 'bg-acid-green/20 text-acid-green border-acid-green/50',
    2: 'bg-cyan-neon/20 text-cyan-neon border-cyan-neon/50',
    3: 'bg-violet-neon/20 text-violet-neon border-violet-neon/50'
  }
  return classes[status as keyof typeof classes] || 'bg-white/10 text-slate-400 border-white/20'
}

const getStatusText = (status: number) => {
  const texts = { 0: '待审核', 1: '已上架', 2: '已租出', 3: '已售出' }
  return texts[status as keyof typeof texts] || '未知'
}

onMounted(() => {
  console.log('物品管理页面加载')
})
</script>
