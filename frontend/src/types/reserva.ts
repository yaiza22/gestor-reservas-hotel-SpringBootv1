/** Estados de una reserva (MODELO-DE-DATOS.md §3). */
export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'ENCURSO' | 'FINALIZADA' | 'CANCELADA'

/** Temporadas que cambian el precio por noche (MODELO-DE-DATOS.md §3). */
export type Temporada = 'BAJA' | 'ALTA'

/** Reserva tal como la devuelve el backend de gestión (API.md §4.3). Solo guarda ids. */
export interface Reserva {
  id: number
  clienteId: number
  habitacionesId: number[]
  /** Fechas en formato yyyy-MM-dd. */
  fechaInicio: string
  fechaFin: string
  noches: number
  cantHuespedes: number
  temporada: Temporada
  estado: EstadoReserva
  precioTotal: number
  /** HH:mm:ss; vale `null` hasta el check-in. */
  horaEntrada: string | null
  /** HH:mm:ss; vale `null` hasta el check-out. */
  horaSalida: string | null
}

/** Cuerpo del POST /reservas. */
export interface ReservaRequest {
  clienteId: number
  habitacionesId: number[]
  fechaInicio: string
  fechaFin: string
  cantHuespedes: number
  temporada: Temporada
}
