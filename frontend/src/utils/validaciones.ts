import type { TipoDocIdentidad } from '../types/common'
import type { EstadoHabitacion, TipoHabitacion } from '../types/habitacion'
import type { Temporada } from '../types/reserva'
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

/** Valores del formulario de habitación; los números se guardan como texto hasta validarlos. */
export interface HabitacionForm {
  numero: string
  tipoHabitacion: TipoHabitacion | ''
  piso: string
  precioBase: string
  capacidad: string
  estado: EstadoHabitacion
}

const REGEX_ENTERO = /^-?\d+$/
const REGEX_DECIMAL = /^-?\d+(\.\d+)?$/

/** Validaciones de MODELO-DE-DATOS.md §5.2. Al editar no se revisa el número. */
export function validarHabitacion(v: HabitacionForm, esEdicion: boolean): ErroresForm {
  const errores: ErroresForm = {}
  if (!esEdicion) {
    agregar(
      errores,
      'numero',
      textoObligatorio(
        v.numero,
        'El número de la habitación es obligatorio.',
        10,
        'El número no puede superar 10 caracteres.',
      ),
    )
  }
  if (v.tipoHabitacion === '') errores.tipoHabitacion = 'Debe seleccionar un tipo de habitación.'

  const piso = v.piso.trim()
  if (!REGEX_ENTERO.test(piso)) errores.piso = 'El piso debe ser un número entero.'
  else if (Number(piso) < 0) errores.piso = 'El piso no puede ser negativo.'

  const precio = v.precioBase.trim()
  if (!REGEX_DECIMAL.test(precio)) errores.precioBase = 'El precio base debe ser un número.'
  else if (Number(precio) <= 0) errores.precioBase = 'El precio base debe ser mayor que 0.'
  else if ((precio.split('.')[1] ?? '').length > 2) errores.precioBase = 'El precio base admite máximo 2 decimales.'

  const capacidad = v.capacidad.trim()
  if (!REGEX_ENTERO.test(capacidad)) errores.capacidad = 'La capacidad debe ser un número entero.'
  else if (Number(capacidad) < 1 || Number(capacidad) > 10) errores.capacidad = 'La capacidad debe estar entre 1 y 10.'

  return errores
}

/** Valores del formulario de reserva; el cliente y la temporada empiezan vacíos hasta que el usuario elige. */
export interface ReservaForm {
  clienteId: number | ''
  habitacionesId: number[]
  /** Formato yyyy-MM-dd, el mismo que entrega el selector de fecha. */
  fechaInicio: string
  fechaFin: string
  cantHuespedes: string
  temporada: Temporada | ''
}

/** Validaciones de MODELO-DE-DATOS.md §6.2. Las que consultan otros datos (cruce de fechas, capacidad…) las hace el servidor. */
export function validarReserva(v: ReservaForm): ErroresForm {
  const errores: ErroresForm = {}
  if (v.clienteId === '') errores.clienteId = 'El cliente es obligatorio.'
  if (v.habitacionesId.length === 0) {
    errores.habitacionesId = 'Debes agregar al menos una habitación a la lista.'
  } else if (v.habitacionesId.length > 10) {
    errores.habitacionesId = 'Las habitaciones no pueden repetirse (máximo 10).'
  }
  if (v.fechaInicio === '') errores.fechaInicio = 'La fecha de inicio es obligatoria.'
  if (v.fechaFin === '') {
    errores.fechaFin = 'La fecha de fin es obligatoria.'
  } else if (v.fechaInicio !== '' && v.fechaFin <= v.fechaInicio) {
    // Las fechas yyyy-MM-dd se pueden comparar como texto
    errores.fechaFin = 'La fecha de fin debe ser posterior a la fecha de inicio.'
  }
  const huespedes = v.cantHuespedes.trim()
  if (!REGEX_ENTERO.test(huespedes)) errores.cantHuespedes = 'La cantidad de huéspedes debe ser un número entero.'
  else if (Number(huespedes) < 1) errores.cantHuespedes = 'La cantidad de huéspedes debe ser al menos 1.'
  if (v.temporada === '') errores.temporada = 'Debe seleccionar una temporada.'
  return errores
}
