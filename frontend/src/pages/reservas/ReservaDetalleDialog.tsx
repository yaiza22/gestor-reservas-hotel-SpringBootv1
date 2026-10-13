import type { ReactNode } from 'react'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material'
import { EstadoChip } from '../../components/EstadoChip'
import type { Reserva } from '../../types/reserva'
import { formatearFecha, formatearMoneda } from '../../utils/formato'

interface ReservaDetalleDialogProps {
  reserva: Reserva
  /** Nombre del cliente y números de habitación, ya resueltos por la página. */
  cliente: string
  habitaciones: string
  onCerrar: () => void
}

interface DatoProps {
  etiqueta: string
  children: ReactNode
}

/** Un dato del detalle: etiqueta pequeña arriba y el valor debajo. */
function Dato({ etiqueta, children }: DatoProps) {
  return (
    <Box>
      <Typography variant="caption" color="text.secondary">
        {etiqueta}
      </Typography>
      <Typography component="div">{children}</Typography>
    </Box>
  )
}

/** Ventana de solo lectura con todos los datos de una reserva, incluidas las noches y el precio total. */
export function ReservaDetalleDialog({ reserva, cliente, habitaciones, onCerrar }: ReservaDetalleDialogProps) {
  return (
    <Dialog open onClose={onCerrar} maxWidth="sm" fullWidth>
      <DialogTitle>Reserva {reserva.id}</DialogTitle>
      <DialogContent>
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2, pt: 1 }}>
          <Dato etiqueta="Cliente">{cliente}</Dato>
          <Dato etiqueta="Habitaciones">{habitaciones}</Dato>
          <Dato etiqueta="Fecha de inicio">{formatearFecha(reserva.fechaInicio)}</Dato>
          <Dato etiqueta="Fecha de fin">{formatearFecha(reserva.fechaFin)}</Dato>
          <Dato etiqueta="Noches">{reserva.noches}</Dato>
          <Dato etiqueta="Huéspedes">{reserva.cantHuespedes}</Dato>
          <Dato etiqueta="Temporada">{reserva.temporada === 'ALTA' ? 'Alta' : 'Baja'}</Dato>
          <Dato etiqueta="Estado">
            <EstadoChip valor={reserva.estado} />
          </Dato>
          <Dato etiqueta="Precio total">{formatearMoneda(reserva.precioTotal)}</Dato>
          <Dato etiqueta="Hora de entrada">{reserva.horaEntrada ?? 'Sin registrar'}</Dato>
          <Dato etiqueta="Hora de salida">{reserva.horaSalida ?? 'Sin registrar'}</Dato>
        </Box>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onCerrar}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  )
}
