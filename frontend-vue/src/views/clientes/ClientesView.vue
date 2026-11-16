<script setup lang="ts">
import { computed, ref } from 'vue'
import { eliminarCliente, listarClientes } from '@/api/clientes.api'
import AlertaError from '@/components/AlertaError.vue'
import CargandoTabla from '@/components/CargandoTabla.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useLista } from '@/composables/useLista'
import { useNotify } from '@/composables/useNotify'
import type { Cliente } from '@/types/cliente'
import type { ApiError } from '@/types/common'
import { comoApiError } from '@/utils/errores'
import ClienteFormDialog from './ClienteFormDialog.vue'

/** Pantalla de clientes: tabla, crear, editar y eliminar. Es la plantilla de las demás pantallas. */
const notify = useNotify()
const { datos: clientes, cargando, error, recargar } = useLista(listarClientes)
const formAbierto = ref(false)
const editando = ref<Cliente | null>(null)
const porEliminar = ref<Cliente | null>(null)
const eliminando = ref(false)
const errorAccion = ref<ApiError | null>(null)

const errorVisible = computed(() => errorAccion.value ?? error.value)

function abrirNuevo() {
  editando.value = null
  formAbierto.value = true
}

function abrirEdicion(cliente: Cliente) {
  editando.value = cliente
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
    await eliminarCliente(porEliminar.value.id)
    notify.exito('Cliente eliminado')
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
  <PageHeader titulo="Clientes">
    <template #accion>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNuevo">Nuevo cliente</v-btn>
    </template>
  </PageHeader>

  <AlertaError v-if="errorVisible" :error="errorVisible" servicio="gestión" />

  <v-card>
    <v-table class="tabla" hover aria-label="Lista de clientes">
      <thead>
        <tr>
          <th>ID</th>
          <th>Documento</th>
          <th>Nombre</th>
          <th>Teléfono</th>
          <th>Correo</th>
          <th>Nacionalidad</th>
          <th>País de residencia</th>
          <th class="text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cliente in clientes" :key="cliente.id">
          <td>{{ cliente.id }}</td>
          <td>{{ cliente.tipoDocIdentidad }} {{ cliente.numDocIdentidad }}</td>
          <td>{{ cliente.nombre }}</td>
          <td>{{ cliente.telefono }}</td>
          <td>{{ cliente.correo }}</td>
          <td>{{ cliente.nacionalidad }}</td>
          <td>{{ cliente.paisResidencia }}</td>
          <td class="text-right text-no-wrap">
            <v-btn
              icon="mdi-pencil"
              variant="text"
              size="small"
              :aria-label="`Editar cliente ${cliente.nombre}`"
              @click="abrirEdicion(cliente)"
            >
              <v-icon icon="mdi-pencil" />
              <v-tooltip activator="parent" location="top">Editar</v-tooltip>
            </v-btn>
            <v-btn
              icon="mdi-delete"
              variant="text"
              size="small"
              color="error"
              :aria-label="`Eliminar cliente ${cliente.nombre}`"
              @click="porEliminar = cliente"
            >
              <v-icon icon="mdi-delete" />
              <v-tooltip activator="parent" location="top">Eliminar</v-tooltip>
            </v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>
    <CargandoTabla v-if="cargando" etiqueta="Cargando clientes" />
    <EmptyState v-if="!cargando && !error && clientes.length === 0" mensaje="Todavía no hay clientes registrados.">
      <template #accion>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNuevo">Nuevo cliente</v-btn>
      </template>
    </EmptyState>
  </v-card>

  <ClienteFormDialog v-if="formAbierto" :cliente="editando" @cerrar="formAbierto = false" @guardado="alGuardar" />
  <ConfirmDialog
    :abierto="porEliminar !== null"
    titulo="Eliminar cliente"
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
