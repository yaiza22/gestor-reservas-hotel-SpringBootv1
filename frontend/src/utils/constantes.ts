import type { TipoDocIdentidad } from '../types/common'
import type { EstadoHabitacion, TipoHabitacion } from '../types/habitacion'
import type { EstadoReserva, Temporada } from '../types/reserva'
import type { Rol } from '../types/usuario'

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

/** Opciones de la lista «Rol» (MODELO-DE-DATOS.md §3). */
export const ROLES: { valor: Rol; etiqueta: string }[] = [
  { valor: 'ADMINISTRADOR', etiqueta: 'Administrador' },
  { valor: 'ASISTENTE', etiqueta: 'Asistente' },
]

/** Opciones de la lista «Tipo de habitación» (MODELO-DE-DATOS.md §3). */
export const TIPOS_HABITACION: { valor: TipoHabitacion; etiqueta: string }[] = [
  { valor: 'INDIVIDUAL', etiqueta: 'Individual' },
  { valor: 'DOBLE', etiqueta: 'Doble' },
  { valor: 'TWIN', etiqueta: 'Twin' },
  { valor: 'TRIPLE', etiqueta: 'Triple' },
  { valor: 'FAMILIAR', etiqueta: 'Familiar' },
]

/** Estados de una habitación, en el orden en que se muestran en listas y filtros. */
export const ESTADOS_HABITACION: EstadoHabitacion[] = ['DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO', 'LIMPIEZA']

/** Opciones de la lista «Temporada» (MODELO-DE-DATOS.md §3). */
export const TEMPORADAS: { valor: Temporada; etiqueta: string }[] = [
  { valor: 'BAJA', etiqueta: 'Baja' },
  { valor: 'ALTA', etiqueta: 'Alta' },
]

/** Estados de una reserva, en el orden en que se muestran en el filtro. */
export const ESTADOS_RESERVA: EstadoReserva[] = ['PENDIENTE', 'CONFIRMADA', 'ENCURSO', 'FINALIZADA', 'CANCELADA']
