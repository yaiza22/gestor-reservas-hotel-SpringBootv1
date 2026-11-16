import type { Usuario, UsuarioRequest, UsuarioUpdateRequest } from '../types/usuario'
import { gestionApi } from './httpClient'

/** Trae todos los usuarios (GET /usuarios). */
export async function listarUsuarios(): Promise<Usuario[]> {
  const { data } = await gestionApi.get<Usuario[]>('/usuarios')
  return data
}

/** Trae un usuario por su id (GET /usuarios/{id}). */
export async function obtenerUsuario(id: number): Promise<Usuario> {
  const { data } = await gestionApi.get<Usuario>(`/usuarios/${id}`)
  return data
}

/** Crea un usuario (POST /usuarios). */
export async function crearUsuario(cuerpo: UsuarioRequest): Promise<Usuario> {
  const { data } = await gestionApi.post<Usuario>('/usuarios', cuerpo)
  return data
}

/** Actualiza los datos editables de un usuario (PUT /usuarios/{id}). */
export async function actualizarUsuario(id: number, cuerpo: UsuarioUpdateRequest): Promise<Usuario> {
  const { data } = await gestionApi.put<Usuario>(`/usuarios/${id}`, cuerpo)
  return data
}

/** Elimina un usuario (DELETE /usuarios/{id}). */
export async function eliminarUsuario(id: number): Promise<void> {
  await gestionApi.delete(`/usuarios/${id}`)
}
