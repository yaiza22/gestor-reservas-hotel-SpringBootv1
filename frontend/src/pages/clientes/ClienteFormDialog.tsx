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
import { actualizarCliente, crearCliente } from '../../api/clientes.api'
import { PasswordField } from '../../components/PasswordField'
import { useNotify } from '../../hooks/useNotify'
import type { Cliente } from '../../types/cliente'
import type { ApiError } from '../../types/common'
import { TIPOS_DOCUMENTO } from '../../utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '../../utils/errores'
import { validarCliente } from '../../utils/validaciones'
import type { ClienteForm, ErroresForm } from '../../utils/validaciones'

interface ClienteFormDialogProps {
  /** Cliente a editar; si es `null` el formulario crea uno nuevo. */
  cliente: Cliente | null
  onCerrar: () => void
  /** Se llama después de guardar bien, para que la página recargue la lista. */
  onGuardado: () => void
}

function valoresIniciales(cliente: Cliente | null): ClienteForm {
  return {
    tipoDocIdentidad: cliente?.tipoDocIdentidad ?? '',
    numDocIdentidad: cliente?.numDocIdentidad ?? '',
    nombre: cliente?.nombre ?? '',
    telefono: cliente?.telefono ?? '',
    correo: cliente?.correo ?? '',
    password: '',
    nacionalidad: cliente?.nacionalidad ?? '',
    paisResidencia: cliente?.paisResidencia ?? '',
  }
}

/** Formulario para crear o editar un cliente (MODELO-DE-DATOS.md §8). */
export function ClienteFormDialog({ cliente, onCerrar, onGuardado }: ClienteFormDialogProps) {
  const notify = useNotify()
  const esEdicion = cliente !== null
  const [valores, setValores] = useState<ClienteForm>(() => valoresIniciales(cliente))
  const [errores, setErrores] = useState<ErroresForm>({})
  const [errorGeneral, setErrorGeneral] = useState<ApiError | null>(null)
  const [guardando, setGuardando] = useState(false)

  function cambiar<K extends keyof ClienteForm>(campo: K, valor: ClienteForm[K]) {
    setValores((anteriores) => ({ ...anteriores, [campo]: valor }))
  }

  async function guardar(evento: FormEvent) {
    evento.preventDefault()
    const erroresLocales = validarCliente(valores, esEdicion)
    setErrores(erroresLocales)
    setErrorGeneral(null)
    if (Object.keys(erroresLocales).length > 0 || valores.tipoDocIdentidad === '') return

    setGuardando(true)
    try {
      const datos = {
        nombre: valores.nombre.trim(),
        telefono: valores.telefono.trim(),
        correo: valores.correo.trim(),
        nacionalidad: valores.nacionalidad.trim(),
        paisResidencia: valores.paisResidencia.trim(),
      }
      if (cliente) {
        await actualizarCliente(cliente.id, datos)
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
        <DialogTitle>{esEdicion ? 'Editar cliente' : 'Nuevo cliente'}</DialogTitle>
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
              onChange={(e) => cambiar('tipoDocIdentidad', e.target.value as ClienteForm['tipoDocIdentidad'])}
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
              label="Nacionalidad"
              value={valores.nacionalidad}
              onChange={(e) => cambiar('nacionalidad', e.target.value)}
              error={Boolean(errores.nacionalidad)}
              helperText={errores.nacionalidad}
              required
              fullWidth
            />
            <TextField
              label="País de residencia"
              value={valores.paisResidencia}
              onChange={(e) => cambiar('paisResidencia', e.target.value)}
              error={Boolean(errores.paisResidencia)}
              helperText={errores.paisResidencia}
              required
              fullWidth
            />
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
