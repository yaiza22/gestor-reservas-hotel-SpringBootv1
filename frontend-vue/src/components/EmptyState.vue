<script setup lang="ts">
defineProps<{
  mensaje: string
}>()
</script>

<template>
  <div class="text-center py-12 px-4">
    <p class="text-medium-emphasis" :class="{ 'mb-4': $slots.accion }">{{ mensaje }}</p>
    <slot name="accion" />
  </div>
</template>
