<script setup lang="ts">
import { computed, ref } from 'vue'
import { eliminarUsuario, listarUsuarios } from '@/api/usuarios.api'
import AlertaError from '@/components/AlertaError.vue'
import CargandoTabla from '@/components/CargandoTabla.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'
import EstadoChip from '@/components/EstadoChip.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useLista } from '@/composables/useLista'
import { useNotify } from '@/composables/useNotify'
import type { ApiError } from '@/types/common'
import type { Usuario } from '@/types/usuario'
import { comoApiError } from '@/utils/errores'
import UsuarioFormDialog from './UsuarioFormDialog.vue'

/** Pantalla de usuarios: tabla, crear, editar y eliminar. */
const notify = useNotify()
const { datos: usuarios, cargando, error, recargar } = useLista(listarUsuarios)
const formAbierto = ref(false)
const editando = ref<Usuario | null>(null)
const porEliminar = ref<Usuario | null>(null)
const eliminando = ref(false)
const errorAccion = ref<ApiError | null>(null)

const errorVisible = computed(() => errorAccion.value ?? error.value)

function abrirNuevo() {
  editando.value = null
  formAbierto.value = true
}

function abrirEdicion(usuario: Usuario) {
  editando.value = usuario
  formAbierto.value = true
}

function alGuardar() {
  formAbierto.value = false
  errorAccion.value = null
  recargar()
}

async function confirmarEliminar() {
  if (!porEliminar.value) return
  eliminando.value = true
  try {
    await eliminarUsuario(porEliminar.value.id)
    notify.exito('Usuario eliminado')
    errorAccion.value = null
    recargar()
  } catch (e: unknown) {
    errorAccion.value = comoApiError(e)
  } finally {
    eliminando.value = false
    porEliminar.value = null
  }
}
</script>

<template>
  <PageHeader titulo="Usuarios">
    <template #accion>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNuevo">Nuevo usuario</v-btn>
    </template>
  </PageHeader>

  <AlertaError v-if="errorVisible" :error="errorVisible" servicio="gestión" />

  <v-card>
    <v-table class="tabla" hover aria-label="Lista de usuarios">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Documento</th>
          <th>Correo</th>
          <th>Rol</th>
          <th class="text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="usuario in usuarios" :key="usuario.id">
          <td>{{ usuario.id }}</td>
          <td>{{ usuario.nombre }}</td>
          <td>{{ usuario.tipoDocIdentidad }} {{ usuario.numDocIdentidad }}</td>
          <td>{{ usuario.correo }}</td>
          <td><EstadoChip :valor="usuario.rol" /></td>
          <td class="text-right text-no-wrap">
            <v-btn
              icon="mdi-pencil"
              variant="text"
              class="text-medium-emphasis"
              size="small"
              :aria-label="`Editar usuario ${usuario.nombre}`"
              @click="abrirEdicion(usuario)"
            >
              <v-icon icon="mdi-pencil" />
              <v-tooltip activator="parent" location="top">Editar</v-tooltip>
            </v-btn>
            <v-btn
              icon="mdi-delete"
              variant="text"
              size="small"
              color="error"
              :aria-label="`Eliminar usuario ${usuario.nombre}`"
              @click="porEliminar = usuario"
            >
              <v-icon icon="mdi-delete" />
              <v-tooltip activator="parent" location="top">Eliminar</v-tooltip>
            </v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>
    <CargandoTabla v-if="cargando" etiqueta="Cargando usuarios" />
    <EmptyState v-if="!cargando && !error && usuarios.length === 0" mensaje="Todavía no hay usuarios registrados.">
      <template #accion>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNuevo">Nuevo usuario</v-btn>
      </template>
    </EmptyState>
  </v-card>

  <UsuarioFormDialog v-if="formAbierto" :usuario="editando" @cerrar="formAbierto = false" @guardado="alGuardar" />
  <ConfirmDialog
    :abierto="porEliminar !== null"
    titulo="Eliminar usuario"
    :mensaje="`¿Seguro que quieres eliminar a ${porEliminar?.nombre ?? ''}? Esta acción no se puede deshacer.`"
    :procesando="eliminando"
    @confirmar="confirmarEliminar"
    @cancelar="porEliminar = null"
  />
</template>

<style scoped>
.tabla :deep(thead th) {
  background-color: #eceff1;
  font-weight: 600;
}
</style>
