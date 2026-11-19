import type { EstadoReserva, Reserva, ReservaRequest } from '../types/reserva'
import { gestionApi } from './httpClient'

/** Filtros opcionales de la lista (API.md §4.3). Si un filtro no viene, no se envía. */
export interface FiltrosReservas {
  estado?: EstadoReserva
  clienteId?: number
}

/** Trae las reservas; con filtros, solo las que cumplen todos (GET /reservas?estado=&clienteId=). */
export async function listarReservas(filtros: FiltrosReservas = {}): Promise<Reserva[]> {
  const { data } = await gestionApi.get<Reserva[]>('/reservas', { params: filtros })
  return data
}

/** Crea una reserva en estado PENDIENTE; el servidor calcula el precio (POST /reservas). */
export async function crearReserva(cuerpo: ReservaRequest): Promise<Reserva> {
  const { data } = await gestionApi.post<Reserva>('/reservas', cuerpo)
  return data
}

/** Edita una reserva PENDIENTE o CONFIRMADA y el servidor recalcula el precio (PUT /reservas/{id}). */
export async function actualizarReserva(id: number, cuerpo: ReservaRequest): Promise<Reserva> {
  const { data } = await gestionApi.put<Reserva>(`/reservas/${id}`, cuerpo)
  return data
}

/** PENDIENTE → CONFIRMADA (POST /reservas/{id}/confirmar). */
export async function confirmarReserva(id: number): Promise<Reserva> {
  const { data } = await gestionApi.post<Reserva>(`/reservas/${id}/confirmar`)
  return data
}

/** CONFIRMADA → ENCURSO; las habitaciones quedan OCUPADA (POST /reservas/{id}/check-in). */
export async function registrarCheckIn(id: number): Promise<Reserva> {
  const { data } = await gestionApi.post<Reserva>(`/reservas/${id}/check-in`)
  return data
}

/** ENCURSO → FINALIZADA; las habitaciones pasan a LIMPIEZA (POST /reservas/{id}/check-out). */
export async function registrarCheckOut(id: number): Promise<Reserva> {
  const { data } = await gestionApi.post<Reserva>(`/reservas/${id}/check-out`)
  return data
}

/** PENDIENTE o CONFIRMADA → CANCELADA (POST /reservas/{id}/cancelar). */
export async function cancelarReserva(id: number): Promise<Reserva> {
  const { data } = await gestionApi.post<Reserva>(`/reservas/${id}/cancelar`)
  return data
}

/** Elimina una reserva; el servidor responde 409 si está en curso (DELETE /reservas/{id}). */
export async function eliminarReserva(id: number): Promise<void> {
  await gestionApi.delete(`/reservas/${id}`)
}
