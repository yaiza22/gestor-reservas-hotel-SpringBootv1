<script setup lang="ts">
withDefaults(
  defineProps<{
    abierto: boolean
    titulo: string
    mensaje: string
    textoConfirmar?: string
    /** Mientras es true, los botones se deshabilitan para no enviar dos veces. */
    procesando?: boolean
  }>(),
  { textoConfirmar: 'Eliminar', procesando: false },
)

const emit = defineEmits<{
  confirmar: []
  cancelar: []
}>()
</script>

<template>
  <v-dialog
    :model-value="abierto"
    :persistent="procesando"
    max-width="444"
    @update:model-value="(valor: boolean) => !valor && emit('cancelar')"
  >
    <v-card>
      <v-card-title class="px-6 pt-5">{{ titulo }}</v-card-title>
      <v-card-text class="px-6">{{ mensaje }}</v-card-text>
      <v-card-actions class="px-6 pb-4">
        <v-spacer />
        <v-btn :disabled="procesando" @click="emit('cancelar')">Cancelar</v-btn>
        <v-btn color="error" :disabled="procesando" @click="emit('confirmar')">
          {{ textoConfirmar }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
