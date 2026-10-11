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
import { actualizarHabitacion, crearHabitacion } from '../../api/habitaciones.api'
import { useNotify } from '../../hooks/useNotify'
import type { ApiError } from '../../types/common'
import type { Habitacion } from '../../types/habitacion'
import { ESTADOS_HABITACION, TIPOS_HABITACION } from '../../utils/constantes'
import { comoApiError, erroresPorCampo, mensajeDeError } from '../../utils/errores'
import { validarHabitacion } from '../../utils/validaciones'
import type { ErroresForm, HabitacionForm } from '../../utils/validaciones'
import { EstadoChip } from '../../components/EstadoChip'

interface HabitacionFormDialogProps {
  /** Habitación a editar; si es `null` el formulario crea una nueva. */
  habitacion: Habitacion | null
  onCerrar: () => void
  /** Se llama después de guardar bien, para que la página recargue la lista. */
  onGuardado: () => void
}

function valoresIniciales(habitacion: Habitacion | null): HabitacionForm {
  return {
    numero: habitacion?.numero ?? '',
    tipoHabitacion: habitacion?.tipoHabitacion ?? '',
    piso: habitacion ? String(habitacion.piso) : '',
    precioBase: habitacion ? String(habitacion.precioBase) : '',
    capacidad: habitacion ? String(habitacion.capacidad) : '',
    estado: habitacion?.estado ?? 'DISPONIBLE',
  }
}

/** Formulario para crear o editar una habitación (MODELO-DE-DATOS.md §5). */
export function HabitacionFormDialog({ habitacion, onCerrar, onGuardado }: HabitacionFormDialogProps) {
  const notify = useNotify()
  const esEdicion = habitacion !== null
  const [valores, setValores] = useState<HabitacionForm>(() => valoresIniciales(habitacion))
  const [errores, setErrores] = useState<ErroresForm>({})
  const [errorGeneral, setErrorGeneral] = useState<ApiError | null>(null)
  const [guardando, setGuardando] = useState(false)

  function cambiar<K extends keyof HabitacionForm>(campo: K, valor: HabitacionForm[K]) {
    setValores((anteriores) => ({ ...anteriores, [campo]: valor }))
  }

  async function guardar(evento: FormEvent) {
    evento.preventDefault()
    const erroresLocales = validarHabitacion(valores, esEdicion)
    setErrores(erroresLocales)
    setErrorGeneral(null)
    if (Object.keys(erroresLocales).length > 0 || valores.tipoHabitacion === '') return

    setGuardando(true)
    try {
      const datos = {
        tipoHabitacion: valores.tipoHabitacion,
        piso: Number(valores.piso),
        precioBase: Number(valores.precioBase),
        capacidad: Number(valores.capacidad),
        estado: valores.estado,
      }
      if (habitacion) {
        await actualizarHabitacion(habitacion.id, datos)
        notify.exito('Habitación actualizada')
      } else {
        await crearHabitacion({ ...datos, numero: valores.numero.trim() })
        notify.exito('Habitación creada')
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
        <DialogTitle>{esEdicion ? 'Editar habitación' : 'Nueva habitación'}</DialogTitle>
        <DialogContent>
          {errorGeneral && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {mensajeDeError(errorGeneral, 'habitaciones')}
            </Alert>
          )}
          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2, pt: 1 }}>
            <TextField
              label="Número"
              value={valores.numero}
              onChange={(e) => cambiar('numero', e.target.value)}
              error={Boolean(errores.numero)}
              helperText={errores.numero}
              disabled={esEdicion}
              required
              fullWidth
            />
            <TextField
              select
              label="Tipo de habitación"
              value={valores.tipoHabitacion}
              onChange={(e) => cambiar('tipoHabitacion', e.target.value as HabitacionForm['tipoHabitacion'])}
              error={Boolean(errores.tipoHabitacion)}
              helperText={errores.tipoHabitacion}
              required
              fullWidth
            >
              {TIPOS_HABITACION.map((tipo) => (
                <MenuItem key={tipo.valor} value={tipo.valor}>
                  {tipo.etiqueta}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              label="Piso"
              value={valores.piso}
              onChange={(e) => cambiar('piso', e.target.value)}
              error={Boolean(errores.piso)}
              helperText={errores.piso}
              slotProps={{ htmlInput: { inputMode: 'numeric' } }}
              required
              fullWidth
            />
            <TextField
              label="Capacidad (personas)"
              value={valores.capacidad}
              onChange={(e) => cambiar('capacidad', e.target.value)}
              error={Boolean(errores.capacidad)}
              helperText={errores.capacidad}
              slotProps={{ htmlInput: { inputMode: 'numeric' } }}
              required
              fullWidth
            />
            <TextField
              label="Precio base por noche (COP)"
              value={valores.precioBase}
              onChange={(e) => cambiar('precioBase', e.target.value)}
              error={Boolean(errores.precioBase)}
              helperText={errores.precioBase}
              slotProps={{ htmlInput: { inputMode: 'decimal' } }}
              required
              fullWidth
            />
            <TextField
              select
              label="Estado"
              value={valores.estado}
              onChange={(e) => cambiar('estado', e.target.value as HabitacionForm['estado'])}
              error={Boolean(errores.estado)}
              helperText={errores.estado}
              fullWidth
            >
              {ESTADOS_HABITACION.map((estado) => (
                <MenuItem key={estado} value={estado}>
                  <EstadoChip valor={estado} />
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
