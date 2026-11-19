<script setup lang="ts">
import { NOMBRE_APP_HABITACIONES, SECCIONES_HABITACIONES } from '@/utils/constantes'
</script>

<template>
  <v-app-bar color="primary" flat>
    <div class="titulo-app">{{ NOMBRE_APP_HABITACIONES }}</div>
    <nav aria-label="Secciones" class="d-flex ga-2">
      <v-btn
        v-for="seccion in SECCIONES_HABITACIONES"
        :key="seccion.ruta"
        :to="seccion.ruta"
        variant="text"
        active-class="seccion-activa"
      >
        {{ seccion.etiqueta }}
      </v-btn>
    </nav>
  </v-app-bar>
  <v-main>
    <v-container class="px-6 py-8" style="max-width: 1200px">
      <router-view />
    </v-container>
  </v-main>
</template>

<style scoped>
.titulo-app {
  font-size: 1.25rem;
  font-weight: 500;
  margin: 0 24px 0 16px;
  white-space: nowrap;
}

.seccion-activa {
  background-color: rgba(255, 255, 255, 0.2);
  font-weight: 700;
}
</style>
