<script setup lang="ts">
defineProps<{
  titulo: string
}>()
</script>

<template>
  <div class="d-flex flex-wrap align-center justify-space-between ga-4 mb-6">
    <h1 class="titulo-pagina">{{ titulo }}</h1>
    <slot name="accion" />
  </div>
</template>

<style scoped>
.titulo-pagina {
  font-size: 1.75rem;
  font-weight: 600;
  line-height: 1.3;
}
</style>
