import type { TipoDocIdentidad } from './common'

/** Cliente tal como lo devuelve la API (nunca trae la contraseña, RN-13). */
export interface Cliente {
  id: number
  tipoDocIdentidad: TipoDocIdentidad
  numDocIdentidad: string
  nombre: string
  telefono: string
  correo: string
  nacionalidad: string
  paisResidencia: string
}

/** Cuerpo del POST /clientes. */
export interface ClienteRequest {
  tipoDocIdentidad: TipoDocIdentidad
  numDocIdentidad: string
  nombre: string
  telefono: string
  correo: string
  password: string
  nacionalidad: string
  paisResidencia: string
}

/** Cuerpo del PUT /clientes/{id}: el documento y la contraseña no se editan. */
export interface ClienteUpdateRequest {
  nombre: string
  telefono: string
  correo: string
  nacionalidad: string
  paisResidencia: string
}
