import { EmptyState } from '../../components/EmptyState'
import { PageHeader } from '../../components/PageHeader'

/** Pantalla de Clientes (por ahora vacía; se completa en su propia rama). */
export function ClientesPage() {
  return (
    <>
      <PageHeader titulo="Clientes" />
      <EmptyState mensaje="Esta pantalla se construirá próximamente." />
    </>
  )
}
