import type { ApiError } from '../types/common'
import { MENSAJE_ERROR_INESPERADO } from './constantes'

/** Comprueba que un error capturado (`unknown`) sea el `ApiError` que arma el interceptor. */
export function esApiError(valor: unknown): valor is ApiError {
  return (
    typeof valor === 'object' &&
    valor !== null &&
    'codigo' in valor &&
    'status' in valor &&
    'detail' in valor
  )
}

/** Si el error no es un `ApiError` (fallo del propio código), lo trata como inesperado. */
export function comoApiError(valor: unknown): ApiError {
  if (esApiError(valor)) return valor
  return { status: 500, title: 'Error', detail: MENSAJE_ERROR_INESPERADO, codigo: 'ERROR_INTERNO' }
}

/** Pasa la lista `errores[]` del servidor a un objeto { campo: mensaje } para mostrarlo bajo cada campo. */
export function erroresPorCampo(error: ApiError): Record<string, string> {
  const resultado: Record<string, string> = {}
  for (const item of error.errores ?? []) {
    resultado[item.campo] = item.mensaje
  }
  return resultado
}

/**
 * Texto para la alerta según el código del error (CONVENCIONES.md §6.4).
 * @param servicio nombre del servicio que se estaba llamando, para el caso SIN_CONEXION
 */
export function mensajeDeError(error: ApiError, servicio: string): string {
  switch (error.codigo) {
    case 'SIN_CONEXION':
      return `No se pudo comunicar con el servicio de ${servicio}. Intenta de nuevo.`
    case 'SERVICIO_NO_DISPONIBLE':
      // Gestión avisa que no pudo hablar con el microservicio de habitaciones
      return 'No se pudo comunicar con el servicio de habitaciones. Intenta de nuevo.'
    case 'ERROR_INTERNO':
      return MENSAJE_ERROR_INESPERADO
    default:
      return error.detail
  }
}
