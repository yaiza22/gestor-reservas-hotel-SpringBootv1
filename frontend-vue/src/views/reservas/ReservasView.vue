<script setup lang="ts">
import { computed, ref } from 'vue'
import { listarClientes } from '@/api/clientes.api'
import { listarHabitaciones } from '@/api/habitaciones.api'
import {
  cancelarReserva,
  confirmarReserva,
  eliminarReserva,
  listarReservas,
  registrarCheckIn,
  registrarCheckOut,
} from '@/api/reservas.api'
import AlertaError from '@/components/AlertaError.vue'
import CargandoTabla from '@/components/CargandoTabla.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'
import EstadoChip from '@/components/EstadoChip.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useLista } from '@/composables/useLista'
import { useNotify } from '@/composables/useNotify'
import type { ApiError } from '@/types/common'
import type { EstadoReserva, Reserva } from '@/types/reserva'
import { ESTADOS_RESERVA } from '@/utils/constantes'
import { comoApiError, mensajeDeError } from '@/utils/errores'
import { formatearFecha, formatearMoneda } from '@/utils/formato'
import ReservaDetalleDialog from './ReservaDetalleDialog.vue'
import ReservaFormDialog from './ReservaFormDialog.vue'

/** Botón de transición de estado: qué hace, desde qué estados se puede y cómo avisar que salió bien. */
interface Transicion {
  texto: string
  desde: EstadoReserva[]
  ejecutar: (id: number) => Promise<Reserva>
  exito: string
}

const TRANSICIONES: Transicion[] = [
  { texto: 'Confirmar', desde: ['PENDIENTE'], ejecutar: confirmarReserva, exito: 'Reserva confirmada' },
  { texto: 'Check-in', desde: ['CONFIRMADA'], ejecutar: registrarCheckIn, exito: 'Check-in registrado' },
  { texto: 'Check-out', desde: ['ENCURSO'], ejecutar: registrarCheckOut, exito: 'Check-out registrado' },
]

/** Pantalla de reservas: tabla, filtro por estado, crear, editar, cambiar de estado y eliminar. */
const notify = useNotify()
const { datos: reservas, cargando, error, recargar } = useLista(listarReservas)
const { datos: clientes, cargando: cargandoClientes, error: errorClientes } = useLista(listarClientes)
const { datos: habitaciones, cargando: cargandoHabitaciones, error: errorHabitaciones } = useLista(listarHabitaciones)
const filtroEstado = ref<EstadoReserva | ''>('')
const formAbierto = ref(false)
const editando = ref<Reserva | null>(null)
const verDetalle = ref<Reserva | null>(null)
const porCancelar = ref<Reserva | null>(null)
const porEliminar = ref<Reserva | null>(null)
// Reserva con una acción en marcha: se deshabilitan sus botones para no enviarla dos veces
const enProceso = ref<number | null>(null)
const errorAccion = ref<ApiError | null>(null)

const opcionesFiltro: { title: string; value: EstadoReserva | '' }[] = [
  { title: 'Todos', value: '' },
  ...ESTADOS_RESERVA.map((estado) => ({ title: estado, value: estado })),
]

const visibles = computed(() =>
  filtroEstado.value === '' ? reservas.value : reservas.value.filter((r) => r.estado === filtroEstado.value),
)

/** Nombre del cliente. Si la lista no se pudo cargar no se sabe, así que se muestra el id en vez de decir «eliminado». */
function nombreCliente(id: number): string {
  if (errorClientes.value) return `Cliente #${id}`
  return clientes.value.find((c) => c.id === id)?.nombre ?? 'Cliente eliminado'
}

/** Números de las habitaciones; mismo criterio que `nombreCliente`. */
function numerosHabitaciones(ids: number[]): string {
  return ids
    .map((id) => {
      if (errorHabitaciones.value) return `#${id}`
      return habitaciones.value.find((h) => h.id === id)?.numero ?? 'Habitación eliminada'
    })
    .join(', ')
}

