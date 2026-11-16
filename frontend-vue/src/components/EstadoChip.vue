<script setup lang="ts">
import { computed, useAttrs } from 'vue'
import type { EstadoHabitacion } from '@/types/habitacion'
import type { EstadoReserva } from '@/types/reserva'
import type { Rol } from '@/types/usuario'

type ValorEstado = EstadoReserva | EstadoHabitacion | Rol

interface Apariencia {
  texto: string
  color?: string
}

/** Mapa fijo: cada valor siempre se ve igual en toda la aplicación. */
const APARIENCIA: Record<ValorEstado, Apariencia> = {
  // Reservas
  PENDIENTE: { texto: 'Pendiente', color: 'warning' },
  CONFIRMADA: { texto: 'Confirmada', color: 'info' },
  ENCURSO: { texto: 'En curso', color: 'primary' },
  FINALIZADA: { texto: 'Finalizada', color: 'success' },
  CANCELADA: { texto: 'Cancelada', color: 'error' },
  // Habitaciones
  DISPONIBLE: { texto: 'Disponible', color: 'success' },
  OCUPADA: { texto: 'Ocupada', color: 'error' },
  MANTENIMIENTO: { texto: 'Mantenimiento', color: 'warning' },
  LIMPIEZA: { texto: 'Limpieza', color: 'info' },
  // Roles
  ADMINISTRADOR: { texto: 'Administrador', color: 'primary' },
  ASISTENTE: { texto: 'Asistente' },
}

const props = defineProps<{
  valor: ValorEstado
  /** Texto para lectores de pantalla; solo se usa cuando la etiqueta es un botón (tiene `@click`). */
  ariaLabel?: string
}>()

const attrs = useAttrs()
const esBoton = computed(() => typeof attrs.onClick === 'function')
const apariencia = computed(() => APARIENCIA[props.valor])
</script>

<template>
  <v-chip
    size="small"
    :color="apariencia.color"
    :variant="apariencia.color ? 'flat' : 'tonal'"
    :aria-label="esBoton ? ariaLabel : undefined"
  >
    {{ apariencia.texto }}
  </v-chip>
</template>
