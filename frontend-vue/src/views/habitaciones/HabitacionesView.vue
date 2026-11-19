<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { cambiarEstadoHabitacion, eliminarHabitacion, listarHabitaciones } from '@/api/habitaciones.api'
import AlertaError from '@/components/AlertaError.vue'
import CargandoTabla from '@/components/CargandoTabla.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'
import EstadoChip from '@/components/EstadoChip.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useLista } from '@/composables/useLista'
import { useNotify } from '@/composables/useNotify'
import type { ApiError } from '@/types/common'
import type { EstadoHabitacion, Habitacion, TipoHabitacion } from '@/types/habitacion'
import { ESTADOS_HABITACION, TIPOS_HABITACION } from '@/utils/constantes'
import { comoApiError } from '@/utils/errores'
import { etiquetaTipoHabitacion, formatearMoneda } from '@/utils/formato'
import HabitacionFormDialog from './HabitacionFormDialog.vue'

/** Pantalla de habitaciones: tabla, filtros por estado y tipo, cambio rápido de estado, crear, editar y eliminar. */
const notify = useNotify()
const filtroEstado = ref<EstadoHabitacion | ''>('')
const filtroTipo = ref<TipoHabitacion | ''>('')
// Los filtros los aplica el servidor; '' significa «sin filtro» y ese parámetro no se envía
const { datos: habitaciones, cargando, error, recargar } = useLista(() =>
  listarHabitaciones({ estado: filtroEstado.value || undefined, tipoHabitacion: filtroTipo.value || undefined }),
)
watch([filtroEstado, filtroTipo], () => recargar())
const hayFiltros = computed(() => filtroEstado.value !== '' || filtroTipo.value !== '')
const formAbierto = ref(false)
const editando = ref<Habitacion | null>(null)
const porEliminar = ref<Habitacion | null>(null)
const eliminando = ref(false)
const errorAccion = ref<ApiError | null>(null)
// Menú del cambio rápido de estado: qué habitación y dónde se abre
const menuEstado = ref<{ habitacion: Habitacion; ancla: HTMLElement } | null>(null)

const opcionesFiltro: { title: string; value: EstadoHabitacion | '' }[] = [
  { title: 'Todos', value: '' },
  ...ESTADOS_HABITACION.map((estado) => ({ title: estado, value: estado })),
]

const opcionesTipo: { title: string; value: TipoHabitacion | '' }[] = [
  { title: 'Todos', value: '' },
  ...TIPOS_HABITACION.map((tipo) => ({ title: tipo.etiqueta, value: tipo.valor })),
]
const errorVisible = computed(() => errorAccion.value ?? error.value)

function abrirNueva() {
  editando.value = null
  formAbierto.value = true
}

function abrirEdicion(habitacion: Habitacion) {
  editando.value = habitacion
  formAbierto.value = true
}

function alGuardar() {
  formAbierto.value = false
  errorAccion.value = null
  recargar()
}

function abrirMenuEstado(habitacion: Habitacion, evento: MouseEvent) {
  menuEstado.value = { habitacion, ancla: evento.currentTarget as HTMLElement }
}

async function elegirEstado(estado: EstadoHabitacion) {
  const habitacion = menuEstado.value?.habitacion
  menuEstado.value = null
  if (!habitacion || habitacion.estado === estado) return
  try {
    await cambiarEstadoHabitacion(habitacion.id, estado)
    notify.exito(`Habitación ${habitacion.numero} actualizada`)
    errorAccion.value = null
    recargar()
  } catch (e: unknown) {
    errorAccion.value = comoApiError(e)
  }
}

