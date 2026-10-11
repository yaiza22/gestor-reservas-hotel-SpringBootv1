import type {
  EstadoHabitacion,
  Habitacion,
  HabitacionRequest,
  HabitacionUpdateRequest,
} from '../types/habitacion'
import { habitacionesApi } from './httpClient'

/** Trae todas las habitaciones (GET /habitaciones). */
export async function listarHabitaciones(): Promise<Habitacion[]> {
  const { data } = await habitacionesApi.get<Habitacion[]>('/habitaciones')
  return data
}

/** Trae una habitación por su id (GET /habitaciones/{id}). */
export async function obtenerHabitacion(id: number): Promise<Habitacion> {
  const { data } = await habitacionesApi.get<Habitacion>(`/habitaciones/${id}`)
  return data
}

/** Crea una habitación (POST /habitaciones). */
export async function crearHabitacion(cuerpo: HabitacionRequest): Promise<Habitacion> {
  const { data } = await habitacionesApi.post<Habitacion>('/habitaciones', cuerpo)
  return data
}

/** Actualiza una habitación (PUT /habitaciones/{id}). */
export async function actualizarHabitacion(id: number, cuerpo: HabitacionUpdateRequest): Promise<Habitacion> {
  const { data } = await habitacionesApi.put<Habitacion>(`/habitaciones/${id}`, cuerpo)
  return data
}

/** Cambia solo el estado (PATCH /habitaciones/{id}/estado). */
export async function cambiarEstadoHabitacion(id: number, estado: EstadoHabitacion): Promise<Habitacion> {
  const { data } = await habitacionesApi.patch<Habitacion>(`/habitaciones/${id}/estado`, { estado })
  return data
}

/** Elimina una habitación (DELETE /habitaciones/{id}); el servidor responde 409 si está ocupada. */
export async function eliminarHabitacion(id: number): Promise<void> {
  await habitacionesApi.delete(`/habitaciones/${id}`)
}
