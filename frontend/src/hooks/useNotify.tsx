import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { Alert, Snackbar } from '@mui/material'

type Gravedad = 'success' | 'error'

interface Mensaje {
  texto: string
  gravedad: Gravedad
}

interface NotifyContextValue {
  exito: (texto: string) => void
  error: (texto: string) => void
}

const NotifyContext = createContext<NotifyContextValue | null>(null)

/** Reparte los avisos breves (éxito o error) a toda la aplicación y los muestra abajo. */
export function NotifyProvider({ children }: { children: ReactNode }) {
  const [mensaje, setMensaje] = useState<Mensaje | null>(null)
  const [abierto, setAbierto] = useState(false)

  const mostrar = useCallback((texto: string, gravedad: Gravedad) => {
    setMensaje({ texto, gravedad })
    setAbierto(true)
  }, [])

  const valor = useMemo<NotifyContextValue>(
    () => ({
      exito: (texto) => mostrar(texto, 'success'),
      error: (texto) => mostrar(texto, 'error'),
    }),
    [mostrar],
  )

  return (
    <NotifyContext.Provider value={valor}>
      {children}
      <Snackbar
        open={abierto}
        autoHideDuration={4000}
        onClose={() => setAbierto(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity={mensaje?.gravedad ?? 'success'} variant="filled" onClose={() => setAbierto(false)}>
          {mensaje?.texto}
        </Alert>
      </Snackbar>
    </NotifyContext.Provider>
  )
}

/** Da acceso a `exito(texto)` y `error(texto)` desde cualquier componente. */
// eslint-disable-next-line react-refresh/only-export-components
export function useNotify(): NotifyContextValue {
  const contexto = useContext(NotifyContext)
  if (!contexto) {
    throw new Error('useNotify debe usarse dentro de NotifyProvider')
  }
  return contexto
}
