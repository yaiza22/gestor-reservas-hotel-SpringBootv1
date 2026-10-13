import { useState } from 'react'
import {
  Button,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import DeleteIcon from '@mui/icons-material/Delete'
import EditIcon from '@mui/icons-material/Edit'
import { eliminarCliente, listarClientes } from '../../api/clientes.api'
import { AlertaError } from '../../components/AlertaError'
import { CargandoTabla } from '../../components/CargandoTabla'
import { ConfirmDialog } from '../../components/ConfirmDialog'
import { EmptyState } from '../../components/EmptyState'
import { PageHeader } from '../../components/PageHeader'
import { useLista } from '../../hooks/useLista'
import { useNotify } from '../../hooks/useNotify'
import type { Cliente } from '../../types/cliente'
import type { ApiError } from '../../types/common'
import { comoApiError } from '../../utils/errores'
import { ClienteFormDialog } from './ClienteFormDialog'

/** Pantalla de clientes: tabla, crear, editar y eliminar. Es la plantilla de las demás pantallas. */
export function ClientesPage() {
  const notify = useNotify()
  const { datos: clientes, cargando, error, recargar } = useLista(listarClientes)
  const [formAbierto, setFormAbierto] = useState(false)
  const [editando, setEditando] = useState<Cliente | null>(null)
  const [porEliminar, setPorEliminar] = useState<Cliente | null>(null)
  const [eliminando, setEliminando] = useState(false)
  const [errorAccion, setErrorAccion] = useState<ApiError | null>(null)

  function abrirNuevo() {
    setEditando(null)
    setFormAbierto(true)
  }

  function abrirEdicion(cliente: Cliente) {
    setEditando(cliente)
    setFormAbierto(true)
  }

  function alGuardar() {
    setFormAbierto(false)
    setErrorAccion(null)
    recargar()
  }

  async function confirmarEliminar() {
    if (!porEliminar) return
    setEliminando(true)
    try {
      await eliminarCliente(porEliminar.id)
      notify.exito('Cliente eliminado')
      setErrorAccion(null)
      recargar()
    } catch (e: unknown) {
      setErrorAccion(comoApiError(e))
    } finally {
      setEliminando(false)
      setPorEliminar(null)
    }
  }

  const botonNuevo = (
    <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo}>
      Nuevo cliente
    </Button>
  )
  const errorVisible = errorAccion ?? error

  return (
    <>
      <PageHeader titulo="Clientes" accion={botonNuevo} />
      {errorVisible && <AlertaError error={errorVisible} servicio="gestión" />}
      <Paper>
        <TableContainer>
          <Table aria-label="Lista de clientes">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Documento</TableCell>
                <TableCell>Nombre</TableCell>
                <TableCell>Teléfono</TableCell>
                <TableCell>Correo</TableCell>
                <TableCell>Nacionalidad</TableCell>
                <TableCell>País de residencia</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {clientes.map((cliente) => (
                <TableRow key={cliente.id} hover>
                  <TableCell>{cliente.id}</TableCell>
                  <TableCell>
                    {cliente.tipoDocIdentidad} {cliente.numDocIdentidad}
                  </TableCell>
                  <TableCell>{cliente.nombre}</TableCell>
                  <TableCell>{cliente.telefono}</TableCell>
                  <TableCell>{cliente.correo}</TableCell>
                  <TableCell>{cliente.nacionalidad}</TableCell>
                  <TableCell>{cliente.paisResidencia}</TableCell>
                  <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                    <Tooltip title="Editar">
                      <IconButton aria-label={`Editar cliente ${cliente.nombre}`} onClick={() => abrirEdicion(cliente)}>
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Eliminar">
                      <IconButton
                        aria-label={`Eliminar cliente ${cliente.nombre}`}
                        color="error"
                        onClick={() => setPorEliminar(cliente)}
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
        {cargando && <CargandoTabla etiqueta="Cargando clientes" />}
        {!cargando && !error && clientes.length === 0 && (
          <EmptyState mensaje="Todavía no hay clientes registrados." accion={botonNuevo} />
        )}
      </Paper>

      {formAbierto && (
        <ClienteFormDialog cliente={editando} onCerrar={() => setFormAbierto(false)} onGuardado={alGuardar} />
      )}
      <ConfirmDialog
        abierto={porEliminar !== null}
        titulo="Eliminar cliente"
        mensaje={`¿Seguro que quieres eliminar a ${porEliminar?.nombre ?? ''}? Esta acción no se puede deshacer.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  )
}
