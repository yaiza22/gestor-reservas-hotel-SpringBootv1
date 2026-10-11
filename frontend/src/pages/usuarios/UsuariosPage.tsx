import { EmptyState } from '../../components/EmptyState'
import { PageHeader } from '../../components/PageHeader'

/** Pantalla de Usuarios (por ahora vacía; se completa en su propia rama). */
export function UsuariosPage() {
  return (
    <>
      <PageHeader titulo="Usuarios" />
      <EmptyState mensaje="Esta pantalla se construirá próximamente." />
    </>
  )
}
