import { useState } from 'react'
import type { FormEvent } from 'react'
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
} from '@mui/material'
import { actualizarUsuario, crearUsuario } from '../../api/usuarios.api'
import { PasswordField } from '../../components/PasswordField'
import { useNotify } from '../../hooks/useNotify'
import type { Usuario } from '../../types/usuario'
import type { ApiError } from '../../types/common'
import { ROLES, TIPOS_DOCUMENTO } from '../../utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '../../utils/errores'
import { validarUsuario } from '../../utils/validaciones'
import type { UsuarioForm, ErroresForm } from '../../utils/validaciones'

interface UsuarioFormDialogProps {
  /** Usuario a editar; si es `null` el formulario crea uno nuevo. */
  usuario: Usuario | null
  onCerrar: () => void
  /** Se llama después de guardar bien, para que la página recargue la lista. */
  onGuardado: () => void
}

function valoresIniciales(usuario: Usuario | null): UsuarioForm {
  return {
    tipoDocIdentidad: usuario?.tipoDocIdentidad ?? '',
    numDocIdentidad: usuario?.numDocIdentidad ?? '',
    nombre: usuario?.nombre ?? '',
    telefono: usuario?.telefono ?? '',
    correo: usuario?.correo ?? '',
    password: '',
    rol: usuario?.rol ?? '',
  }
}

/** Formulario para crear o editar un usuario (MODELO-DE-DATOS.md §8). */
export function UsuarioFormDialog({ usuario, onCerrar, onGuardado }: UsuarioFormDialogProps) {
  const notify = useNotify()
  const esEdicion = usuario !== null
  const [valores, setValores] = useState<UsuarioForm>(() => valoresIniciales(usuario))
  const [errores, setErrores] = useState<ErroresForm>({})
  const [errorGeneral, setErrorGeneral] = useState<ApiError | null>(null)
  const [guardando, setGuardando] = useState(false)

  function cambiar<K extends keyof UsuarioForm>(campo: K, valor: UsuarioForm[K]) {
    setValores((anteriores) => ({ ...anteriores, [campo]: valor }))
  }

  async function guardar(evento: FormEvent) {
    evento.preventDefault()
    const erroresLocales = validarUsuario(valores, esEdicion)
    setErrores(erroresLocales)
    setErrorGeneral(null)
    if (Object.keys(erroresLocales).length > 0 || valores.tipoDocIdentidad === '' || valores.rol === '') return

    setGuardando(true)
    try {
      const datos = {
        nombre: valores.nombre.trim(),
        telefono: valores.telefono.trim(),
        correo: valores.correo.trim(),
        rol: valores.rol,
      }
      if (usuario) {
        await actualizarUsuario(usuario.id, datos)
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
      onGuardado()
    } catch (e: unknown) {
      const error = comoApiError(e)
      const porCampo = erroresPorCampo(error)
      if (Object.keys(porCampo).length > 0) {
        setErrores(porCampo)
      } else {
        setErrorGeneral(error)
      }
      setGuardando(false)
    }
  }

  return (
    <Dialog open onClose={guardando ? undefined : onCerrar} maxWidth="sm" fullWidth>
      <Box component="form" onSubmit={guardar} noValidate>
        <DialogTitle>{esEdicion ? 'Editar usuario' : 'Nuevo usuario'}</DialogTitle>
        <DialogContent>
          {errorGeneral && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {mensajeDeError(errorGeneral, 'gestión')}
            </Alert>
          )}
          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2, pt: 1 }}>
            <TextField
              select
              label="Tipo de documento"
              value={valores.tipoDocIdentidad}
              onChange={(e) => cambiar('tipoDocIdentidad', e.target.value as UsuarioForm['tipoDocIdentidad'])}
              error={Boolean(errores.tipoDocIdentidad)}
              helperText={errores.tipoDocIdentidad}
              disabled={esEdicion}
              required
              fullWidth
            >
              {TIPOS_DOCUMENTO.map((tipo) => (
                <MenuItem key={tipo.valor} value={tipo.valor}>
                  {tipo.valor} · {tipo.etiqueta}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              label="Número de documento"
              value={valores.numDocIdentidad}
              onChange={(e) => cambiar('numDocIdentidad', e.target.value)}
              error={Boolean(errores.numDocIdentidad)}
              helperText={errores.numDocIdentidad}
              disabled={esEdicion}
              required
              fullWidth
            />
            <TextField
              label="Nombre"
              value={valores.nombre}
              onChange={(e) => cambiar('nombre', e.target.value)}
              error={Boolean(errores.nombre)}
              helperText={errores.nombre}
              required
              fullWidth
            />
            <TextField
              label="Teléfono"
              value={valores.telefono}
              onChange={(e) => cambiar('telefono', e.target.value)}
              error={Boolean(errores.telefono)}
              helperText={errores.telefono}
              required
              fullWidth
            />
            <TextField
              label="Correo electrónico"
              type="email"
              value={valores.correo}
              onChange={(e) => cambiar('correo', e.target.value)}
              error={Boolean(errores.correo)}
              helperText={errores.correo}
              required
              fullWidth
            />
            {!esEdicion && (
              <PasswordField
                valor={valores.password}
                error={errores.password}
                onChange={(valor) => cambiar('password', valor)}
              />
            )}
            <TextField
              select
              label="Rol"
              value={valores.rol}
              onChange={(e) => cambiar('rol', e.target.value as UsuarioForm['rol'])}
              error={Boolean(errores.rol)}
              helperText={errores.rol}
              required
              fullWidth
            >
              {ROLES.map((rol) => (
                <MenuItem key={rol.valor} value={rol.valor}>
                  {rol.etiqueta}
                </MenuItem>
              ))}
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={onCerrar} disabled={guardando}>
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={guardando}
            startIcon={guardando ? <CircularProgress size={16} color="inherit" /> : undefined}
          >
            Guardar
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  )
}
