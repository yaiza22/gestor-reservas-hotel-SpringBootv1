<script setup lang="ts">
import { reactive, ref } from 'vue'
import { actualizarHabitacion, crearHabitacion } from '@/api/habitaciones.api'
import EstadoChip from '@/components/EstadoChip.vue'
import { useNotify } from '@/composables/useNotify'
import type { ApiError } from '@/types/common'
import type { EstadoHabitacion, Habitacion } from '@/types/habitacion'
import { ESTADOS_HABITACION, TIPOS_HABITACION } from '@/utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '@/utils/errores'
import { validarHabitacion } from '@/utils/validaciones'
import type { ErroresForm, HabitacionForm } from '@/utils/validaciones'

const props = defineProps<{
  /** Habitación a editar; si es `null` el formulario crea una nueva. */
  habitacion: Habitacion | null
}>()

const emit = defineEmits<{
  cerrar: []
  /** Se emite después de guardar bien, para que la pantalla recargue la lista. */
  guardado: []
}>()

const notify = useNotify()
const esEdicion = props.habitacion !== null
const opcionesTipo = TIPOS_HABITACION.map((tipo) => ({ title: tipo.etiqueta, value: tipo.valor }))
const opcionesEstado = ESTADOS_HABITACION.map((estado) => ({ title: estado, value: estado }))

const valores = reactive<HabitacionForm>({
  numero: props.habitacion?.numero ?? '',
  tipoHabitacion: props.habitacion?.tipoHabitacion ?? '',
  piso: props.habitacion ? String(props.habitacion.piso) : '',
  precioBase: props.habitacion ? String(props.habitacion.precioBase) : '',
  capacidad: props.habitacion ? String(props.habitacion.capacidad) : '',
  estado: props.habitacion?.estado ?? 'DISPONIBLE',
})
const errores = ref<ErroresForm>({})
const errorGeneral = ref<ApiError | null>(null)
const guardando = ref(false)

async function guardar() {
  const erroresLocales = validarHabitacion(valores, esEdicion)
  errores.value = erroresLocales
  errorGeneral.value = null
  if (Object.keys(erroresLocales).length > 0 || valores.tipoHabitacion === '') return

  guardando.value = true
  try {
    const datos = {
      tipoHabitacion: valores.tipoHabitacion,
      piso: Number(valores.piso),
      precioBase: Number(valores.precioBase),
      capacidad: Number(valores.capacidad),
      estado: valores.estado,
    }
    if (props.habitacion) {
      await actualizarHabitacion(props.habitacion.id, datos)
      notify.exito('Habitación actualizada')
    } else {
      await crearHabitacion({ ...datos, numero: valores.numero.trim() })
      notify.exito('Habitación creada')
    }
    emit('guardado')
  } catch (e: unknown) {
    const error = comoApiError(e)
    const porCampo = erroresPorCampo(error)
    if (Object.keys(porCampo).length > 0) {
      errores.value = porCampo
    } else {
      errorGeneral.value = error
    }
    guardando.value = false
  }
}
</script>

<template>
  <v-dialog :model-value="true" :persistent="guardando" max-width="600" @update:model-value="(abierto: boolean) => !abierto && emit('cerrar')">
    <v-card>
      <v-form @submit.prevent="guardar">
        <v-card-title class="px-6 pt-5">{{ esEdicion ? 'Editar habitación' : 'Nueva habitación' }}</v-card-title>
        <v-card-text class="px-6">
          <v-alert v-if="errorGeneral" type="error" variant="tonal" class="mb-4">
            {{ mensajeDeError(errorGeneral, 'habitaciones') }}
          </v-alert>
          <div class="campos">
            <v-text-field
              v-model="valores.numero"
              label="Número *"
              :error-messages="errores.numero"
              :disabled="esEdicion"
            />
            <v-select
              v-model="valores.tipoHabitacion"
              label="Tipo de habitación *"
              :items="opcionesTipo"
              :error-messages="errores.tipoHabitacion"
            />
            <v-text-field v-model="valores.piso" label="Piso *" inputmode="numeric" :error-messages="errores.piso" />
            <v-text-field
              v-model="valores.precioBase"
              label="Precio base por noche (COP) *"
              inputmode="decimal"
              :error-messages="errores.precioBase"
            />
            <v-text-field
              v-model="valores.capacidad"
              label="Capacidad (personas) *"
              inputmode="numeric"
              :error-messages="errores.capacidad"
            />
            <v-select
              v-model="valores.estado"
              label="Estado"
              :items="opcionesEstado"
              :error-messages="errores.estado"
            >
              <template #item="{ props: itemProps, item }">
                <v-list-item v-bind="itemProps" :title="undefined">
                  <EstadoChip :valor="item.value as EstadoHabitacion" />
                </v-list-item>
              </template>
              <template #selection="{ item }">
                <EstadoChip :valor="item.value as EstadoHabitacion" />
              </template>
            </v-select>
          </div>
        </v-card-text>
        <v-card-actions class="px-6 pb-4">
          <v-spacer />
          <v-btn color="primary" :disabled="guardando" @click="emit('cerrar')">Cancelar</v-btn>
          <v-btn type="submit" color="primary" variant="flat" :loading="guardando" :disabled="guardando">Guardar</v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.campos {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 16px;
  row-gap: 12px;
  padding-top: 8px;
}

@media (max-width: 599px) {
  .campos {
    grid-template-columns: 1fr;
  }
}
</style>
