<template>
  <div class="auth-shell">
    <div class="auth-grid">
      <section class="auth-panel auth-form-panel auth-card">
        <div class="auth-header">
          <div class="auth-badge">后台管理</div>
          <div>
            <h2 class="auth-panel-title">后台账号登录</h2>
            <p class="auth-panel-text">请输入用户名和密码登录后台。</p>
          </div>
        </div>

        <form class="auth-form" @submit.prevent="handleLogin">
          <label class="field">
            <span class="field-label">用户名</span>
            <input
              v-model.trim="form.username"
              class="field-control"
              type="text"
              autocomplete="username"
              placeholder="请输入后台用户名"
            />
          </label>

          <label class="field">
            <span class="field-label">密码</span>
            <input
              v-model="form.password"
              class="field-control"
              type="password"
              autocomplete="current-password"
              placeholder="请输入后台密码"
            />
          </label>

          <button class="button button-primary auth-submit" type="submit" :disabled="submitting">
            {{ submitting ? '登录中...' : '进入后台' }}
          </button>
        </form>

        <p class="auth-note">首次登录后请尽快修改密码。</p>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminAuthApi } from '../api'
import { setAdminSession } from '../utils/auth'

const route = useRoute()
const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const submitting = ref(false)

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  submitting.value = true
  try {
    const res = await adminAuthApi.login({
      username: form.username,
      password: form.password
    })
    setAdminSession(res.token, res.userInfo)
    ElMessage.success('登录成功')

    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    router.replace(redirect)
  } catch (error) {
    console.error('后台登录失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>
