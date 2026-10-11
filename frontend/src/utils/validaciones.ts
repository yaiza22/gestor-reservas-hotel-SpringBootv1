import type { TipoDocIdentidad } from '../types/common'
import type { Rol } from '../types/usuario'

/** Errores por campo: la clave es el nombre del campo y el valor, el mensaje. */
export type ErroresForm = Record<string, string>

/** El mismo regex del backend (MODELO-DE-DATOS.md §4.2). */
const REGEX_CORREO = /^[A-Za-z0-9+_.-]+@(.+)$/

/** Campo de texto obligatorio con largo máximo. Devuelve el mensaje de error o `undefined` si está bien. */
function textoObligatorio(valor: string, obligatorio: string, max: number, superaMax: string): string | undefined {
  if (valor.trim() === '') return obligatorio
  if (valor.length > max) return superaMax
  return undefined
}

function agregar(errores: ErroresForm, campo: string, mensaje: string | undefined) {
  if (mensaje) errores[campo] = mensaje
}

/** Valores del formulario de una persona; las listas empiezan vacías hasta que el usuario elige. */
export interface PersonaForm {
  tipoDocIdentidad: TipoDocIdentidad | ''
  numDocIdentidad: string
  nombre: string
  telefono: string
  correo: string
  password: string
}

function validarDocumento(v: PersonaForm, errores: ErroresForm) {
  if (v.tipoDocIdentidad === '') errores.tipoDocIdentidad = 'Debe seleccionar un tipo de documento.'
  agregar(
    errores,
    'numDocIdentidad',
    textoObligatorio(
      v.numDocIdentidad,
      'El número de documento es obligatorio.',
      20,
      'El número de documento no puede superar 20 caracteres.',
    ),
  )
}

function validarContacto(v: PersonaForm, errores: ErroresForm) {
  agregar(errores, 'nombre', textoObligatorio(v.nombre, 'El nombre es obligatorio.', 100, 'El nombre no puede superar 100 caracteres.'))
  agregar(
    errores,
    'telefono',
    textoObligatorio(v.telefono, 'El teléfono es obligatorio.', 20, 'El teléfono no puede superar 20 caracteres.'),
  )
  if (v.correo.trim() === '') {
    errores.correo = 'El correo electrónico es obligatorio.'
  } else if (!REGEX_CORREO.test(v.correo)) {
    errores.correo = 'El formato del correo electrónico no es válido.'
  } else if (v.correo.length > 100) {
    errores.correo = 'El correo no puede superar 100 caracteres.'
  }
}

function validarPassword(v: PersonaForm, errores: ErroresForm) {
  if (v.password === '') {
    errores.password = 'La contraseña es obligatoria.'
  } else if (v.password.length < 8 || v.password.length > 60) {
    errores.password = 'La contraseña debe tener entre 8 y 60 caracteres.'
  }
}

export interface ClienteForm extends PersonaForm {
  nacionalidad: string
  paisResidencia: string
}

/** Validaciones de MODELO-DE-DATOS.md §4.2/§4.3. Al editar no se revisan documento ni contraseña. */
export function validarCliente(v: ClienteForm, esEdicion: boolean): ErroresForm {
  const errores: ErroresForm = {}
  if (!esEdicion) validarDocumento(v, errores)
  validarContacto(v, errores)
  if (!esEdicion) validarPassword(v, errores)
  agregar(
    errores,
    'nacionalidad',
    textoObligatorio(v.nacionalidad, 'La nacionalidad es obligatoria.', 50, 'La nacionalidad no puede superar 50 caracteres.'),
  )
  agregar(
    errores,
    'paisResidencia',
    textoObligatorio(
      v.paisResidencia,
      'El país de residencia es obligatorio.',
      50,
      'El país de residencia no puede superar 50 caracteres.',
    ),
  )
  return errores
}

export interface UsuarioForm extends PersonaForm {
  rol: Rol | ''
}

/** Validaciones de usuario (MODELO-DE-DATOS.md §4.2/§4.3). */
export function validarUsuario(v: UsuarioForm, esEdicion: boolean): ErroresForm {
  const errores: ErroresForm = {}
  if (!esEdicion) validarDocumento(v, errores)
  validarContacto(v, errores)
  if (!esEdicion) validarPassword(v, errores)
  if (v.rol === '') errores.rol = 'Debe seleccionar un rol.'
  return errores
}
