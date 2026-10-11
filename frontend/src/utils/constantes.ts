import type { TipoDocIdentidad } from '../types/common'

/** Texto del menú superior y su ruta (ARQUITECTURA.md §3.3). */
export const SECCIONES = [
  { ruta: '/reservas', etiqueta: 'Reservas' },
  { ruta: '/habitaciones', etiqueta: 'Habitaciones' },
  { ruta: '/clientes', etiqueta: 'Clientes' },
  { ruta: '/usuarios', etiqueta: 'Usuarios' },
] as const

export const NOMBRE_APP = 'Gestor de Reservas de Hotel'

/** Mensaje genérico para errores 500 (CONVENCIONES.md §6.4): sin detalles técnicos. */
export const MENSAJE_ERROR_INESPERADO = 'Ocurrió un error inesperado.'

/** Opciones de la lista «Tipo de documento» (MODELO-DE-DATOS.md §3). */
export const TIPOS_DOCUMENTO: { valor: TipoDocIdentidad; etiqueta: string }[] = [
  { valor: 'CC', etiqueta: 'Cédula de ciudadanía' },
  { valor: 'CE', etiqueta: 'Cédula de extranjería' },
  { valor: 'PA', etiqueta: 'Pasaporte' },
  { valor: 'PEP', etiqueta: 'Permiso especial de permanencia' },
  { valor: 'RC', etiqueta: 'Registro civil' },
  { valor: 'TI', etiqueta: 'Tarjeta de identidad' },
]
