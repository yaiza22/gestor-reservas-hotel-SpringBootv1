import { useState } from 'react'
import type { FormEvent } from 'react'
import {
  Alert,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  ListItemText,
  MenuItem,
  TextField,
} from '@mui/material'
import { actualizarReserva, crearReserva } from '../../api/reservas.api'
import { useNotify } from '../../hooks/useNotify'
import type { Cliente } from '../../types/cliente'
import type { ApiError } from '../../types/common'
import type { Habitacion } from '../../types/habitacion'
import type { Reserva } from '../../types/reserva'
import { TEMPORADAS } from '../../utils/constantes'
import { EstadoChip } from '../../components/EstadoChip'
import { comoApiError, erroresPorCampo, mensajeDeError } from '../../utils/errores'
import { etiquetaTipoHabitacion } from '../../utils/formato'
import { validarReserva } from '../../utils/validaciones'
import type { ErroresForm, ReservaForm } from '../../utils/validaciones'

interface ReservaFormDialogProps {
  /** Reserva a editar; si es `null` el formulario crea una nueva. */
  reserva: Reserva | null
  /** Clientes y habitaciones que se pueden elegir (se piden en la página). */
  clientes: Cliente[]
  habitaciones: Habitacion[]
  onCerrar: () => void
  /** Se llama después de guardar bien, para que la página recargue la lista. */
  onGuardado: () => void
}

function valoresIniciales(reserva: Reserva | null): ReservaForm {
  return {
    clienteId: reserva?.clienteId ?? '',
    habitacionesId: reserva?.habitacionesId ?? [],
    fechaInicio: reserva?.fechaInicio ?? '',
    fechaFin: reserva?.fechaFin ?? '',
    cantHuespedes: reserva ? String(reserva.cantHuespedes) : '',
    temporada: reserva?.temporada ?? '',
  }
}

/** Formulario para crear o editar una reserva (MODELO-DE-DATOS.md §6 y §8). El precio lo calcula el servidor. */
export function ReservaFormDialog({ reserva, clientes, habitaciones, onCerrar, onGuardado }: ReservaFormDialogProps) {
  const notify = useNotify()
  const esEdicion = reserva !== null
  const [valores, setValores] = useState<ReservaForm>(() => valoresIniciales(reserva))
  const [errores, setErrores] = useState<ErroresForm>({})
  const [errorGeneral, setErrorGeneral] = useState<ApiError | null>(null)
  const [guardando, setGuardando] = useState(false)

  function cambiar<K extends keyof ReservaForm>(campo: K, valor: ReservaForm[K]) {
    setValores((anteriores) => ({ ...anteriores, [campo]: valor }))
  }

  function numeroDe(id: number): string {
    return habitaciones.find((h) => h.id === id)?.numero ?? String(id)
  }

  async function guardar(evento: FormEvent) {
    evento.preventDefault()
    const erroresLocales = validarReserva(valores)
    setErrores(erroresLocales)
    setErrorGeneral(null)
    if (Object.keys(erroresLocales).length > 0 || valores.clienteId === '' || valores.temporada === '') return

    setGuardando(true)
    try {
      const datos = {
        clienteId: valores.clienteId,
        habitacionesId: valores.habitacionesId,
        fechaInicio: valores.fechaInicio,
        fechaFin: valores.fechaFin,
        cantHuespedes: Number(valores.cantHuespedes),
        temporada: valores.temporada,
      }
      if (reserva) {
        await actualizarReserva(reserva.id, datos)
        notify.exito('Reserva actualizada')
      } else {
        await crearReserva(datos)
        notify.exito('Reserva creada')
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
        <DialogTitle>{esEdicion ? 'Editar reserva' : 'Nueva reserva'}</DialogTitle>
        <DialogContent>
          {errorGeneral && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {mensajeDeError(errorGeneral, 'gestión')}
            </Alert>
          )}
          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2, pt: 1 }}>
            <TextField
              select
              label="Cliente"
              value={valores.clienteId}
              onChange={(e) => cambiar('clienteId', Number(e.target.value))}
              error={Boolean(errores.clienteId)}
              helperText={errores.clienteId}
              required
              fullWidth
              sx={{ gridColumn: { sm: '1 / -1' } }}
            >
              {clientes.map((cliente) => (
                <MenuItem key={cliente.id} value={cliente.id}>
                  {cliente.nombre} · {cliente.tipoDocIdentidad} {cliente.numDocIdentidad}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Habitaciones"
              value={valores.habitacionesId}
              onChange={(e) => cambiar('habitacionesId', (e.target.value as unknown as number[]).map(Number))}
              error={Boolean(errores.habitacionesId)}
              helperText={errores.habitacionesId}
              slotProps={{
                select: {
                  multiple: true,
                  renderValue: (elegidas) => (elegidas as number[]).map(numeroDe).join(', '),
                },
              }}
              required
              fullWidth
              sx={{ gridColumn: { sm: '1 / -1' } }}
            >
              {habitaciones.map((habitacion) => (
                <MenuItem key={habitacion.id} value={habitacion.id}>
                  <Checkbox checked={valores.habitacionesId.includes(habitacion.id)} />
                  <ListItemText
                    primary={`${habitacion.numero} · ${etiquetaTipoHabitacion(habitacion.tipoHabitacion)}`}
                    secondary={
                      <Box component="span" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        Capacidad {habitacion.capacidad}
                        <EstadoChip valor={habitacion.estado} />
                      </Box>
                    }
                  />
                </MenuItem>
              ))}
            </TextField>
            <TextField
              type="date"
              label="Fecha de inicio"
              value={valores.fechaInicio}
              onChange={(e) => cambiar('fechaInicio', e.target.value)}
              error={Boolean(errores.fechaInicio)}
              helperText={errores.fechaInicio}
              slotProps={{ inputLabel: { shrink: true } }}
              required
              fullWidth
            />
            <TextField
              type="date"
              label="Fecha de fin"
              value={valores.fechaFin}
              onChange={(e) => cambiar('fechaFin', e.target.value)}
              error={Boolean(errores.fechaFin)}
              helperText={errores.fechaFin}
              slotProps={{ inputLabel: { shrink: true } }}
              required
              fullWidth
            />
            <TextField
              label="Cantidad de huéspedes"
              value={valores.cantHuespedes}
              onChange={(e) => cambiar('cantHuespedes', e.target.value)}
              error={Boolean(errores.cantHuespedes)}
              helperText={errores.cantHuespedes}
              slotProps={{ htmlInput: { inputMode: 'numeric' } }}
              required
              fullWidth
            />
            <TextField
              select
              label="Temporada"
              value={valores.temporada}
              onChange={(e) => cambiar('temporada', e.target.value as ReservaForm['temporada'])}
              error={Boolean(errores.temporada)}
              helperText={errores.temporada}
              required
              fullWidth
            >
              {TEMPORADAS.map((temporada) => (
                <MenuItem key={temporada.valor} value={temporada.valor}>
                  {temporada.etiqueta}
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
