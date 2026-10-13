import { useMemo, useState } from 'react'
import {
  Box,
  Button,
  IconButton,
  MenuItem,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import DeleteIcon from '@mui/icons-material/Delete'
import VisibilityIcon from '@mui/icons-material/Visibility'
import { listarClientes } from '../../api/clientes.api'
import { listarHabitaciones } from '../../api/habitaciones.api'
import {
  cancelarReserva,
  confirmarReserva,
  eliminarReserva,
  listarReservas,
  registrarCheckIn,
  registrarCheckOut,
} from '../../api/reservas.api'
import { AlertaError } from '../../components/AlertaError'
import { CargandoTabla } from '../../components/CargandoTabla'
import { ConfirmDialog } from '../../components/ConfirmDialog'
import { EmptyState } from '../../components/EmptyState'
import { EstadoChip } from '../../components/EstadoChip'
import { PageHeader } from '../../components/PageHeader'
import { useLista } from '../../hooks/useLista'
import { useNotify } from '../../hooks/useNotify'
import type { ApiError } from '../../types/common'
import type { EstadoReserva, Reserva } from '../../types/reserva'
import { ESTADOS_RESERVA } from '../../utils/constantes'
import { comoApiError, mensajeDeError } from '../../utils/errores'
import { formatearFecha, formatearMoneda } from '../../utils/formato'
import { ReservaDetalleDialog } from './ReservaDetalleDialog'
import { ReservaFormDialog } from './ReservaFormDialog'

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

/** Pantalla de reservas: tabla, filtro por estado, crear, cambiar de estado y eliminar. */
export function ReservasPage() {
  const notify = useNotify()
  const { datos: reservas, cargando, error, recargar } = useLista(listarReservas)
  const { datos: clientes, cargando: cargandoClientes, error: errorClientes } = useLista(listarClientes)
  const { datos: habitaciones, cargando: cargandoHabitaciones, error: errorHabitaciones } = useLista(listarHabitaciones)
  const [filtroEstado, setFiltroEstado] = useState<EstadoReserva | ''>('')
  const [formAbierto, setFormAbierto] = useState(false)
  const [verDetalle, setVerDetalle] = useState<Reserva | null>(null)
  const [porCancelar, setPorCancelar] = useState<Reserva | null>(null)
  const [porEliminar, setPorEliminar] = useState<Reserva | null>(null)
  // Reserva con una acción en marcha: se deshabilitan sus botones para no enviarla dos veces
  const [enProceso, setEnProceso] = useState<number | null>(null)
  const [errorAccion, setErrorAccion] = useState<ApiError | null>(null)

  const visibles = useMemo(
    () => (filtroEstado === '' ? reservas : reservas.filter((r) => r.estado === filtroEstado)),
    [reservas, filtroEstado],
  )

  /** Nombre del cliente. Si la lista no se pudo cargar no se sabe, así que se muestra el id en vez de decir «eliminado». */
  function nombreCliente(id: number): string {
    if (errorClientes) return `Cliente #${id}`
    return clientes.find((c) => c.id === id)?.nombre ?? 'Cliente eliminado'
  }

  /** Números de las habitaciones; mismo criterio que `nombreCliente`. */
  function numerosHabitaciones(ids: number[]): string {
    return ids
      .map((id) => {
        if (errorHabitaciones) return `#${id}`
        return habitaciones.find((h) => h.id === id)?.numero ?? 'Habitación eliminada'
      })
      .join(', ')
  }

  async function ejecutar(reserva: Reserva, accion: () => Promise<unknown>, mensajeExito: string) {
    setEnProceso(reserva.id)
    try {
      await accion()
      notify.exito(mensajeExito)
      setErrorAccion(null)
      recargar()
    } catch (e: unknown) {
      setErrorAccion(comoApiError(e))
    } finally {
      setEnProceso(null)
    }
  }

  async function confirmarCancelar() {
    if (!porCancelar) return
    await ejecutar(porCancelar, () => cancelarReserva(porCancelar.id), 'Reserva cancelada')
    setPorCancelar(null)
  }

  async function confirmarEliminar() {
    if (!porEliminar) return
    await ejecutar(porEliminar, () => eliminarReserva(porEliminar.id), 'Reserva eliminada')
    setPorEliminar(null)
  }

  function alGuardar() {
    setFormAbierto(false)
    setErrorAccion(null)
    recargar()
  }

  const botonNueva = (
    <Button
      variant="contained"
      startIcon={<AddIcon />}
      disabled={cargandoClientes || cargandoHabitaciones}
      onClick={() => setFormAbierto(true)}
    >
      Nueva reserva
    </Button>
  )
  // Las tres cargas pueden fallar por la misma causa; se muestra cada mensaje distinto una sola vez
  const alertas = [
    { error: errorAccion ?? error, servicio: 'gestión' },
    { error: errorClientes, servicio: 'gestión' },
    { error: errorHabitaciones, servicio: 'habitaciones' },
  ]
    .flatMap(({ error: e, servicio }) => (e ? [{ error: e, servicio, mensaje: mensajeDeError(e, servicio) }] : []))
    .filter((alerta, i, todas) => todas.findIndex((o) => o.mensaje === alerta.mensaje) === i)

  return (
    <>
      <PageHeader titulo="Reservas" accion={botonNueva} />
      <Box sx={{ mb: 2 }}>
        <TextField
          select
          size="small"
          label="Filtrar por estado"
          value={filtroEstado}
          onChange={(e) => setFiltroEstado(e.target.value as EstadoReserva | '')}
          sx={{ minWidth: 224 }}
        >
          <MenuItem value="">Todos</MenuItem>
          {ESTADOS_RESERVA.map((estado) => (
            <MenuItem key={estado} value={estado}>
              <EstadoChip valor={estado} />
            </MenuItem>
          ))}
        </TextField>
      </Box>
      {alertas.map((alerta) => (
        <AlertaError key={alerta.mensaje} error={alerta.error} servicio={alerta.servicio} />
      ))}
      <Paper>
        <TableContainer>
          <Table aria-label="Lista de reservas">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Cliente</TableCell>
                <TableCell>Habitaciones</TableCell>
                <TableCell>Inicio</TableCell>
                <TableCell>Fin</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell align="right">Precio total</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {visibles.map((reserva) => (
                <TableRow key={reserva.id} hover>
                  <TableCell>{reserva.id}</TableCell>
                  <TableCell>{nombreCliente(reserva.clienteId)}</TableCell>
                  <TableCell>{numerosHabitaciones(reserva.habitacionesId)}</TableCell>
                  <TableCell>{formatearFecha(reserva.fechaInicio)}</TableCell>
                  <TableCell>{formatearFecha(reserva.fechaFin)}</TableCell>
                  <TableCell>
                    <EstadoChip valor={reserva.estado} />
                  </TableCell>
                  <TableCell align="right">{formatearMoneda(reserva.precioTotal)}</TableCell>
                  <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                    <Tooltip title="Ver detalle">
                      <IconButton aria-label={`Ver detalle de la reserva ${reserva.id}`} onClick={() => setVerDetalle(reserva)}>
                        <VisibilityIcon />
                      </IconButton>
                    </Tooltip>
                    {TRANSICIONES.filter((t) => t.desde.includes(reserva.estado)).map((t) => (
                      <Button
                        key={t.texto}
                        size="small"
                        variant="outlined"
                        disabled={enProceso === reserva.id}
                        aria-label={`${t.texto} reserva ${reserva.id}`}
                        onClick={() => ejecutar(reserva, () => t.ejecutar(reserva.id), t.exito)}
                        sx={{ mr: 1 }}
                      >
                        {t.texto}
                      </Button>
                    ))}
                    {(reserva.estado === 'PENDIENTE' || reserva.estado === 'CONFIRMADA') && (
                      <Button
                        size="small"
                        color="error"
                        disabled={enProceso === reserva.id}
                        aria-label={`Cancelar reserva ${reserva.id}`}
                        onClick={() => setPorCancelar(reserva)}
                      >
                        Cancelar
                      </Button>
                    )}
                    {reserva.estado !== 'ENCURSO' && (
                      <Tooltip title="Eliminar">
                        <span>
                          <IconButton
                            aria-label={`Eliminar reserva ${reserva.id}`}
                            color="error"
                            disabled={enProceso === reserva.id}
                            onClick={() => setPorEliminar(reserva)}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </span>
                      </Tooltip>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        {cargando && <CargandoTabla etiqueta="Cargando reservas" />}
        {!cargando && !error && reservas.length === 0 && (
          <EmptyState mensaje="Todavía no hay reservas registradas." accion={botonNueva} />
        )}
        {!cargando && !error && reservas.length > 0 && visibles.length === 0 && (
          <EmptyState mensaje="No hay reservas con ese estado." />
        )}
      </Paper>

      {verDetalle && (
        <ReservaDetalleDialog
          reserva={verDetalle}
          cliente={nombreCliente(verDetalle.clienteId)}
          habitaciones={numerosHabitaciones(verDetalle.habitacionesId)}
          onCerrar={() => setVerDetalle(null)}
        />
      )}
      {formAbierto && (
        <ReservaFormDialog
          clientes={clientes}
          habitaciones={habitaciones}
          onCerrar={() => setFormAbierto(false)}
          onGuardado={alGuardar}
        />
      )}
      <ConfirmDialog
        abierto={porCancelar !== null}
        titulo="Cancelar reserva"
        mensaje={`¿Seguro que quieres cancelar la reserva ${porCancelar?.id ?? ''}?`}
        textoConfirmar="Cancelar reserva"
        procesando={enProceso !== null && enProceso === porCancelar?.id}
        onConfirmar={confirmarCancelar}
        onCancelar={() => setPorCancelar(null)}
      />
      <ConfirmDialog
        abierto={porEliminar !== null}
        titulo="Eliminar reserva"
        mensaje={`¿Seguro que quieres eliminar la reserva ${porEliminar?.id ?? ''}? Esta acción no se puede deshacer.`}
        procesando={enProceso !== null && enProceso === porEliminar?.id}
        onConfirmar={confirmarEliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  )
}
