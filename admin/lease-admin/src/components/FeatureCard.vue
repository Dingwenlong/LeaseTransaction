<template>
  <div
    class="group relative overflow-hidden rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-sm transition-all duration-150 hover:-translate-y-2 hover:border-cyan-neon/50 hover:bg-white/10"
    :class="hoverClass"
  >
    <div class="absolute inset-0 bg-gradient-to-br from-cyan-neon/5 via-fuchsia-500/5 to-violet-neon/5 opacity-0 transition-opacity duration-150 group-hover:opacity-100"></div>
    <div class="relative z-10">
      <div class="text-4xl mb-4">{{ icon }}</div>
      <h3 class="text-xl font-bold text-white mb-2">{{ title }}</h3>
      <p class="text-slate-400 text-sm leading-relaxed mb-4">{{ description }}</p>
      <div class="flex items-center justify-between">
        <span class="px-3 py-1 rounded-full text-xs font-semibold bg-gradient-to-r from-cyan-neon/20 to-fuchsia-500/20 border border-cyan-neon/30 text-cyan-neon">
          {{ tag }}
        </span>
        <div class="text-cyan-neon opacity-0 group-hover:opacity-100 transition-opacity duration-150">
          →
        </div>
      </div>
    </div>
    <div
      class="absolute -bottom-1/2 -left-1/2 w-full h-full bg-gradient-to-r from-cyan-neon/20 to-fuchsia-500/20 rounded-full blur-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  icon: string
  title: string
  description: string
  tag: string
}

const props = defineProps<Props>()

const hoverClass = computed(() => {
  return 'hover:shadow-[0_20px_60px_rgba(34,211,238,0.2)]'
})
</script>
