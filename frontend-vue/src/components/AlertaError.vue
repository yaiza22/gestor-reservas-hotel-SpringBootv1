<script setup lang="ts">
import type { ApiError } from '@/types/common'
import { mensajeDeError } from '@/utils/errores'

defineProps<{
  error: ApiError
  /** Servicio que se estaba llamando («gestión», «habitaciones»), para el mensaje de «sin conexión». */
  servicio: string
}>()
</script>

<template>
  <v-alert type="error" variant="tonal" class="mb-4">
    {{ mensajeDeError(error, servicio) }}
  </v-alert>
</template>
