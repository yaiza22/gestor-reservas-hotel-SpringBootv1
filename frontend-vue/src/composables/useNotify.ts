import { ref } from 'vue'

type Gravedad = 'success' | 'error'

/** Estado único del aviso: lo comparten `useNotify` (que lo escribe) y `App.vue` (que lo muestra). */
const abierto = ref(false)
const texto = ref('')
const gravedad = ref<Gravedad>('success')

function mostrar(mensaje: string, tipo: Gravedad) {
  texto.value = mensaje
  gravedad.value = tipo
  abierto.value = true
}

export const estadoNotify = { abierto, texto, gravedad }

/** Da acceso a `exito(texto)` y `error(texto)` desde cualquier componente. */
export function useNotify() {
  return {
    exito: (mensaje: string) => mostrar(mensaje, 'success'),
    error: (mensaje: string) => mostrar(mensaje, 'error'),
  }
}
