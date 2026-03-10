<template>
  <div class="flex min-h-screen bg-navy-deep">
    <!-- Sidebar - 使用 flex 布局在左侧 -->
    <aside class="w-64 flex-shrink-0 bg-gradient-to-b from-slate-900 to-navy-deep border-r border-white/10 flex flex-col sticky top-0 h-screen">
      <!-- Logo Area -->
      <div class="h-20 flex items-center px-6 border-b border-white/10 flex-shrink-0">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-cyan-neon to-fuchsia-500 flex items-center justify-center shadow-neon-cyan animate-pulse-glow">
            <span class="text-white font-bold text-2xl">⚡</span>
          </div>
          <div class="flex flex-col gap-1">
            <span class="text-lg font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon to-fuchsia-500">
              租赁管理
            </span>
            <span class="text-xs text-slate-500 font-medium">Admin Panel</span>
          </div>
        </div>
      </div>

      <!-- Navigation - 可滚动区域 -->
      <nav style="padding: 20px;" class="flex-1 overflow-y-auto flex flex-col gap-3">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-5 px-5 py-4 rounded-xl transition-all duration-200 group relative overflow-hidden"
          :class="$route.path === item.path 
            ? 'bg-gradient-to-r from-cyan-neon/20 to-fuchsia-500/20 border border-cyan-neon/50 text-cyan-neon shadow-neon-cyan' 
            : 'text-slate-400 hover:bg-white/5 hover:text-white border border-transparent'"
        >
          <!-- Active Indicator -->
          <div 
            v-if="$route.path === item.path"
            class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-gradient-to-b from-cyan-neon to-fuchsia-500 rounded-r-full"
          ></div>
          
          <span 
            class="text-xl transition-transform duration-200 group-hover:scale-110"
            :class="$route.path === item.path ? 'animate-float' : ''"
          >
            {{ item.icon }}
          </span>
          <span class="font-semibold text-sm">{{ item.name }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- Main Content - 在侧边栏右侧 -->
    <main class="flex-1 min-h-screen overflow-auto relative">
      <!-- Background Gradient -->
      <div class="absolute inset-0 bg-gradient-to-br from-navy-deep via-slate-900/50 to-navy-deep pointer-events-none"></div>
      
      <!-- Content -->
      <div class="relative z-10 p-6 lg:p-8">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const menuItems = ref([
  { path: '/', name: '首页', icon: '🏠' },
  { path: '/users', name: '用户管理', icon: '👥' },
  { path: '/items', name: '物品管理', icon: '📦' },
  { path: '/orders', name: '订单管理', icon: '📋' }
])
</script>
