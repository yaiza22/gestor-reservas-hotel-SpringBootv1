<script setup lang="ts">
import { estadoNotify } from '@/composables/useNotify'

const { abierto, texto, gravedad } = estadoNotify
</script>

<template>
  <v-app>
    <router-view />
    <v-snackbar v-model="abierto" :timeout="4000" location="bottom" :color="gravedad">
      {{ texto }}
      <template #actions>
        <v-btn icon="mdi-close" variant="text" aria-label="Cerrar aviso" @click="abierto = false" />
      </template>
    </v-snackbar>
  </v-app>
</template>
