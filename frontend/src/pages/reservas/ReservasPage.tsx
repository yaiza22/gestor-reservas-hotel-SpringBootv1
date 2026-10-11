import { EmptyState } from '../../components/EmptyState'
import { PageHeader } from '../../components/PageHeader'

/** Pantalla de Reservas (por ahora vacía; se completa en su propia rama). */
export function ReservasPage() {
  return (
    <>
      <PageHeader titulo="Reservas" />
      <EmptyState mensaje="Esta pantalla se construirá próximamente." />
    </>
  )
}
