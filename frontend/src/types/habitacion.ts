/** Estados de una habitación (MODELO-DE-DATOS.md §3). */
export type EstadoHabitacion = 'DISPONIBLE' | 'OCUPADA' | 'MANTENIMIENTO' | 'LIMPIEZA'

/** Tipos de habitación (MODELO-DE-DATOS.md §3). */
export type TipoHabitacion = 'DOBLE' | 'INDIVIDUAL' | 'TWIN' | 'TRIPLE' | 'FAMILIAR'

/** Habitación tal como la devuelve el microservicio (MODELO-DE-DATOS.md §5). */
export interface Habitacion {
  id: number
  numero: string
  tipoHabitacion: TipoHabitacion
  piso: number
  precioBase: number
  capacidad: number
  estado: EstadoHabitacion
}

/** Cuerpo del POST /habitaciones. El estado es opcional: si no se envía queda DISPONIBLE. */
export interface HabitacionRequest {
  numero: string
  tipoHabitacion: TipoHabitacion
  piso: number
  precioBase: number
  capacidad: number
  estado?: EstadoHabitacion
}

/** Cuerpo del PUT /habitaciones/{id}: el número no se edita. */
export interface HabitacionUpdateRequest {
  tipoHabitacion: TipoHabitacion
  piso: number
  precioBase: number
  capacidad: number
  estado?: EstadoHabitacion
}
