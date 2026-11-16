import { onMounted, ref } from 'vue'
import type { Ref } from 'vue'
import type { ApiError } from '@/types/common'
import { comoApiError } from '@/utils/errores'

/**
 * Pide una lista al servidor y lleva la cuenta de `cargando` y `error`.
 * `recargar` se vuelve a llamar después de crear, editar o eliminar.
 * @param traer función de `api/` que devuelve la lista
 */
export function useLista<T>(traer: () => Promise<T[]>) {
  const datos = ref([]) as Ref<T[]>
  const cargando = ref(true)
  const error = ref<ApiError | null>(null)

  async function recargar() {
    cargando.value = true
    try {
      datos.value = await traer()
      error.value = null
    } catch (e: unknown) {
      error.value = comoApiError(e)
    } finally {
      cargando.value = false
    }
  }

  onMounted(recargar)

  return { datos, cargando, error, recargar }
}
