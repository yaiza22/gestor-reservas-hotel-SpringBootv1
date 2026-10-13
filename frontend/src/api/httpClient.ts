import axios, { AxiosError } from 'axios'
import type { ApiError, CodigoError, ErrorCampo } from '../types/common'

/** Backend de gestión: clientes, usuarios y reservas (:8080). */
export const gestionApi = axios.create({
  baseURL: import.meta.env.VITE_GESTION_API_URL,
})

/** Microservicio de habitaciones (:8081). */
export const habitacionesApi = axios.create({
  baseURL: import.meta.env.VITE_HABITACIONES_API_URL,
})

const CODIGOS_CONOCIDOS: CodigoError[] = [
  'VALIDACION',
  'REFERENCIA_INEXISTENTE',
  'NO_ENCONTRADO',
  'CONFLICTO',
  'ERROR_INTERNO',
  'SERVICIO_NO_DISPONIBLE',
]

/** Comprueba que un valor `unknown` sea un objeto, para leerlo sin usar `any`. */
function esObjeto(valor: unknown): valor is Record<string, unknown> {
  return typeof valor === 'object' && valor !== null
}

/** Lee la lista `errores[]` del cuerpo del error y descarta lo que no tenga la forma { campo, mensaje }. */
function leerErrores(valor: unknown): ErrorCampo[] | undefined {
  if (!Array.isArray(valor)) return undefined
  const lista: ErrorCampo[] = []
  for (const item of valor) {
    if (esObjeto(item) && typeof item.campo === 'string' && typeof item.mensaje === 'string') {
      lista.push({ campo: item.campo, mensaje: item.mensaje })
    }
  }
  return lista
}

/** Convierte cualquier fallo de Axios en un `ApiError` (si no hay respuesta, el código es SIN_CONEXION). */
export function normalizarError(error: unknown): ApiError {
  if (!(error instanceof AxiosError) || !error.response) {
    return {
      status: 0,
      title: 'Sin conexión',
      detail: 'No se pudo comunicar con el servidor.',
      codigo: 'SIN_CONEXION',
    }
  }
  const { status, data } = error.response
  const cuerpo = esObjeto(data) ? data : {}
  const codigoRecibido = cuerpo.codigo
  const codigo: CodigoError =
    typeof codigoRecibido === 'string' && CODIGOS_CONOCIDOS.includes(codigoRecibido as CodigoError)
      ? (codigoRecibido as CodigoError)
      : 'ERROR_INTERNO'
  return {
    status,
    title: typeof cuerpo.title === 'string' ? cuerpo.title : 'Error',
    detail: typeof cuerpo.detail === 'string' ? cuerpo.detail : 'Ocurrió un error inesperado.',
    codigo,
    errores: leerErrores(cuerpo.errores),
  }
}

for (const instancia of [gestionApi, habitacionesApi]) {
  instancia.interceptors.response.use(
    (respuesta) => respuesta,
    (error: unknown) => Promise.reject(normalizarError(error)),
  )
}
