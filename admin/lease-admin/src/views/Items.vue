<template>
  <div class="p-8">
    <!-- Page Header -->
    <div class="mb-8">
      <div class="flex items-center gap-3 mb-2">
        <div class="w-10 h-10 rounded-xl bg-fuchsia-500/10 flex items-center justify-center">
          <span class="text-xl">📦</span>
        </div>
        <h1 class="text-3xl font-black text-transparent bg-clip-text bg-gradient-to-r from-fuchsia-500 to-fuchsia-400">
          物品管理
        </h1>
      </div>
      <p class="text-slate-400 ml-13">审核和管理平台物品，维护物品质量</p>
    </div>

    <!-- Search Filter Card -->
    <div class="card-base p-6 mb-6">
      <div class="flex flex-col lg:flex-row gap-4 items-end">
        <div class="flex-1 grid grid-cols-1 md:grid-cols-3 gap-4 w-full">
          <div>
            <label class="block text-fuchsia-500 mb-2 text-sm font-bold flex items-center gap-2">
              <span>🔍</span>
              <span>关键词搜索</span>
            </label>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索物品名称..."
              class="input-base w-full"
            />
          </div>
          <div>
            <label class="block text-fuchsia-500 mb-2 text-sm font-bold flex items-center gap-2">
              <span>📋</span>
              <span>物品类型</span>
            </label>
            <select
              v-model="filterType"
              class="input-base w-full appearance-none cursor-pointer"
            >
              <option value="" class="bg-slate-900">全部类型</option>
              <option value="1" class="bg-slate-900">租赁</option>
              <option value="2" class="bg-slate-900">出售</option>
            </select>
          </div>
          <div>
            <label class="block text-fuchsia-500 mb-2 text-sm font-bold flex items-center gap-2">
              <span>📊</span>
              <span>物品状态</span>
            </label>
            <select
              v-model="filterStatus"
              class="input-base w-full appearance-none cursor-pointer"
            >
              <option value="" class="bg-slate-900">全部状态</option>
              <option value="0" class="bg-slate-900">待审核</option>
              <option value="1" class="bg-slate-900">已上架</option>
              <option value="2" class="bg-slate-900">已租出</option>
              <option value="3" class="bg-slate-900">已售出</option>
            </select>
          </div>
        </div>
        <button class="btn-primary whitespace-nowrap flex items-center gap-2">
          <span>✓</span>
          <span>批量审核</span>
        </button>
      </div>
    </div>

    <!-- Items Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 mb-8">
      <div
        v-for="item in items"
        :key="item.id"
        class="card-hover group cursor-pointer"
      >
        <!-- Card Header -->
        <div class="p-5">
          <div class="flex items-start justify-between mb-4">
            <div class="flex gap-2">
              <span
                :class="getTypeClass(item.type)"
                class="tag-base"
              >
                {{ item.type === 1 ? '租赁' : '出售' }}
              </span>
              <span
                :class="getStatusClass(item.status)"
                class="tag-base"
              >
                <span class="flex items-center gap-1">
                  <span class="w-1.5 h-1.5 rounded-full" :class="getStatusDotClass(item.status)"></span>
                  {{ getStatusText(item.status) }}
                </span>
              </span>
            </div>
            <button class="w-8 h-8 rounded-lg bg-white/5 hover:bg-white/10 flex items-center justify-center text-slate-400 hover:text-white transition-all duration-150 opacity-0 group-hover:opacity-100">
              <span>⋮</span>
            </button>
          </div>

          <!-- Title & Description -->
          <h3 class="text-lg font-bold text-white mb-2 line-clamp-1 group-hover:text-transparent group-hover:bg-clip-text group-hover:bg-gradient-to-r group-hover:from-fuchsia-500 group-hover:to-fuchsia-400 transition-all duration-200">
            {{ item.title }}
          </h3>
          <p class="text-slate-400 text-sm mb-4 line-clamp-2">{{ item.description }}</p>

          <!-- Price Info -->
          <div class="flex items-center justify-between mb-4 p-3 rounded-xl bg-white/5">
            <div class="flex items-baseline gap-1">
              <span class="text-fuchsia-500 text-2xl font-black">¥{{ item.price }}</span>
              <span v-if="item.type === 1" class="text-slate-500 text-sm">/天</span>
            </div>
            <span v-if="item.deposit" class="text-slate-400 text-sm">
              押金: <span class="text-cyan-neon font-semibold">¥{{ item.deposit }}</span>
            </span>
          </div>

          <!-- Stats -->
          <div class="flex items-center justify-between text-sm text-slate-500 mb-4">
            <span class="flex items-center gap-1">
              <span>👁</span>
              <span>{{ item.viewCount }} 浏览</span>
            </span>
            <span class="flex items-center gap-1">
              <span>📍</span>
              <span>{{ item.campus }}</span>
            </span>
          </div>

          <!-- Actions -->
          <div class="flex gap-2">
            <button class="flex-1 px-4 py-2.5 rounded-xl bg-cyan-neon/10 text-cyan-neon text-sm font-semibold border border-cyan-neon/30 hover:bg-cyan-neon/20 transition-all duration-150 flex items-center justify-center gap-1">
              <span>👁</span>
              <span>查看详情</span>
            </button>
            <template v-if="item.status === 0">
              <button class="px-4 py-2.5 rounded-xl bg-acid-green/10 text-acid-green text-sm font-semibold border border-acid-green/30 hover:bg-acid-green/20 transition-all duration-150">
                ✓
              </button>
              <button class="px-4 py-2.5 rounded-xl bg-red-500/10 text-red-400 text-sm font-semibold border border-red-500/30 hover:bg-red-500/20 transition-all duration-150">
                ✕
              </button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="card-base p-5">
      <div class="flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="text-slate-400 text-sm">
          显示 1-{{ items.length }} 条，共 {{ items.length }} 条
        </div>
        <div class="flex items-center gap-2">
          <button class="px-4 py-2 rounded-lg bg-white/5 text-slate-300 font-medium border border-white/10 hover:bg-white/10 hover:text-white transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1">
            <span>←</span>
            <span>上一页</span>
          </button>
          <button class="w-10 h-10 rounded-lg bg-gradient-to-r from-fuchsia-500 to-fuchsia-400 text-navy-deep font-bold">
            1
          </button>
          <button class="px-4 py-2 rounded-lg bg-white/5 text-slate-300 font-medium border border-white/10 hover:bg-white/10 hover:text-white transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1">
            <span>下一页</span>
            <span>→</span>
          </button>
        </div>
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
  return type === 1 
    ? 'bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30' 
    : 'bg-fuchsia-500/10 text-fuchsia-500 border-fuchsia-500/30'
}

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'bg-yellow-500/10 text-yellow-400 border-yellow-400/30',
    1: 'bg-acid-green/10 text-acid-green border-acid-green/30',
    2: 'bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30',
    3: 'bg-violet-neon/10 text-violet-neon border-violet-neon/30'
  }
  return classes[status] || 'bg-white/10 text-slate-400 border-white/20'
}

const getStatusDotClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'bg-yellow-400',
    1: 'bg-acid-green',
    2: 'bg-cyan-neon',
    3: 'bg-violet-neon'
  }
  return classes[status] || 'bg-slate-400'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 
    0: '待审核', 
    1: '已上架', 
    2: '已租出', 
    3: '已售出' 
  }
  return texts[status] || '未知'
}

onMounted(() => {
  console.log('物品管理页面加载')
})
</script>
