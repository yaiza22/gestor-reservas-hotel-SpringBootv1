import type { Reserva, ReservaRequest } from '../types/reserva'
import { gestionApi } from './httpClient'

/** Trae todas las reservas (GET /reservas). */
export async function listarReservas(): Promise<Reserva[]> {
  const { data } = await gestionApi.get<Reserva[]>('/reservas')
  return data
}

/** Crea una reserva en estado PENDIENTE; el servidor calcula el precio (POST /reservas). */
export async function crearReserva(cuerpo: ReservaRequest): Promise<Reserva> {
  const { data } = await gestionApi.post<Reserva>('/reservas', cuerpo)
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
