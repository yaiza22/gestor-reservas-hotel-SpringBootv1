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
