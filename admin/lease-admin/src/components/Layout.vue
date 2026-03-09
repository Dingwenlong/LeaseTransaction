<template>
  <div class="flex min-h-screen bg-navy-deep">
    <!-- Sidebar -->
    <aside class="w-64 bg-gradient-to-b from-slate-900 to-navy-deep border-r border-white/10 flex flex-col">
      <!-- Logo Area -->
      <div class="p-6 border-b border-white/10">
        <div class="flex items-center gap-3">
          <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-cyan-neon to-fuchsia-500 flex items-center justify-center shadow-neon-cyan animate-pulse-glow">
            <span class="text-white font-bold text-2xl">⚡</span>
          </div>
          <div class="flex flex-col">
            <span class="text-lg font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon to-fuchsia-500">
              租赁管理
            </span>
            <span class="text-xs text-slate-500 font-medium">Admin Panel</span>
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 p-4 space-y-1">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group relative overflow-hidden"
          :class="$route.path === item.path 
            ? 'bg-gradient-to-r from-cyan-neon/20 to-fuchsia-500/20 border border-cyan-neon/50 text-cyan-neon shadow-neon-cyan' 
            : 'text-slate-400 hover:bg-white/5 hover:text-white'"
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
          <span class="font-semibold">{{ item.name }}</span>
          
          <!-- Hover Glow Effect -->
          <div 
            v-if="$route.path !== item.path"
            class="absolute inset-0 bg-gradient-to-r from-cyan-neon/5 to-fuchsia-500/5 opacity-0 group-hover:opacity-100 transition-opacity duration-200"
          ></div>
        </router-link>
      </nav>

      <!-- Bottom Info -->
      <div class="p-4 border-t border-white/10">
        <div class="rounded-xl bg-white/5 border border-white/10 p-4">
          <div class="flex items-center gap-2 mb-2">
            <div class="w-2 h-2 rounded-full bg-acid-green animate-pulse"></div>
            <span class="text-xs text-slate-400">系统状态</span>
          </div>
          <p class="text-sm text-white font-medium">运行正常</p>
          <p class="text-xs text-slate-500 mt-1">v1.0.0 Beta</p>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 overflow-auto relative">
      <!-- Background Gradient -->
      <div class="absolute inset-0 bg-gradient-to-br from-navy-deep via-slate-900/50 to-navy-deep pointer-events-none"></div>
      
      <!-- Content -->
      <div class="relative z-10">
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
