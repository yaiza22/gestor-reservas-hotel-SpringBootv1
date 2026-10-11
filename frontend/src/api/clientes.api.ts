import type { Cliente, ClienteRequest, ClienteUpdateRequest } from '../types/cliente'
import { gestionApi } from './httpClient'

/** Trae todos los clientes (GET /clientes). */
export async function listarClientes(): Promise<Cliente[]> {
  const { data } = await gestionApi.get<Cliente[]>('/clientes')
  return data
}

/** Trae un cliente por su id (GET /clientes/{id}). */
export async function obtenerCliente(id: number): Promise<Cliente> {
  const { data } = await gestionApi.get<Cliente>(`/clientes/${id}`)
  return data
}

/** Crea un cliente (POST /clientes). */
export async function crearCliente(cuerpo: ClienteRequest): Promise<Cliente> {
  const { data } = await gestionApi.post<Cliente>('/clientes', cuerpo)
  return data
}

/** Actualiza los datos editables de un cliente (PUT /clientes/{id}). */
export async function actualizarCliente(id: number, cuerpo: ClienteUpdateRequest): Promise<Cliente> {
  const { data } = await gestionApi.put<Cliente>(`/clientes/${id}`, cuerpo)
  return data
}

/** Elimina un cliente (DELETE /clientes/{id}). */
export async function eliminarCliente(id: number): Promise<void> {
  await gestionApi.delete(`/clientes/${id}`)
}
