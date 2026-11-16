<script setup lang="ts">
defineProps<{
  /** Texto para lectores de pantalla, por ejemplo «Cargando clientes». */
  etiqueta: string
}>()
</script>

<template>
  <div class="d-flex justify-center pa-6">
    <v-progress-circular indeterminate color="primary" :aria-label="etiqueta" />
  </div>
</template>
