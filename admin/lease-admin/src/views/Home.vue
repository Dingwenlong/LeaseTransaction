<template>
  <div class="relative min-h-screen scanlines noise">
    <div class="relative z-10">
      <section class="relative overflow-hidden py-20 lg:py-24 bg-gradient-to-br from-navy-deep via-slate-900 to-navy-deep">
        <div class="absolute inset-0 bg-gradient-to-br from-cyan-neon/10 via-fuchsia-500/10 to-violet-neon/10 animate-pulse-glow"></div>
        <div class="max-w-6xl mx-auto px-6 lg:px-10 relative">
          <div class="relative z-10 text-center">
            <div class="inline-block mb-6 px-4 py-2 rounded-full border-2 border-cyan-neon/50 bg-cyan-neon/10 backdrop-blur-sm">
              <span class="text-cyan-neon text-sm font-semibold tracking-widest">Y2K ADMIN PANEL</span>
            </div>
            <h1 class="text-4xl md:text-5xl lg:text-6xl font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon mb-6 leading-tight">
              校园租赁<br />
              <span class="relative inline-block animate-float">交易管理</span>
              <span class="inline-block text-cyan-neon">系统</span>
            </h1>
            <p class="text-lg text-slate-300 max-w-2xl mx-auto mb-10 leading-relaxed">
              复古未来感的校园个人物品租赁与交易管理平台
            </p>
            <div class="flex flex-col sm:flex-row gap-4 justify-center">
              <button class="px-8 py-4 font-bold text-white bg-gradient-to-r from-cyan-neon to-fuchsia-500 rounded-full shadow-[0_0_30px_rgba(34,211,238,0.4)] hover:shadow-[0_0_50px_rgba(236,72,153,0.6)] transition-all duration-150 hover:scale-105 active:scale-98">
                开始管理
              </button>
              <button class="px-8 py-4 font-bold text-slate-300 border-2 border-fuchsia-500 rounded-full hover:bg-fuchsia-500/10 transition-all duration-150 hover:scale-105">
                查看文档
              </button>
            </div>
          </div>
        </div>
      </section>

      <section class="py-16 lg:py-20 bg-navy-deep">
        <div class="max-w-6xl mx-auto px-6 lg:px-10">
          <div class="text-center mb-12">
            <h2 class="text-3xl font-black text-white mb-4">核心功能</h2>
            <div class="w-32 h-1 bg-gradient-to-r from-cyan-neon to-fuchsia-500 mx-auto"></div>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <FeatureCard
              icon="👥"
              title="用户管理"
              description="校园身份认证、信用管理、权限控制"
              tag="核心模块"
            />
            <FeatureCard
              icon="📦"
              title="物品管理"
              description="物品审核、分类管理、上下架控制"
              tag="核心模块"
            />
            <FeatureCard
              icon="📋"
              title="订单监控"
              description="租赁/交易订单、状态跟踪、纠纷处理"
              tag="核心模块"
            />
            <FeatureCard
              icon="💰"
              title="资金管理"
              description="押金管理、租金结算、支付记录"
              tag="核心模块"
            />
            <FeatureCard
              icon="📊"
              title="数据统计"
              description="用户活跃度、物品流转、财务分析"
              tag="数据模块"
            />
            <FeatureCard
              icon="⚙️"
              title="系统配置"
              description="轮播图、公告、分类字典"
              tag="配置模块"
            />
          </div>
        </div>
      </section>

      <section class="py-16 lg:py-20 bg-slate-900">
        <div class="max-w-6xl mx-auto px-6 lg:px-10">
          <div class="rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm overflow-hidden">
            <div class="p-6 border-b border-white/10 bg-gradient-to-r from-cyan-neon/10 to-fuchsia-500/10">
              <h2 class="text-2xl font-bold text-white">实时数据概览</h2>
            </div>
            <div class="overflow-x-auto">
              <table class="w-full text-sm font-mono">
                <thead class="bg-gradient-to-r from-cyan-neon/5 to-fuchsia-500/5">
                  <tr class="text-left text-cyan-neon border-b border-white/10">
                    <th class="p-4 font-bold">指标</th>
                    <th class="p-4 font-bold">今日</th>
                    <th class="p-4 font-bold">本周</th>
                    <th class="p-4 font-bold">本月</th>
                    <th class="p-4 font-bold text-right">状态</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-white/10">
                  <tr class="hover:bg-white/5 transition-colors">
                    <td class="p-4 text-slate-300">用户数</td>
                    <td class="p-4 text-white">128</td>
                    <td class="p-4 text-white">856</td>
                    <td class="p-4 text-white">3,247</td>
                    <td class="p-4 text-right"><span class="px-2 py-1 rounded-full text-xs font-bold bg-acid-green/20 text-acid-green">上升</span></td>
                  </tr>
                  <tr class="hover:bg-white/5 transition-colors bg-white/[0.02]">
                    <td class="p-4 text-slate-300">物品数</td>
                    <td class="p-4 text-white">45</td>
                    <td class="p-4 text-white">234</td>
                    <td class="p-4 text-white">1,089</td>
                    <td class="p-4 text-right"><span class="px-2 py-1 rounded-full text-xs font-bold bg-acid-green/20 text-acid-green">活跃</span></td>
                  </tr>
                  <tr class="hover:bg-white/5 transition-colors">
                    <td class="p-4 text-slate-300">订单数</td>
                    <td class="p-4 text-white">23</td>
                    <td class="p-4 text-white">156</td>
                    <td class="p-4 text-white">678</td>
                    <td class="p-4 text-right"><span class="px-2 py-1 rounded-full text-xs font-bold bg-cyan-neon/20 text-cyan-neon">正常</span></td>
                  </tr>
                  <tr class="hover:bg-white/5 transition-colors bg-white/[0.02]">
                    <td class="p-4 text-slate-300">交易额</td>
                    <td class="p-4 text-white">¥3,456</td>
                    <td class="p-4 text-white">¥28,901</td>
                    <td class="p-4 text-white">¥125,678</td>
                    <td class="p-4 text-right"><span class="px-2 py-1 rounded-full text-xs font-bold bg-fuchsia-500/20 text-fuchsia-500">增长</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </section>

      <section class="py-16 lg:py-20 bg-navy-deep">
        <div class="max-w-4xl mx-auto px-6 lg:px-10 text-center relative overflow-hidden">
          <div class="absolute inset-0 bg-gradient-to-r from-cyan-neon/5 via-fuchsia-500/5 to-violet-neon/5"></div>
          <div class="relative z-10 py-12">
            <div class="text-6xl mb-6">⚡</div>
            <blockquote class="text-2xl md:text-3xl font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon mb-6">
              打造校园最酷的租赁交易平台
            </blockquote>
            <p class="text-slate-400 text-lg">
              复古未来 · 便捷高效 · 安全可信
            </p>
          </div>
        </div>
      </section>

      <section class="py-16 lg:py-20 bg-gradient-to-r from-cyan-neon via-fuchsia-500 to-violet-neon">
        <div class="max-w-4xl mx-auto px-6 lg:px-10 text-center">
          <div class="rounded-3xl bg-navy-deep/90 backdrop-blur-sm p-8 lg:p-12 border-2 border-white/20">
            <h2 class="text-3xl font-black text-white mb-4">准备好开始了吗？</h2>
            <p class="text-slate-300 text-lg mb-8 max-w-2xl mx-auto">
              让我们一起打造最酷的校园租赁交易管理平台
            </p>
            <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-acid-green/20 border border-acid-green/50 mb-6">
              <span class="text-acid-green text-sm font-bold">🚀 BETA 版本</span>
            </div>
            <button class="px-10 py-4 font-black text-navy-deep bg-white rounded-full shadow-[0_0_40px_rgba(255,255,255,0.3)] hover:shadow-[0_0_60px_rgba(255,255,255,0.5)] transition-all duration-150 hover:scale-105 active:scale-98">
              立即体验
            </button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import FeatureCard from '../components/FeatureCard.vue'
</script>
