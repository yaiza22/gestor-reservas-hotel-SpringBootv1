import { EmptyState } from '../../components/EmptyState'
import { PageHeader } from '../../components/PageHeader'

/** Pantalla de Habitaciones (por ahora vacía; se completa en su propia rama). */
export function HabitacionesPage() {
  return (
    <>
      <PageHeader titulo="Habitaciones" />
      <EmptyState mensaje="Esta pantalla se construirá próximamente." />
    </>
  )
}
