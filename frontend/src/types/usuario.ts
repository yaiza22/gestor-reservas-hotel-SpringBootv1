import type { TipoDocIdentidad } from './common'

/** Roles de un usuario (MODELO-DE-DATOS.md §3). */
export type Rol = 'ADMINISTRADOR' | 'ASISTENTE'

/** Usuario (empleado del hotel) tal como lo devuelve la API; nunca trae la contraseña (RN-13). */
export interface Usuario {
  id: number
  tipoDocIdentidad: TipoDocIdentidad
  numDocIdentidad: string
  nombre: string
  telefono: string
  correo: string
  rol: Rol
}

/** Cuerpo del POST /usuarios. */
export interface UsuarioRequest {
  tipoDocIdentidad: TipoDocIdentidad
  numDocIdentidad: string
  nombre: string
  telefono: string
  correo: string
  password: string
  rol: Rol
}

/** Cuerpo del PUT /usuarios/{id}: el documento y la contraseña no se editan. */
export interface UsuarioUpdateRequest {
  nombre: string
  telefono: string
  correo: string
  rol: Rol
}
