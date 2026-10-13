import { useMemo, useState } from 'react'
import type { MouseEvent } from 'react'
import {
  Box,
  Button,
  IconButton,
  Menu,
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
import EditIcon from '@mui/icons-material/Edit'
import { cambiarEstadoHabitacion, eliminarHabitacion, listarHabitaciones } from '../../api/habitaciones.api'
import { AlertaError } from '../../components/AlertaError'
import { CargandoTabla } from '../../components/CargandoTabla'
import { ConfirmDialog } from '../../components/ConfirmDialog'
import { EmptyState } from '../../components/EmptyState'
import { EstadoChip } from '../../components/EstadoChip'
import { PageHeader } from '../../components/PageHeader'
import { useLista } from '../../hooks/useLista'
import { useNotify } from '../../hooks/useNotify'
import type { ApiError } from '../../types/common'
import type { EstadoHabitacion, Habitacion } from '../../types/habitacion'
import { ESTADOS_HABITACION } from '../../utils/constantes'
import { comoApiError } from '../../utils/errores'
import { etiquetaTipoHabitacion, formatearMoneda } from '../../utils/formato'
import { HabitacionFormDialog } from './HabitacionFormDialog'

/** Pantalla de habitaciones: tabla, filtro por estado, cambio rápido de estado, crear, editar y eliminar. */
export function HabitacionesPage() {
  const notify = useNotify()
  const { datos: habitaciones, cargando, error, recargar } = useLista(listarHabitaciones)
  const [filtroEstado, setFiltroEstado] = useState<EstadoHabitacion | ''>('')
  const [formAbierto, setFormAbierto] = useState(false)
  const [editando, setEditando] = useState<Habitacion | null>(null)
  const [porEliminar, setPorEliminar] = useState<Habitacion | null>(null)
  const [eliminando, setEliminando] = useState(false)
  const [errorAccion, setErrorAccion] = useState<ApiError | null>(null)
  // Menú del cambio rápido de estado: qué habitación y dónde se abre
  const [menuEstado, setMenuEstado] = useState<{ habitacion: Habitacion; ancla: HTMLElement } | null>(null)

  const visibles = useMemo(
    () => (filtroEstado === '' ? habitaciones : habitaciones.filter((h) => h.estado === filtroEstado)),
    [habitaciones, filtroEstado],
  )

  function abrirNueva() {
    setEditando(null)
    setFormAbierto(true)
  }

  function abrirEdicion(habitacion: Habitacion) {
    setEditando(habitacion)
    setFormAbierto(true)
  }

  function alGuardar() {
    setFormAbierto(false)
    setErrorAccion(null)
    recargar()
  }

  function abrirMenuEstado(habitacion: Habitacion, evento: MouseEvent<HTMLElement>) {
    setMenuEstado({ habitacion, ancla: evento.currentTarget })
  }

  async function elegirEstado(estado: EstadoHabitacion) {
    const habitacion = menuEstado?.habitacion
    setMenuEstado(null)
    if (!habitacion || habitacion.estado === estado) return
    try {
      await cambiarEstadoHabitacion(habitacion.id, estado)
      notify.exito(`Habitación ${habitacion.numero} actualizada`)
      setErrorAccion(null)
      recargar()
    } catch (e: unknown) {
      setErrorAccion(comoApiError(e))
    }
  }

  async function confirmarEliminar() {
    if (!porEliminar) return
    setEliminando(true)
    try {
      await eliminarHabitacion(porEliminar.id)
      notify.exito('Habitación eliminada')
      setErrorAccion(null)
      recargar()
    } catch (e: unknown) {
      setErrorAccion(comoApiError(e))
    } finally {
      setEliminando(false)
      setPorEliminar(null)
    }
  }

  const botonNueva = (
    <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNueva}>
      Nueva habitación
    </Button>
  )
  const errorVisible = errorAccion ?? error

  return (
    <>
      <PageHeader titulo="Habitaciones" accion={botonNueva} />
      <Box sx={{ mb: 2 }}>
        <TextField
          select
          size="small"
          label="Filtrar por estado"
          value={filtroEstado}
          onChange={(e) => setFiltroEstado(e.target.value as EstadoHabitacion | '')}
          sx={{ minWidth: 224 }}
        >
          <MenuItem value="">Todos</MenuItem>
          {ESTADOS_HABITACION.map((estado) => (
            <MenuItem key={estado} value={estado}>
              <EstadoChip valor={estado} />
            </MenuItem>
          ))}
        </TextField>
      </Box>
      {errorVisible && <AlertaError error={errorVisible} servicio="habitaciones" />}
      <Paper>
        <TableContainer>
          <Table aria-label="Lista de habitaciones">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Número</TableCell>
                <TableCell>Tipo</TableCell>
                <TableCell align="right">Piso</TableCell>
                <TableCell align="right">Precio base</TableCell>
                <TableCell align="right">Capacidad</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {visibles.map((habitacion) => (
                <TableRow key={habitacion.id} hover>
                  <TableCell>{habitacion.id}</TableCell>
                  <TableCell>{habitacion.numero}</TableCell>
                  <TableCell>{etiquetaTipoHabitacion(habitacion.tipoHabitacion)}</TableCell>
                  <TableCell align="right">{habitacion.piso}</TableCell>
                  <TableCell align="right">{formatearMoneda(habitacion.precioBase)}</TableCell>
                  <TableCell align="right">{habitacion.capacidad}</TableCell>
                  <TableCell>
                    <EstadoChip
                      valor={habitacion.estado}
                      onClick={(e) => abrirMenuEstado(habitacion, e)}
                      ariaLabel={`Cambiar estado de la habitación ${habitacion.numero}`}
                    />
                  </TableCell>
                  <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                    <Tooltip title="Editar">
                      <IconButton
                        aria-label={`Editar habitación ${habitacion.numero}`}
                        onClick={() => abrirEdicion(habitacion)}
                      >
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Eliminar">
                      <IconButton
                        aria-label={`Eliminar habitación ${habitacion.numero}`}
                        color="error"
                        onClick={() => setPorEliminar(habitacion)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        {cargando && <CargandoTabla etiqueta="Cargando habitaciones" />}
        {!cargando && !error && habitaciones.length === 0 && (
          <EmptyState mensaje="Todavía no hay habitaciones registradas." accion={botonNueva} />
        )}
        {!cargando && !error && habitaciones.length > 0 && visibles.length === 0 && (
          <EmptyState mensaje="No hay habitaciones con ese estado." />
        )}
      </Paper>

      <Menu anchorEl={menuEstado?.ancla} open={menuEstado !== null} onClose={() => setMenuEstado(null)}>
        {ESTADOS_HABITACION.map((estado) => (
          <MenuItem key={estado} selected={estado === menuEstado?.habitacion.estado} onClick={() => elegirEstado(estado)}>
            <EstadoChip valor={estado} />
          </MenuItem>
        ))}
      </Menu>
      {formAbierto && (
        <HabitacionFormDialog habitacion={editando} onCerrar={() => setFormAbierto(false)} onGuardado={alGuardar} />
      )}
      <ConfirmDialog
        abierto={porEliminar !== null}
        titulo="Eliminar habitación"
        mensaje={`¿Seguro que quieres eliminar la habitación ${porEliminar?.numero ?? ''}? Esta acción no se puede deshacer.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  )
}
