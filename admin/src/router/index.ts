import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { authState, isAdminAuthenticated } from '../utils/auth'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'AdminLogin',
    component: () => import('../views/AdminLogin.vue'),
    meta: {
      public: true
    }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('../views/Users.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/system-users',
    name: 'SystemUsers',
    component: () => import('../views/SystemUsers.vue'),
    meta: {
      requiresAuth: true,
      superAdminOnly: true
    }
  },
  {
    path: '/items',
    name: 'Items',
    component: () => import('../views/Items.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('../views/Reviews.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/stats',
    name: 'Stats',
    component: () => import('../views/Stats.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/config',
    name: 'Config',
    component: () => import('../views/SystemConfig.vue'),
    meta: {
      requiresAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const authenticated = isAdminAuthenticated()

  if (to.meta.public) {
    if (authenticated && to.path === '/login') {
      const redirect = typeof to.query.redirect === 'string' ? to.query.redirect : '/'
      return redirect
    }
    return true
  }

  if (!authenticated) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath
      }
    }
  }

  if (to.meta.superAdminOnly && authState.user?.role !== 'SUPER_ADMIN') {
    return '/'
  }

  return true
})

export default router