async function confirmarEliminar() {
  if (!porEliminar.value) return
  eliminando.value = true
  try {
    await eliminarHabitacion(porEliminar.value.id)
    notify.exito('Habitación eliminada')
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
  <PageHeader titulo="Habitaciones">
    <template #accion>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNueva">Nueva habitación</v-btn>
    </template>
  </PageHeader>

  <div class="mb-4 d-flex flex-wrap ga-4">
    <v-select
      v-model="filtroEstado"
      label="Filtrar por estado"
      :items="opcionesFiltro"
      density="compact"
      hide-details
      style="max-width: 224px"
    >
      <template #item="{ props: itemProps, item }">
        <v-list-item v-bind="itemProps" :title="undefined">
          <EstadoChip v-if="item.value !== ''" :valor="item.value as EstadoHabitacion" />
          <span v-else>Todos</span>
        </v-list-item>
      </template>
      <template #selection="{ item }">
        <EstadoChip v-if="item.value !== ''" :valor="item.value as EstadoHabitacion" />
        <span v-else>Todos</span>
      </template>
    </v-select>
    <v-select
      v-model="filtroTipo"
      label="Filtrar por tipo"
      :items="opcionesTipo"
      density="compact"
      hide-details
      style="max-width: 224px"
    />
  </div>

  <AlertaError v-if="errorVisible" :error="errorVisible" servicio="habitaciones" />

  <v-card>
    <v-table class="tabla" hover aria-label="Lista de habitaciones">
      <thead>
        <tr>
          <th>ID</th>
          <th>Número</th>
          <th>Tipo</th>
          <th class="text-right">Piso</th>
          <th class="text-right">Precio base</th>
          <th class="text-right">Capacidad</th>
          <th>Estado</th>
          <th class="text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="habitacion in habitaciones" :key="habitacion.id">
          <td>{{ habitacion.id }}</td>
          <td>{{ habitacion.numero }}</td>
          <td>{{ etiquetaTipoHabitacion(habitacion.tipoHabitacion) }}</td>
          <td class="text-right">{{ habitacion.piso }}</td>
          <td class="text-right">{{ formatearMoneda(habitacion.precioBase) }}</td>
          <td class="text-right">{{ habitacion.capacidad }}</td>
          <td>
            <EstadoChip
              :valor="habitacion.estado"
              :aria-label="`Cambiar estado de la habitación ${habitacion.numero}`"
              @click="(evento: MouseEvent) => abrirMenuEstado(habitacion, evento)"
            />
          </td>
          <td class="text-right text-no-wrap">
            <v-btn
              icon="mdi-pencil"
              variant="text"
              class="text-medium-emphasis"
              size="small"
              :aria-label="`Editar habitación ${habitacion.numero}`"
              @click="abrirEdicion(habitacion)"
            >
              <v-icon icon="mdi-pencil" />
              <v-tooltip activator="parent" location="top">Editar</v-tooltip>
            </v-btn>
            <v-btn
              icon="mdi-delete"
              variant="text"
              size="small"
              color="error"
              :aria-label="`Eliminar habitación ${habitacion.numero}`"
              @click="porEliminar = habitacion"
            >
              <v-icon icon="mdi-delete" />
              <v-tooltip activator="parent" location="top">Eliminar</v-tooltip>
            </v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>
    <CargandoTabla v-if="cargando" etiqueta="Cargando habitaciones" />
    <EmptyState
      v-if="!cargando && !error && habitaciones.length === 0 && !hayFiltros"
      mensaje="Todavía no hay habitaciones registradas."
    >
      <template #accion>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNueva">Nueva habitación</v-btn>
      </template>
    </EmptyState>
    <EmptyState
      v-if="!cargando && !error && habitaciones.length === 0 && hayFiltros"
      mensaje="No hay habitaciones con esos filtros."
    />
  </v-card>

  <v-menu
    :model-value="menuEstado !== null"
    :target="menuEstado?.ancla"
    @update:model-value="(abierto: boolean) => !abierto && (menuEstado = null)"
  >
    <v-list>
      <v-list-item
        v-for="estado in ESTADOS_HABITACION"
        :key="estado"
        :active="estado === menuEstado?.habitacion.estado"
        @click="elegirEstado(estado)"
      >
        <EstadoChip :valor="estado" />
      </v-list-item>
    </v-list>
  </v-menu>

  <HabitacionFormDialog
    v-if="formAbierto"
    :habitacion="editando"
    @cerrar="formAbierto = false"
    @guardado="alGuardar"
  />
  <ConfirmDialog
    :abierto="porEliminar !== null"
    titulo="Eliminar habitación"
    :mensaje="`¿Seguro que quieres eliminar la habitación ${porEliminar?.numero ?? ''}? Esta acción no se puede deshacer.`"
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
