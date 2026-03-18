<template>
  <section class="page-header">
    <div class="page-header-main">
      <p class="page-eyebrow">Config</p>
      <h1 class="page-title">首页内容、分类字典和风控规则统一配置。</h1>
      <p class="page-description">统一维护首页内容和基础字典。</p>
    </div>
    <div class="page-actions">
      <button class="button button-ghost" @click="loadConfig">重新加载</button>
      <button class="button button-primary" @click="saveConfig">
        <span>保存配置</span>
        <span>✓</span>
      </button>
    </div>
  </section>

  <section class="config-grid">
    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">轮播配置</h2>
          <p class="panel-subtitle">维护首页轮播。</p>
        </div>
        <button class="button button-ghost button-sm" @click="addBanner">新增</button>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="(banner, index) in form.banners" :key="`banner-${index}`" class="config-card">
            <input v-model="banner.title" class="field-control" type="text" placeholder="轮播标题" />
            <textarea v-model="banner.subtitle" class="config-textarea" placeholder="轮播副标题"></textarea>
            <div class="inline-actions">
              <label class="toggle-line">
                <input v-model="banner.active" type="checkbox" />
                <span>启用</span>
              </label>
              <button class="button button-danger button-sm" @click="removeBanner(index)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </article>

    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">公告配置</h2>
          <p class="panel-subtitle">维护系统公告。</p>
        </div>
        <button class="button button-ghost button-sm" @click="addAnnouncement">新增</button>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="(item, index) in form.announcements" :key="`notice-${index}`" class="config-card">
            <input v-model="item.title" class="field-control" type="text" placeholder="公告标题" />
            <textarea v-model="item.content" class="config-textarea" placeholder="公告内容"></textarea>
            <div class="inline-actions">
              <button class="button button-danger button-sm" @click="removeAnnouncement(index)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </article>
  </section>

  <section class="card-grid two">
    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">分类与校区</h2>
          <p class="panel-subtitle">维护分类和校区字典。</p>
        </div>
      </div>
      <div class="panel-body">
        <div class="form-grid">
          <label class="field">
            <span class="field-label">分类字典</span>
            <textarea v-model="categoryText" class="config-textarea" placeholder="每行一个分类"></textarea>
          </label>
          <label class="field">
            <span class="field-label">校区字典</span>
            <textarea v-model="campusText" class="config-textarea" placeholder="每行一个校区"></textarea>
          </label>
        </div>
      </div>
    </article>

    <article class="panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">风控规则</h2>
          <p class="panel-subtitle">维护风控开关。</p>
        </div>
        <button class="button button-ghost button-sm" @click="addRiskRule">新增</button>
      </div>
      <div class="panel-body">
        <div class="list-stack">
          <div v-for="(rule, index) in form.riskRules" :key="`risk-${index}`" class="list-item">
            <div class="field" style="flex: 1;">
              <span class="field-label">规则名称</span>
              <input v-model="rule.name" class="field-control" type="text" placeholder="输入规则名称" />
            </div>
            <label class="toggle-line">
              <input v-model="rule.enabled" type="checkbox" />
              <span>{{ rule.enabled ? '已启用' : '已关闭' }}</span>
            </label>
            <button class="button button-danger button-sm" @click="removeRiskRule(index)">删除</button>
          </div>
        </div>
      </div>
    </article>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { configApi, type SystemConfig } from '../api'

const form = reactive<SystemConfig>({
  banners: [],
  announcements: [],
  categories: [],
  campuses: [],
  riskRules: []
})

const categoryText = computed({
  get: () => form.categories.join('\n'),
  set: (value: string) => {
    form.categories = value.split('\n').map((item) => item.trim()).filter(Boolean)
  }
})

const campusText = computed({
  get: () => form.campuses.join('\n'),
  set: (value: string) => {
    form.campuses = value.split('\n').map((item) => item.trim()).filter(Boolean)
  }
})

const applyConfig = (config: SystemConfig) => {
  form.banners = config.banners ? [...config.banners] : []
  form.announcements = config.announcements ? [...config.announcements] : []
  form.categories = config.categories ? [...config.categories] : []
  form.campuses = config.campuses ? [...config.campuses] : []
  form.riskRules = config.riskRules ? [...config.riskRules] : []
}

const loadConfig = async () => {
  const res = await configApi.getSystem()
  applyConfig(res)
}

const saveConfig = async () => {
  await configApi.saveSystem({
    banners: form.banners,
    announcements: form.announcements,
    categories: form.categories,
    campuses: form.campuses,
    riskRules: form.riskRules
  })
  ElMessage.success('系统配置已保存')
}

const addBanner = () => {
  form.banners.push({ title: '', subtitle: '', active: true })
}

const removeBanner = (index: number) => {
  form.banners.splice(index, 1)
}

const addAnnouncement = () => {
  form.announcements.push({ title: '', content: '' })
}

const removeAnnouncement = (index: number) => {
  form.announcements.splice(index, 1)
}

const addRiskRule = () => {
  form.riskRules.push({ name: '', enabled: true })
}

const removeRiskRule = (index: number) => {
  form.riskRules.splice(index, 1)
}

onMounted(() => {
  loadConfig().catch(() => {
    ElMessage.warning('加载系统配置失败，已显示空白配置')
  })
})
</script>