// Las tres cargas pueden fallar por la misma causa; se muestra cada mensaje distinto una sola vez
const alertas = computed(() =>
  [
    { error: errorAccion.value ?? error.value, servicio: 'gestión' },
    { error: errorClientes.value, servicio: 'gestión' },
    { error: errorHabitaciones.value, servicio: 'habitaciones' },
  ]
    .flatMap(({ error: e, servicio }) => (e ? [{ error: e, servicio, mensaje: mensajeDeError(e, servicio) }] : []))
    .filter((alerta, i, todas) => todas.findIndex((o) => o.mensaje === alerta.mensaje) === i),
)

async function ejecutar(reserva: Reserva, accion: () => Promise<unknown>, mensajeExito: string) {
  enProceso.value = reserva.id
  try {
    await accion()
    notify.exito(mensajeExito)
    errorAccion.value = null
    recargar()
  } catch (e: unknown) {
    errorAccion.value = comoApiError(e)
  } finally {
    enProceso.value = null
  }
}

async function confirmarCancelar() {
  const reserva = porCancelar.value
  if (!reserva) return
  await ejecutar(reserva, () => cancelarReserva(reserva.id), 'Reserva cancelada')
  porCancelar.value = null
}

async function confirmarEliminar() {
  const reserva = porEliminar.value
  if (!reserva) return
  await ejecutar(reserva, () => eliminarReserva(reserva.id), 'Reserva eliminada')
  porEliminar.value = null
}

function abrirNueva() {
  editando.value = null
  formAbierto.value = true
}

function abrirEdicion(reserva: Reserva) {
  editando.value = reserva
  formAbierto.value = true
}

function alGuardar() {
  formAbierto.value = false
  errorAccion.value = null
  recargar()
}

function sePuedeEditar(reserva: Reserva): boolean {
  return reserva.estado === 'PENDIENTE' || reserva.estado === 'CONFIRMADA'
}
</script>

