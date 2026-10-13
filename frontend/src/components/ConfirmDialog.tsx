import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material'

interface ConfirmDialogProps {
  abierto: boolean
  titulo: string
  mensaje: string
  textoConfirmar?: string
  /** Mientras es true, los botones se deshabilitan para no enviar dos veces. */
  procesando?: boolean
  onConfirmar: () => void
  onCancelar: () => void
}

/** Diálogo para acciones destructivas: el botón de confirmar es rojo (CONVENCIONES.md §6.6). */
export function ConfirmDialog({
  abierto,
  titulo,
  mensaje,
  textoConfirmar = 'Eliminar',
  procesando = false,
  onConfirmar,
  onCancelar,
}: ConfirmDialogProps) {
  return (
    <Dialog open={abierto} onClose={procesando ? undefined : onCancelar} maxWidth="xs" fullWidth>
      <DialogTitle>{titulo}</DialogTitle>
      <DialogContent>
        <DialogContentText>{mensaje}</DialogContentText>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onCancelar} disabled={procesando}>
          Cancelar
        </Button>
        <Button color="error" variant="contained" onClick={onConfirmar} disabled={procesando}>
          {textoConfirmar}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
