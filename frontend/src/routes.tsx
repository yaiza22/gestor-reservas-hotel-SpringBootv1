import { Navigate, Route, Routes } from 'react-router'
import { MainLayout } from './layouts/MainLayout'
import { ClientesPage } from './pages/clientes/ClientesPage'
import { HabitacionesPage } from './pages/habitaciones/HabitacionesPage'
import { ReservasPage } from './pages/reservas/ReservasPage'
import { UsuariosPage } from './pages/usuarios/UsuariosPage'

/** Tabla de rutas: la raíz «/» lleva a «/reservas». */
export function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Navigate to="/reservas" replace />} />
        <Route path="/reservas" element={<ReservasPage />} />
        <Route path="/habitaciones" element={<HabitacionesPage />} />
        <Route path="/clientes" element={<ClientesPage />} />
        <Route path="/usuarios" element={<UsuariosPage />} />
        <Route path="*" element={<Navigate to="/reservas" replace />} />
      </Route>
    </Routes>
  )
}
