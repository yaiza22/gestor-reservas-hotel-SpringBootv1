<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  error?: string
}>()

const valor = defineModel<string>({ required: true })
const visible = ref(false)
</script>

<template>
  <v-text-field
    v-model="valor"
    label="Contraseña *"
    :type="visible ? 'text' : 'password'"
    :error-messages="error ? [error] : []"
    hint="Entre 8 y 60 caracteres."
    persistent-hint
    autocomplete="new-password"
  >
    <template #append-inner>
      <v-btn
        :icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
        variant="text"
        size="small"
        :aria-label="visible ? 'Ocultar contraseña' : 'Mostrar contraseña'"
        @click="visible = !visible"
      />
    </template>
  </v-text-field>
</template>