<template>
  <PageHeader titulo="Reservas">
    <template #accion>
      <v-btn color="primary" prepend-icon="mdi-plus" :disabled="cargandoClientes || cargandoHabitaciones" @click="abrirNueva">
        Nueva reserva
      </v-btn>
    </template>
  </PageHeader>

  <div class="mb-4">
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
          <EstadoChip v-if="item.value !== ''" :valor="item.value as EstadoReserva" />
          <span v-else>Todos</span>
        </v-list-item>
      </template>
      <template #selection="{ item }">
        <EstadoChip v-if="item.value !== ''" :valor="item.value as EstadoReserva" />
        <span v-else>Todos</span>
      </template>
    </v-select>
  </div>

  <AlertaError v-for="alerta in alertas" :key="alerta.mensaje" :error="alerta.error" :servicio="alerta.servicio" />

  <v-card>
    <v-table class="tabla" hover aria-label="Lista de reservas">
      <thead>
        <tr>
          <th>ID</th>
          <th>Cliente</th>
          <th>Habitaciones</th>
          <th>Inicio</th>
          <th>Fin</th>
          <th>Estado</th>
          <th class="text-right">Precio total</th>
          <th class="text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="reserva in visibles" :key="reserva.id">
          <td>{{ reserva.id }}</td>
          <td>{{ nombreCliente(reserva.clienteId) }}</td>
          <td>{{ numerosHabitaciones(reserva.habitacionesId) }}</td>
          <td>{{ formatearFecha(reserva.fechaInicio) }}</td>
          <td>{{ formatearFecha(reserva.fechaFin) }}</td>
          <td><EstadoChip :valor="reserva.estado" /></td>
          <td class="text-right">{{ formatearMoneda(reserva.precioTotal) }}</td>
          <td class="text-right text-no-wrap">
            <v-btn
              icon="mdi-eye"
              variant="text"
              class="text-medium-emphasis"
              size="small"
              :aria-label="`Ver detalle de la reserva ${reserva.id}`"
              @click="verDetalle = reserva"
            >
              <v-icon icon="mdi-eye" />
              <v-tooltip activator="parent" location="top">Ver detalle</v-tooltip>
            </v-btn>
            <v-btn
              v-if="sePuedeEditar(reserva)"
              icon="mdi-pencil"
              variant="text"
              class="text-medium-emphasis"
              size="small"
              :disabled="enProceso === reserva.id"
              :aria-label="`Editar reserva ${reserva.id}`"
              @click="abrirEdicion(reserva)"
            >
              <v-icon icon="mdi-pencil" />
              <v-tooltip activator="parent" location="top">Editar</v-tooltip>
            </v-btn>
            <v-btn
              v-for="transicion in TRANSICIONES.filter((t) => t.desde.includes(reserva.estado))"
              :key="transicion.texto"
              size="small"
              variant="outlined"
              color="primary"
              class="mr-2"
              :disabled="enProceso === reserva.id"
              :aria-label="`${transicion.texto} reserva ${reserva.id}`"
              @click="ejecutar(reserva, () => transicion.ejecutar(reserva.id), transicion.exito)"
            >
              {{ transicion.texto }}
            </v-btn>
            <v-btn
              v-if="sePuedeEditar(reserva)"
              size="small"
              variant="text"
              color="error"
              :disabled="enProceso === reserva.id"
              :aria-label="`Cancelar reserva ${reserva.id}`"
              @click="porCancelar = reserva"
            >
              Cancelar
            </v-btn>
            <v-btn
              v-if="reserva.estado !== 'ENCURSO'"
              icon="mdi-delete"
              variant="text"
              size="small"
              color="error"
              :disabled="enProceso === reserva.id"
              :aria-label="`Eliminar reserva ${reserva.id}`"
              @click="porEliminar = reserva"
            >
              <v-icon icon="mdi-delete" />
              <v-tooltip activator="parent" location="top">Eliminar</v-tooltip>
            </v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>
    <CargandoTabla v-if="cargando" etiqueta="Cargando reservas" />
    <EmptyState v-if="!cargando && !error && reservas.length === 0" mensaje="Todavía no hay reservas registradas.">
      <template #accion>
        <v-btn color="primary" prepend-icon="mdi-plus" :disabled="cargandoClientes || cargandoHabitaciones" @click="abrirNueva">
          Nueva reserva
        </v-btn>
      </template>
    </EmptyState>
    <EmptyState
      v-if="!cargando && !error && reservas.length > 0 && visibles.length === 0"
      mensaje="No hay reservas con ese estado."
    />
  </v-card>

  <ReservaDetalleDialog
    v-if="verDetalle"
    :reserva="verDetalle"
    :cliente="nombreCliente(verDetalle.clienteId)"
    :habitaciones="numerosHabitaciones(verDetalle.habitacionesId)"
    @cerrar="verDetalle = null"
  />
  <ReservaFormDialog
    v-if="formAbierto"
    :reserva="editando"
    :clientes="clientes"
    :habitaciones="habitaciones"
    @cerrar="formAbierto = false"
    @guardado="alGuardar"
  />
  <ConfirmDialog
    :abierto="porCancelar !== null"
    titulo="Cancelar reserva"
    :mensaje="`¿Seguro que quieres cancelar la reserva ${porCancelar?.id ?? ''}?`"
    texto-confirmar="Cancelar reserva"
    :procesando="enProceso !== null && enProceso === porCancelar?.id"
    @confirmar="confirmarCancelar"
    @cancelar="porCancelar = null"
  />
  <ConfirmDialog
    :abierto="porEliminar !== null"
    titulo="Eliminar reserva"
    :mensaje="`¿Seguro que quieres eliminar la reserva ${porEliminar?.id ?? ''}? Esta acción no se puede deshacer.`"
    :procesando="enProceso !== null && enProceso === porEliminar?.id"
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
