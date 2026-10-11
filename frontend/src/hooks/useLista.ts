import { useCallback, useEffect, useState } from 'react'
import type { ApiError } from '../types/common'
import { comoApiError } from '../utils/errores'

interface ResultadoLista<T> {
  datos: T[]
  cargando: boolean
  error: ApiError | null
  /** Vuelve a pedir la lista (se usa después de crear, editar o eliminar). */
  recargar: () => void
}

/**
 * Pide una lista al servidor y lleva la cuenta de `cargando` y `error`.
 * @param traer función de `api/` que devuelve la lista; debe ser una función estable (no una creada dentro del componente)
 */
export function useLista<T>(traer: () => Promise<T[]>): ResultadoLista<T> {
  const [datos, setDatos] = useState<T[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<ApiError | null>(null)
  const [version, setVersion] = useState(0)

  useEffect(() => {
    let vigente = true
    traer()
      .then((lista) => {
        if (!vigente) return
        setDatos(lista)
        setError(null)
      })
      .catch((e: unknown) => {
        if (vigente) setError(comoApiError(e))
      })
      .finally(() => {
        if (vigente) setCargando(false)
      })
    return () => {
      vigente = false
    }
  }, [traer, version])

  const recargar = useCallback(() => {
    setCargando(true)
    setVersion((v) => v + 1)
  }, [])

  return { datos, cargando, error, recargar }
}
