/** Tipos de documento de identidad (MODELO-DE-DATOS.md §3). */
export type TipoDocIdentidad = 'PA' | 'CC' | 'CE' | 'PEP' | 'RC' | 'TI'

/** Una línea de error de campo, tal como la envía el backend (API.md §2.3). */
export interface ErrorCampo {
  campo: string
  mensaje: string
}

/** Códigos de error que devuelven los backends, más SIN_CONEXION que solo existe en el frontend. */
export type CodigoError =
  | 'VALIDACION'
  | 'REFERENCIA_INEXISTENTE'
  | 'NO_ENCONTRADO'
  | 'CONFLICTO'
  | 'ERROR_INTERNO'
  | 'SERVICIO_NO_DISPONIBLE'
  | 'SIN_CONEXION'

/** Error normalizado: todas las pantallas reciben siempre esta forma. */
export interface ApiError {
  status: number
  title: string
  detail: string
  codigo: CodigoError
  errores?: ErrorCampo[]
}
