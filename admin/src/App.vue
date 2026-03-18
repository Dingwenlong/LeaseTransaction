<script setup lang="ts">
import Layout from './components/Layout.vue'
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView v-slot="{ Component, route }">
    <Layout v-if="!route.meta.public">
      <component :is="Component" />
    </Layout>
    <component :is="Component" v-else />
  </RouterView>
</template>

<style scoped>
</style>
