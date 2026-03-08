<template>
  <div class="flex min-h-screen bg-navy-deep">
    <aside class="w-64 bg-gradient-to-b from-slate-900 to-navy-deep border-r border-white/10">
      <div class="p-6">
        <div class="flex items-center gap-3 mb-8">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-cyan-neon to-fuchsia-500 flex items-center justify-center">
            <span class="text-white font-bold text-xl">⚡</span>
          </div>
          <span class="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon to-fuchsia-500">
            租赁管理
          </span>
        </div>

        <nav class="space-y-2">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-150 group"
            :class="$route.path === item.path ? 'bg-gradient-to-r from-cyan-neon/20 to-fuchsia-500/20 border border-cyan-neon/50 text-cyan-neon' : 'text-slate-400 hover:bg-white/5 hover:text-white'"
          >
            <span class="text-xl">{{ item.icon }}</span>
            <span class="font-semibold">{{ item.name }}</span>
          </router-link>
        </nav>
      </div>
    </aside>

    <main class="flex-1 overflow-auto">
      <slot />
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
