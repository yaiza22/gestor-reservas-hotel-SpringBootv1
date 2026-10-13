import { Box, CircularProgress } from '@mui/material'

interface CargandoTablaProps {
  /** Texto para lectores de pantalla, por ejemplo «Cargando clientes». */
  etiqueta: string
}

/** Indicador de progreso centrado que va debajo de una tabla mientras se cargan los datos. */
export function CargandoTabla({ etiqueta }: CargandoTablaProps) {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
      <CircularProgress aria-label={etiqueta} />
    </Box>
  )
}
