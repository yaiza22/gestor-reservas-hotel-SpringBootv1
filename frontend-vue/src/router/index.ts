import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import HabitacionesView from '@/views/habitaciones/HabitacionesView.vue'

/** Interfaz de habitaciones: una sola pantalla; la raíz «/» y cualquier ruta desconocida llevan a «/habitaciones». */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', redirect: '/habitaciones' },
        { path: 'habitaciones', component: HabitacionesView },
        { path: ':pathMatch(.*)*', redirect: '/habitaciones' },
      ],
    },
  ],
})

export default router
