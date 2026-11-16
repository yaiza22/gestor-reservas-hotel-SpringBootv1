import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import ClientesView from '@/views/clientes/ClientesView.vue'
import HabitacionesView from '@/views/habitaciones/HabitacionesView.vue'
import ReservasView from '@/views/reservas/ReservasView.vue'
import UsuariosView from '@/views/usuarios/UsuariosView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', redirect: '/reservas' },
        { path: 'reservas', component: ReservasView },
        { path: 'habitaciones', component: HabitacionesView },
        { path: 'clientes', component: ClientesView },
        { path: 'usuarios', component: UsuariosView },
        { path: ':pathMatch(.*)*', redirect: '/reservas' },
      ],
    },
  ],
})

export default router
