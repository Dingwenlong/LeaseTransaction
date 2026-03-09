<template>
  <div class="relative min-h-screen scanlines noise">
    <div class="relative z-10">
      <!-- Hero Section -->
      <section class="relative overflow-hidden py-20 lg:py-28">
        <!-- Background Effects -->
        <div class="absolute inset-0 bg-gradient-to-br from-navy-deep via-slate-900 to-navy-deep"></div>
        <div class="absolute inset-0 bg-gradient-to-br from-cyan-neon/10 via-fuchsia-500/10 to-violet-neon/10 animate-pulse-glow"></div>
        <div class="absolute top-0 left-1/4 w-96 h-96 bg-cyan-neon/20 rounded-full blur-[120px] animate-glow-pulse"></div>
        <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-fuchsia-500/20 rounded-full blur-[120px] animate-glow-pulse" style="animation-delay: 1s;"></div>
        
        <div class="max-w-6xl mx-auto px-6 lg:px-10 relative">
          <div class="relative z-10 text-center">
            <!-- Badge -->
            <div class="inline-flex items-center gap-2 mb-8 px-5 py-2.5 rounded-full border-2 border-cyan-neon/50 bg-cyan-neon/10 backdrop-blur-sm shadow-neon-cyan">
              <span class="w-2 h-2 rounded-full bg-cyan-neon animate-pulse"></span>
              <span class="text-cyan-neon text-sm font-bold tracking-widest uppercase">Y2K Admin Panel</span>
            </div>
            
            <!-- Title -->
            <h1 class="text-5xl md:text-6xl lg:text-7xl font-black mb-6 leading-tight">
              <span class="text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon">
                校园租赁
              </span>
              <br />
              <span class="relative inline-block">
                <span class="text-white">交易管理</span>
                <span class="absolute -bottom-2 left-0 right-0 h-1 bg-gradient-to-r from-cyan-neon to-fuchsia-500 rounded-full"></span>
              </span>
              <span class="text-cyan-neon animate-float inline-block ml-2">系统</span>
            </h1>
            
            <!-- Description -->
            <p class="text-xl text-slate-300 max-w-2xl mx-auto mb-12 leading-relaxed">
              复古未来感的校园个人物品租赁与交易管理平台，让校园资源共享更便捷
            </p>
            
            <!-- CTA Buttons -->
            <div class="flex flex-col sm:flex-row gap-4 justify-center">
              <button class="group px-10 py-4 font-bold text-navy-deep bg-gradient-to-r from-cyan-neon to-cyan-400 rounded-full shadow-button-glow transition-all duration-200 hover:shadow-[0_0_50px_rgba(34,211,238,0.6)] hover:scale-105 active:scale-98 flex items-center justify-center gap-2">
                <span>开始管理</span>
                <span class="transition-transform duration-200 group-hover:translate-x-1">→</span>
              </button>
              <button class="px-10 py-4 font-bold text-white border-2 border-fuchsia-500 rounded-full hover:bg-fuchsia-500/10 hover:shadow-neon-pink transition-all duration-200 hover:scale-105 flex items-center justify-center gap-2">
                <span>查看文档</span>
                <span>📖</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="py-20 lg:py-24">
        <div class="max-w-6xl mx-auto px-6 lg:px-10">
          <!-- Section Header -->
          <div class="text-center mb-16">
            <div class="inline-flex items-center gap-2 mb-4">
              <div class="w-8 h-0.5 bg-gradient-to-r from-transparent to-cyan-neon"></div>
              <span class="text-cyan-neon text-sm font-bold tracking-widest uppercase">Core Features</span>
              <div class="w-8 h-0.5 bg-gradient-to-l from-transparent to-cyan-neon"></div>
            </div>
            <h2 class="text-4xl font-black text-white mb-4">核心功能</h2>
            <div class="w-32 h-1 bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon mx-auto rounded-full"></div>
          </div>
          
          <!-- Feature Cards Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <FeatureCard
              icon="👥"
              title="用户管理"
              description="校园身份认证、信用管理、权限控制，确保平台用户真实可信"
              tag="核心模块"
              color="cyan"
            />
            <FeatureCard
              icon="📦"
              title="物品管理"
              description="物品审核、分类管理、上下架控制，维护平台物品质量"
              tag="核心模块"
              color="fuchsia"
            />
            <FeatureCard
              icon="📋"
              title="订单监控"
              description="租赁/交易订单、状态跟踪、纠纷处理，保障交易安全"
              tag="核心模块"
              color="violet"
            />
            <FeatureCard
              icon="💰"
              title="资金管理"
              description="押金管理、租金结算、支付记录，财务数据一目了然"
              tag="核心模块"
              color="cyan"
            />
            <FeatureCard
              icon="📊"
              title="数据统计"
              description="用户活跃度、物品流转、财务分析，数据驱动决策"
              tag="数据模块"
              color="fuchsia"
            />
            <FeatureCard
              icon="⚙️"
              title="系统配置"
              description="轮播图、公告、分类字典，灵活配置平台参数"
              tag="配置模块"
              color="violet"
            />
          </div>
        </div>
      </section>

      <!-- Data Overview Section -->
      <section class="py-20 lg:py-24 bg-slate-900/50">
        <div class="max-w-6xl mx-auto px-6 lg:px-10">
          <!-- Section Header -->
          <div class="text-center mb-12">
            <div class="inline-flex items-center gap-2 mb-4">
              <div class="w-8 h-0.5 bg-gradient-to-r from-transparent to-acid-green"></div>
              <span class="text-acid-green text-sm font-bold tracking-widest uppercase">Live Data</span>
              <div class="w-8 h-0.5 bg-gradient-to-l from-transparent to-acid-green"></div>
            </div>
            <h2 class="text-3xl font-black text-white mb-4">实时数据概览</h2>
            <div class="w-24 h-1 bg-gradient-to-r from-acid-green to-cyan-neon mx-auto rounded-full"></div>
          </div>
          
          <!-- Data Table Card -->
          <div class="card-base overflow-hidden">
            <!-- Table Header -->
            <div class="p-6 border-b border-white/10 bg-gradient-to-r from-cyan-neon/5 to-fuchsia-500/5">
              <div class="flex items-center justify-between">
                <h3 class="text-xl font-bold text-white flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-acid-green animate-pulse"></span>
                  平台运营数据
                </h3>
                <span class="text-xs text-slate-400 font-mono">更新时间: {{ currentTime }}</span>
              </div>
            </div>
            
            <!-- Table -->
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead>
                  <tr class="text-left border-b border-white/10">
                    <th class="p-5 font-bold text-cyan-neon">
                      <div class="flex items-center gap-2">
                        <span>📊</span>
                        <span>指标</span>
                      </div>
                    </th>
                    <th class="p-5 font-bold text-cyan-neon">
                      <div class="flex items-center gap-2">
                        <span>📅</span>
                        <span>今日</span>
                      </div>
                    </th>
                    <th class="p-5 font-bold text-cyan-neon">
                      <div class="flex items-center gap-2">
                        <span>📆</span>
                        <span>本周</span>
                      </div>
                    </th>
                    <th class="p-5 font-bold text-cyan-neon">
                      <div class="flex items-center gap-2">
                        <span>🗓️</span>
                        <span>本月</span>
                      </div>
                    </th>
                    <th class="p-5 font-bold text-cyan-neon text-right">
                      <div class="flex items-center justify-end gap-2">
                        <span>📈</span>
                        <span>状态</span>
                      </div>
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-white/5">
                  <tr class="table-row">
                    <td class="p-5">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 rounded-xl bg-cyan-neon/10 flex items-center justify-center">
                          <span class="text-lg">👤</span>
                        </div>
                        <span class="text-slate-300 font-medium">用户数</span>
                      </div>
                    </td>
                    <td class="p-5 text-white font-bold text-lg">128</td>
                    <td class="p-5 text-white font-bold text-lg">856</td>
                    <td class="p-5 text-white font-bold text-lg">3,247</td>
                    <td class="p-5 text-right">
                      <span class="tag-base bg-acid-green/10 text-acid-green border-acid-green/30">
                        ↑ 上升
                      </span>
                    </td>
                  </tr>
                  <tr class="table-row bg-white/[0.02]">
                    <td class="p-5">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 rounded-xl bg-fuchsia-500/10 flex items-center justify-center">
                          <span class="text-lg">📦</span>
                        </div>
                        <span class="text-slate-300 font-medium">物品数</span>
                      </div>
                    </td>
                    <td class="p-5 text-white font-bold text-lg">45</td>
                    <td class="p-5 text-white font-bold text-lg">234</td>
                    <td class="p-5 text-white font-bold text-lg">1,089</td>
                    <td class="p-5 text-right">
                      <span class="tag-base bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30">
                        ● 活跃
                      </span>
                    </td>
                  </tr>
                  <tr class="table-row">
                    <td class="p-5">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 rounded-xl bg-violet-neon/10 flex items-center justify-center">
                          <span class="text-lg">📋</span>
                        </div>
                        <span class="text-slate-300 font-medium">订单数</span>
                      </div>
                    </td>
                    <td class="p-5 text-white font-bold text-lg">23</td>
                    <td class="p-5 text-white font-bold text-lg">156</td>
                    <td class="p-5 text-white font-bold text-lg">678</td>
                    <td class="p-5 text-right">
                      <span class="tag-base bg-cyan-neon/10 text-cyan-neon border-cyan-neon/30">
                        ● 正常
                      </span>
                    </td>
                  </tr>
                  <tr class="table-row bg-white/[0.02]">
                    <td class="p-5">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 rounded-xl bg-acid-green/10 flex items-center justify-center">
                          <span class="text-lg">💰</span>
                        </div>
                        <span class="text-slate-300 font-medium">交易额</span>
                      </div>
                    </td>
                    <td class="p-5 text-white font-bold text-lg">¥3,456</td>
                    <td class="p-5 text-white font-bold text-lg">¥28,901</td>
                    <td class="p-5 text-white font-bold text-lg">¥125,678</td>
                    <td class="p-5 text-right">
                      <span class="tag-base bg-fuchsia-500/10 text-fuchsia-500 border-fuchsia-500/30">
                        ↑ 增长
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </section>

      <!-- Quote Section -->
      <section class="py-20 lg:py-24 relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-r from-cyan-neon/5 via-fuchsia-500/5 to-violet-neon/5"></div>
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-radial from-cyan-neon/10 to-transparent rounded-full blur-3xl"></div>
        
        <div class="max-w-4xl mx-auto px-6 lg:px-10 text-center relative z-10">
          <div class="py-12">
            <div class="text-7xl mb-8 animate-float">⚡</div>
            <blockquote class="text-3xl md:text-4xl font-black mb-8">
              <span class="text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon">
                打造校园最酷的租赁交易平台
              </span>
            </blockquote>
            <div class="flex items-center justify-center gap-4 mb-6">
              <div class="w-16 h-0.5 bg-gradient-to-r from-transparent to-cyan-neon/50"></div>
              <p class="text-slate-400 text-lg tracking-widest uppercase">Retro Future · Efficient · Trusted</p>
              <div class="w-16 h-0.5 bg-gradient-to-l from-transparent to-fuchsia-500/50"></div>
            </div>
            <p class="text-slate-500">
              复古未来 · 便捷高效 · 安全可信
            </p>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="py-20 lg:py-24">
        <div class="max-w-5xl mx-auto px-6 lg:px-10">
          <div class="relative rounded-3xl overflow-hidden">
            <!-- Background -->
            <div class="absolute inset-0 bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon"></div>
            <div class="absolute inset-0 bg-gradient-to-br from-white/20 to-transparent"></div>
            
            <!-- Content -->
            <div class="relative bg-navy-deep/95 backdrop-blur-xl m-1 rounded-[22px] p-10 lg:p-16 text-center">
              <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-acid-green/10 border border-acid-green/30 mb-8">
                <span class="w-2 h-2 rounded-full bg-acid-green animate-pulse"></span>
                <span class="text-acid-green text-sm font-bold">🚀 BETA 版本</span>
              </div>
              
              <h2 class="text-4xl font-black text-white mb-4">准备好开始了吗？</h2>
              <p class="text-slate-400 text-lg mb-10 max-w-2xl mx-auto">
                让我们一起打造最酷的校园租赁交易管理平台，为校园生活带来更多便利
              </p>
              
              <button class="group px-12 py-5 font-black text-navy-deep bg-white rounded-full shadow-[0_0_40px_rgba(255,255,255,0.3)] hover:shadow-[0_0_60px_rgba(255,255,255,0.5)] transition-all duration-200 hover:scale-105 active:scale-98 flex items-center justify-center gap-3 mx-auto">
                <span class="text-lg">立即体验</span>
                <span class="transition-transform duration-200 group-hover:translate-x-1">→</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="py-8 border-t border-white/10">
        <div class="max-w-6xl mx-auto px-6 lg:px-10">
          <div class="flex flex-col md:flex-row items-center justify-between gap-4">
            <div class="flex items-center gap-2">
              <span class="text-slate-500 text-sm">© 2024 Campus Lease Trading System</span>
            </div>
            <div class="flex items-center gap-6">
              <a href="#" class="text-slate-500 hover:text-cyan-neon transition-colors text-sm">关于我们</a>
              <a href="#" class="text-slate-500 hover:text-cyan-neon transition-colors text-sm">帮助中心</a>
              <a href="#" class="text-slate-500 hover:text-cyan-neon transition-colors text-sm">隐私政策</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import FeatureCard from '../components/FeatureCard.vue'

const currentTime = ref('')
let timer: number | null = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>
