<script setup lang="ts">
import { reactive, ref } from 'vue'
import { actualizarUsuario, crearUsuario } from '@/api/usuarios.api'
import PasswordField from '@/components/PasswordField.vue'
import { useNotify } from '@/composables/useNotify'
import type { ApiError } from '@/types/common'
import type { Usuario } from '@/types/usuario'
import { ROLES, TIPOS_DOCUMENTO } from '@/utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '@/utils/errores'
import { validarUsuario } from '@/utils/validaciones'
import type { ErroresForm, UsuarioForm } from '@/utils/validaciones'

const props = defineProps<{
  /** Usuario a editar; si es `null` el formulario crea uno nuevo. */
  usuario: Usuario | null
}>()

const emit = defineEmits<{
  cerrar: []
  /** Se emite después de guardar bien, para que la pantalla recargue la lista. */
  guardado: []
}>()

const notify = useNotify()
const esEdicion = props.usuario !== null
const opcionesDocumento = TIPOS_DOCUMENTO.map((tipo) => ({ title: `${tipo.valor} · ${tipo.etiqueta}`, value: tipo.valor }))
const opcionesRol = ROLES.map((rol) => ({ title: rol.etiqueta, value: rol.valor }))

const valores = reactive<UsuarioForm>({
  tipoDocIdentidad: props.usuario?.tipoDocIdentidad ?? '',
  numDocIdentidad: props.usuario?.numDocIdentidad ?? '',
  nombre: props.usuario?.nombre ?? '',
  telefono: props.usuario?.telefono ?? '',
  correo: props.usuario?.correo ?? '',
  password: '',
  rol: props.usuario?.rol ?? '',
})
const errores = ref<ErroresForm>({})
const errorGeneral = ref<ApiError | null>(null)
const guardando = ref(false)

async function guardar() {
  const erroresLocales = validarUsuario(valores, esEdicion)
  errores.value = erroresLocales
  errorGeneral.value = null
  if (Object.keys(erroresLocales).length > 0 || valores.tipoDocIdentidad === '' || valores.rol === '') return

  guardando.value = true
  try {
    const datos = {
      nombre: valores.nombre.trim(),
      telefono: valores.telefono.trim(),
      correo: valores.correo.trim(),
      rol: valores.rol,
    }
    if (props.usuario) {
      await actualizarUsuario(props.usuario.id, datos)
      notify.exito('Usuario actualizado')
    } else {
      await crearUsuario({
        ...datos,
        tipoDocIdentidad: valores.tipoDocIdentidad,
        numDocIdentidad: valores.numDocIdentidad.trim(),
        password: valores.password,
      })
      notify.exito('Usuario creado')
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
        <v-card-title class="px-6 pt-5">{{ esEdicion ? 'Editar usuario' : 'Nuevo usuario' }}</v-card-title>
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
            <v-select v-model="valores.rol" label="Rol *" :items="opcionesRol" :error-messages="errores.rol" />
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
