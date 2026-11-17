<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { actualizarReserva, crearReserva } from '@/api/reservas.api'
import EstadoChip from '@/components/EstadoChip.vue'
import { useNotify } from '@/composables/useNotify'
import type { Cliente } from '@/types/cliente'
import type { ApiError } from '@/types/common'
import type { Habitacion } from '@/types/habitacion'
import type { Reserva } from '@/types/reserva'
import { TEMPORADAS } from '@/utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '@/utils/errores'
import { etiquetaTipoHabitacion } from '@/utils/formato'
import { validarReserva } from '@/utils/validaciones'
import type { ErroresForm, ReservaForm } from '@/utils/validaciones'

const props = defineProps<{
  /** Reserva a editar; si es `null` el formulario crea una nueva. */
  reserva: Reserva | null
  /** Clientes y habitaciones que se pueden elegir (los pide la pantalla). */
  clientes: Cliente[]
  habitaciones: Habitacion[]
}>()

const emit = defineEmits<{
  cerrar: []
  /** Se emite después de guardar bien, para que la pantalla recargue la lista. */
  guardado: []
}>()

const notify = useNotify()
const esEdicion = props.reserva !== null
const opcionesTemporada = TEMPORADAS.map((temporada) => ({ title: temporada.etiqueta, value: temporada.valor }))
const opcionesCliente = computed(() =>
  props.clientes.map((cliente) => ({
    title: `${cliente.nombre} · ${cliente.tipoDocIdentidad} ${cliente.numDocIdentidad}`,
    value: cliente.id,
  })),
)
const opcionesHabitacion = computed(() =>
  props.habitaciones.map((habitacion) => ({
    title: `${habitacion.numero} · ${etiquetaTipoHabitacion(habitacion.tipoHabitacion)}`,
    value: habitacion.id,
    habitacion,
  })),
)

const valores = reactive<ReservaForm>({
  clienteId: props.reserva?.clienteId ?? '',
  habitacionesId: props.reserva ? [...props.reserva.habitacionesId] : [],
  fechaInicio: props.reserva?.fechaInicio ?? '',
  fechaFin: props.reserva?.fechaFin ?? '',
  cantHuespedes: props.reserva ? String(props.reserva.cantHuespedes) : '',
  temporada: props.reserva?.temporada ?? '',
})
const errores = ref<ErroresForm>({})
const errorGeneral = ref<ApiError | null>(null)
const guardando = ref(false)

const numerosElegidos = computed(() =>
  valores.habitacionesId.map((id) => props.habitaciones.find((h) => h.id === id)?.numero ?? String(id)).join(', '),
)

async function guardar() {
  const erroresLocales = validarReserva(valores)
  errores.value = erroresLocales
  errorGeneral.value = null
  if (Object.keys(erroresLocales).length > 0 || valores.clienteId === '' || valores.temporada === '') return

  guardando.value = true
  try {
    const datos = {
      clienteId: valores.clienteId,
      habitacionesId: [...valores.habitacionesId],
      fechaInicio: valores.fechaInicio,
      fechaFin: valores.fechaFin,
      cantHuespedes: Number(valores.cantHuespedes),
      temporada: valores.temporada,
    }
    if (props.reserva) {
      await actualizarReserva(props.reserva.id, datos)
      notify.exito('Reserva actualizada')
    } else {
      await crearReserva(datos)
      notify.exito('Reserva creada')
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
        <v-card-title class="px-6 pt-5">{{ esEdicion ? 'Editar reserva' : 'Nueva reserva' }}</v-card-title>
        <v-card-text class="px-6">
          <v-alert v-if="errorGeneral" type="error" variant="tonal" class="mb-4">
            {{ mensajeDeError(errorGeneral, 'gestión') }}
          </v-alert>
          <div class="campos">
            <v-select
              v-model="valores.clienteId"
              class="ancho-completo"
              label="Cliente *"
              :items="opcionesCliente"
              :error-messages="errores.clienteId"
            />
            <v-select
              v-model="valores.habitacionesId"
              class="ancho-completo"
              label="Habitaciones *"
              :items="opcionesHabitacion"
              multiple
              :error-messages="errores.habitacionesId"
            >
              <template #item="{ props: itemProps, item }">
                <v-list-item v-bind="itemProps" :title="undefined">
                  <template #prepend="{ isSelected }">
                    <v-checkbox-btn :model-value="isSelected" tabindex="-1" />
                  </template>
                  <v-list-item-title>{{ item.title }}</v-list-item-title>
                  <v-list-item-subtitle class="d-flex align-center ga-2">
                    Capacidad {{ item.habitacion.capacidad }}
                    <EstadoChip :valor="item.habitacion.estado" />
                  </v-list-item-subtitle>
                </v-list-item>
              </template>
              <template #selection="{ index }">
                <span v-if="index === 0">{{ numerosElegidos }}</span>
              </template>
            </v-select>
            <v-text-field
              v-model="valores.fechaInicio"
              type="date"
              label="Fecha de inicio *"
              persistent-placeholder
              :error-messages="errores.fechaInicio"
            />
            <v-text-field
              v-model="valores.fechaFin"
              type="date"
              label="Fecha de fin *"
              persistent-placeholder
              :error-messages="errores.fechaFin"
            />
            <v-text-field
              v-model="valores.cantHuespedes"
              label="Cantidad de huéspedes *"
              inputmode="numeric"
              :error-messages="errores.cantHuespedes"
            />
            <v-select
              v-model="valores.temporada"
              label="Temporada *"
              :items="opcionesTemporada"
              :error-messages="errores.temporada"
            />
          </div>
        </v-card-text>
        <v-card-actions class="px-6 pb-4">
          <v-spacer />
          <v-btn :disabled="guardando" @click="emit('cerrar')">Cancelar</v-btn>
          <v-btn type="submit" color="primary" :loading="guardando" :disabled="guardando">Guardar</v-btn>
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

.ancho-completo {
  grid-column: 1 / -1;
}

@media (max-width: 599px) {
  .campos {
    grid-template-columns: 1fr;
  }
}
</style>
