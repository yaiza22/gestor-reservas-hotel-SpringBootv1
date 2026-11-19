<script setup lang="ts">
import { reactive, ref } from 'vue'
import { actualizarCliente, crearCliente } from '@/api/clientes.api'
import PasswordField from '@/components/PasswordField.vue'
import { useNotify } from '@/composables/useNotify'
import type { Cliente } from '@/types/cliente'
import type { ApiError } from '@/types/common'
import { TIPOS_DOCUMENTO } from '@/utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '@/utils/errores'
import { validarCliente } from '@/utils/validaciones'
import type { ClienteForm, ErroresForm } from '@/utils/validaciones'

const props = defineProps<{
  /** Cliente a editar; si es `null` el formulario crea uno nuevo. */
  cliente: Cliente | null
}>()

const emit = defineEmits<{
  cerrar: []
  /** Se emite después de guardar bien, para que la pantalla recargue la lista. */
  guardado: []
}>()

const notify = useNotify()
const esEdicion = props.cliente !== null
const opcionesDocumento = TIPOS_DOCUMENTO.map((tipo) => ({ title: `${tipo.valor} · ${tipo.etiqueta}`, value: tipo.valor }))

const valores = reactive<ClienteForm>({
  tipoDocIdentidad: props.cliente?.tipoDocIdentidad ?? '',
  numDocIdentidad: props.cliente?.numDocIdentidad ?? '',
  nombre: props.cliente?.nombre ?? '',
  telefono: props.cliente?.telefono ?? '',
  correo: props.cliente?.correo ?? '',
  password: '',
  nacionalidad: props.cliente?.nacionalidad ?? '',
  paisResidencia: props.cliente?.paisResidencia ?? '',
})
const errores = ref<ErroresForm>({})
const errorGeneral = ref<ApiError | null>(null)
const guardando = ref(false)

async function guardar() {
  const erroresLocales = validarCliente(valores, esEdicion)
  errores.value = erroresLocales
  errorGeneral.value = null
  if (Object.keys(erroresLocales).length > 0 || valores.tipoDocIdentidad === '') return

  guardando.value = true
  try {
    const datos = {
      nombre: valores.nombre.trim(),
      telefono: valores.telefono.trim(),
      correo: valores.correo.trim(),
      nacionalidad: valores.nacionalidad.trim(),
      paisResidencia: valores.paisResidencia.trim(),
    }
    if (props.cliente) {
      await actualizarCliente(props.cliente.id, datos)
      notify.exito('Cliente actualizado')
    } else {
      await crearCliente({
        ...datos,
        tipoDocIdentidad: valores.tipoDocIdentidad,
        numDocIdentidad: valores.numDocIdentidad.trim(),
        password: valores.password,
      })
      notify.exito('Cliente creado')
    }
    emit('guardado')
  } catch (e: unknown) {
    const error = comoApiError(e)
    const porCampo = erroresPorCampo(error)
    if (Object.keys(porCampo).length > 0) {
      errores.value = porCampo
    } else {
      errorGeneral.value = error
    }
    guardando.value = false
  }
}
</script>

<template>
  <v-dialog :model-value="true" :persistent="guardando" max-width="600" @update:model-value="(abierto: boolean) => !abierto && emit('cerrar')">
    <v-card>
      <v-form @submit.prevent="guardar">
        <v-card-title class="px-6 pt-5">{{ esEdicion ? 'Editar cliente' : 'Nuevo cliente' }}</v-card-title>
        <v-card-text class="px-6">
          <v-alert v-if="errorGeneral" type="error" variant="tonal" class="mb-4">
            {{ mensajeDeError(errorGeneral, 'gestión') }}
          </v-alert>
          <div class="campos">
            <v-select
              v-model="valores.tipoDocIdentidad"
              label="Tipo de documento *"
              :items="opcionesDocumento"
              :error-messages="errores.tipoDocIdentidad"
              :disabled="esEdicion"
            />
            <v-text-field
              v-model="valores.numDocIdentidad"
              label="Número de documento *"
              :error-messages="errores.numDocIdentidad"
              :disabled="esEdicion"
            />
            <v-text-field v-model="valores.nombre" label="Nombre *" :error-messages="errores.nombre" />
            <v-text-field v-model="valores.telefono" label="Teléfono *" :error-messages="errores.telefono" />
            <v-text-field
              v-model="valores.correo"
              label="Correo electrónico *"
              type="email"
              :error-messages="errores.correo"
            />
            <PasswordField v-if="!esEdicion" v-model="valores.password" :error="errores.password" />
            <v-text-field v-model="valores.nacionalidad" label="Nacionalidad *" :error-messages="errores.nacionalidad" />
            <v-text-field
              v-model="valores.paisResidencia"
              label="País de residencia *"
              :error-messages="errores.paisResidencia"
            />
          </div>
        </v-card-text>
        <v-card-actions class="px-6 pb-4">
          <v-spacer />
          <v-btn color="primary" :disabled="guardando" @click="emit('cerrar')">Cancelar</v-btn>
          <v-btn type="submit" color="primary" variant="flat" :loading="guardando" :disabled="guardando">Guardar</v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.campos {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 16px;
  row-gap: 12px;
  padding-top: 8px;
}

@media (max-width: 599px) {
  .campos {
    grid-template-columns: 1fr;
  }
}
</style>
