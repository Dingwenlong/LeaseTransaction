<template>
  <div
    class="group relative overflow-hidden rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-sm transition-all duration-300 hover:-translate-y-2 cursor-pointer"
    :class="cardHoverClass"
  >
    <!-- Background Gradient on Hover -->
    <div 
      class="absolute inset-0 opacity-0 transition-opacity duration-300 group-hover:opacity-100"
      :class="gradientClass"
    ></div>
    
    <!-- Glow Effect -->
    <div
      class="absolute -bottom-1/2 -left-1/2 w-full h-full rounded-full blur-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"
      :class="glowClass"
    ></div>
    
    <!-- Content -->
    <div class="relative z-10">
      <!-- Icon -->
      <div 
        class="w-14 h-14 rounded-2xl flex items-center justify-center mb-5 transition-all duration-300 group-hover:scale-110"
        :class="iconBgClass"
      >
        <span class="text-3xl">{{ icon }}</span>
      </div>
      
      <!-- Title -->
      <h3 class="text-xl font-bold text-white mb-3 group-hover:text-transparent group-hover:bg-clip-text group-hover:bg-gradient-to-r transition-all duration-300"
          :class="titleGradientClass">
        {{ title }}
      </h3>
      
      <!-- Description -->
      <p class="text-slate-400 text-sm leading-relaxed mb-5">{{ description }}</p>
      
      <!-- Footer -->
      <div class="flex items-center justify-between">
        <!-- Tag -->
        <span 
          class="px-3 py-1.5 rounded-full text-xs font-bold border transition-all duration-300"
          :class="tagClass"
        >
          {{ tag }}
        </span>
        
        <!-- Arrow -->
        <div 
          class="w-8 h-8 rounded-full flex items-center justify-center transition-all duration-300 opacity-0 group-hover:opacity-100 transform translate-x-2 group-hover:translate-x-0"
          :class="arrowClass"
        >
          <span class="text-lg">→</span>
        </div>
      </div>
    </div>
    
    <!-- Corner Decoration -->
    <div 
      class="absolute top-0 right-0 w-20 h-20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      :class="cornerClass"
    >
      <div class="absolute top-3 right-3 w-2 h-2 rounded-full"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  icon: string
  title: string
  description: string
  tag: string
  color?: 'cyan' | 'fuchsia' | 'violet'
}

const props = withDefaults(defineProps<Props>(), {
  color: 'cyan'
})

const colorConfig = {
  cyan: {
    border: 'hover:border-cyan-neon/50',
    bg: 'hover:bg-cyan-neon/5',
    shadow: 'hover:shadow-[0_20px_60px_rgba(34,211,238,0.15)]',
    gradient: 'bg-gradient-to-br from-cyan-neon/10 via-transparent to-transparent',
    glow: 'bg-cyan-neon/20',
    iconBg: 'bg-cyan-neon/10 group-hover:bg-cyan-neon/20',
    titleGradient: 'group-hover:from-cyan-neon group-hover:to-cyan-400',
    tag: 'bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30 group-hover:bg-cyan-neon/20',
    arrow: 'bg-cyan-neon/20 text-cyan-neon',
    corner: 'bg-gradient-to-bl from-cyan-neon/20 to-transparent'
  },
  fuchsia: {
    border: 'hover:border-fuchsia-500/50',
    bg: 'hover:bg-fuchsia-500/5',
    shadow: 'hover:shadow-[0_20px_60px_rgba(236,72,153,0.15)]',
    gradient: 'bg-gradient-to-br from-fuchsia-500/10 via-transparent to-transparent',
    glow: 'bg-fuchsia-500/20',
    iconBg: 'bg-fuchsia-500/10 group-hover:bg-fuchsia-500/20',
    titleGradient: 'group-hover:from-fuchsia-500 group-hover:to-fuchsia-400',
    tag: 'bg-fuchsia-500/10 text-fuchsia-500 border-fuchsia-500/30 group-hover:bg-fuchsia-500/20',
    arrow: 'bg-fuchsia-500/20 text-fuchsia-500',
    corner: 'bg-gradient-to-bl from-fuchsia-500/20 to-transparent'
  },
  violet: {
    border: 'hover:border-violet-neon/50',
    bg: 'hover:bg-violet-neon/5',
    shadow: 'hover:shadow-[0_20px_60px_rgba(139,92,246,0.15)]',
    gradient: 'bg-gradient-to-br from-violet-neon/10 via-transparent to-transparent',
    glow: 'bg-violet-neon/20',
    iconBg: 'bg-violet-neon/10 group-hover:bg-violet-neon/20',
    titleGradient: 'group-hover:from-violet-neon group-hover:to-violet-400',
    tag: 'bg-violet-neon/10 text-violet-neon border-violet-neon/30 group-hover:bg-violet-neon/20',
    arrow: 'bg-violet-neon/20 text-violet-neon',
    corner: 'bg-gradient-to-bl from-violet-neon/20 to-transparent'
  }
}

const config = computed(() => colorConfig[props.color])

const cardHoverClass = computed(() => {
  return `${config.value.border} ${config.value.bg} ${config.value.shadow}`
})

const gradientClass = computed(() => config.value.gradient)
const glowClass = computed(() => config.value.glow)
const iconBgClass = computed(() => config.value.iconBg)
const titleGradientClass = computed(() => config.value.titleGradient)
const tagClass = computed(() => config.value.tag)
const arrowClass = computed(() => config.value.arrow)
const cornerClass = computed(() => config.value.corner)
</script>
