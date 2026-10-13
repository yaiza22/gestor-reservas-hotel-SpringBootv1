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
import { eliminarUsuario, listarUsuarios } from '../../api/usuarios.api'
import { AlertaError } from '../../components/AlertaError'
import { CargandoTabla } from '../../components/CargandoTabla'
import { ConfirmDialog } from '../../components/ConfirmDialog'
import { EmptyState } from '../../components/EmptyState'
import { EstadoChip } from '../../components/EstadoChip'
import { PageHeader } from '../../components/PageHeader'
import { useLista } from '../../hooks/useLista'
import { useNotify } from '../../hooks/useNotify'
import type { Usuario } from '../../types/usuario'
import type { ApiError } from '../../types/common'
import { comoApiError } from '../../utils/errores'
import { UsuarioFormDialog } from './UsuarioFormDialog'

/** Pantalla de usuarios: tabla, crear, editar y eliminar. Es la plantilla de las demás pantallas. */
export function UsuariosPage() {
  const notify = useNotify()
  const { datos: usuarios, cargando, error, recargar } = useLista(listarUsuarios)
  const [formAbierto, setFormAbierto] = useState(false)
  const [editando, setEditando] = useState<Usuario | null>(null)
  const [porEliminar, setPorEliminar] = useState<Usuario | null>(null)
  const [eliminando, setEliminando] = useState(false)
  const [errorAccion, setErrorAccion] = useState<ApiError | null>(null)

  function abrirNuevo() {
    setEditando(null)
    setFormAbierto(true)
  }

  function abrirEdicion(usuario: Usuario) {
    setEditando(usuario)
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
      await eliminarUsuario(porEliminar.id)
      notify.exito('Usuario eliminado')
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
      Nuevo usuario
    </Button>
  )
  const errorVisible = errorAccion ?? error

  return (
    <>
      <PageHeader titulo="Usuarios" accion={botonNuevo} />
      {errorVisible && <AlertaError error={errorVisible} servicio="gestión" />}
      <Paper>
        <TableContainer>
          <Table aria-label="Lista de usuarios">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Nombre</TableCell>
                <TableCell>Documento</TableCell>
                <TableCell>Correo</TableCell>
                <TableCell>Rol</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {usuarios.map((usuario) => (
                <TableRow key={usuario.id} hover>
                  <TableCell>{usuario.id}</TableCell>
                  <TableCell>{usuario.nombre}</TableCell>
                  <TableCell>
                    {usuario.tipoDocIdentidad} {usuario.numDocIdentidad}
                  </TableCell>
                  <TableCell>{usuario.correo}</TableCell>
                  <TableCell>
                    <EstadoChip valor={usuario.rol} />
                  </TableCell>
                  <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                    <Tooltip title="Editar">
                      <IconButton aria-label={`Editar usuario ${usuario.nombre}`} onClick={() => abrirEdicion(usuario)}>
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Eliminar">
                      <IconButton
                        aria-label={`Eliminar usuario ${usuario.nombre}`}
                        color="error"
                        onClick={() => setPorEliminar(usuario)}
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
        {cargando && <CargandoTabla etiqueta="Cargando usuarios" />}
        {!cargando && !error && usuarios.length === 0 && (
          <EmptyState mensaje="Todavía no hay usuarios registrados." accion={botonNuevo} />
        )}
      </Paper>

      {formAbierto && (
        <UsuarioFormDialog usuario={editando} onCerrar={() => setFormAbierto(false)} onGuardado={alGuardar} />
      )}
      <ConfirmDialog
        abierto={porEliminar !== null}
        titulo="Eliminar usuario"
        mensaje={`¿Seguro que quieres eliminar a ${porEliminar?.nombre ?? ''}? Esta acción no se puede deshacer.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  )
}
