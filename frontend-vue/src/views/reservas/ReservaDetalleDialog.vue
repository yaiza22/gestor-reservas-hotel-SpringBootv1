<script setup lang="ts">
import { computed } from 'vue'
import EstadoChip from '@/components/EstadoChip.vue'
import type { Reserva } from '@/types/reserva'
import { formatearFecha, formatearMoneda } from '@/utils/formato'

const props = defineProps<{
  reserva: Reserva
  /** Nombre del cliente y números de habitación, ya resueltos por la pantalla. */
  cliente: string
  habitaciones: string
}>()

const emit = defineEmits<{
  cerrar: []
}>()

/** Datos que se muestran: etiqueta pequeña arriba y el valor debajo. */
const datos = computed(() => [
  { etiqueta: 'Cliente', texto: props.cliente },
  { etiqueta: 'Habitaciones', texto: props.habitaciones },
  { etiqueta: 'Fecha de inicio', texto: formatearFecha(props.reserva.fechaInicio) },
  { etiqueta: 'Fecha de fin', texto: formatearFecha(props.reserva.fechaFin) },
  { etiqueta: 'Noches', texto: String(props.reserva.noches) },
  { etiqueta: 'Huéspedes', texto: String(props.reserva.cantHuespedes) },
  { etiqueta: 'Temporada', texto: props.reserva.temporada === 'ALTA' ? 'Alta' : 'Baja' },
  { etiqueta: 'Estado', texto: '', estado: true },
  { etiqueta: 'Precio total', texto: formatearMoneda(props.reserva.precioTotal) },
  { etiqueta: 'Hora de entrada', texto: props.reserva.horaEntrada ?? 'Sin registrar' },
  { etiqueta: 'Hora de salida', texto: props.reserva.horaSalida ?? 'Sin registrar' },
])
</script>

<template>
  <v-dialog :model-value="true" max-width="600" @update:model-value="(abierto: boolean) => !abierto && emit('cerrar')">
    <v-card>
      <v-card-title class="px-6 pt-5">Reserva {{ reserva.id }}</v-card-title>
      <v-card-text class="px-6">
        <div class="campos">
          <div v-for="dato in datos" :key="dato.etiqueta">
            <div class="etiqueta">{{ dato.etiqueta }}</div>
            <div>
              <EstadoChip v-if="dato.estado" :valor="reserva.estado" />
              <template v-else>{{ dato.texto }}</template>
            </div>
          </div>
        </div>
      </v-card-text>
      <v-card-actions class="px-6 pb-4">
        <v-spacer />
        <v-btn color="primary" @click="emit('cerrar')">Cerrar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.campos {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding-top: 8px;
}

.etiqueta {
  font-size: 0.75rem;
  color: rgba(0, 0, 0, 0.6);
}

@media (max-width: 599px) {
  .campos {
    grid-template-columns: 1fr;
  }
}
</style>
