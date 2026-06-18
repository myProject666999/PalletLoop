import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'equipment-types',
        name: 'EquipmentType',
        component: () => import('@/views/EquipmentType.vue')
      },
      {
        path: 'equipments',
        name: 'Equipment',
        component: () => import('@/views/Equipment.vue')
      },
      {
        path: 'partners',
        name: 'Partner',
        component: () => import('@/views/Partner.vue')
      },
      {
        path: 'dispatches',
        name: 'Dispatch',
        component: () => import('@/views/Dispatch.vue')
      },
      {
        path: 'returns',
        name: 'Return',
        component: () => import('@/views/Return.vue')
      },
      {
        path: 'alerts',
        name: 'Alert',
        component: () => import('@/views/Alert.vue')
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
