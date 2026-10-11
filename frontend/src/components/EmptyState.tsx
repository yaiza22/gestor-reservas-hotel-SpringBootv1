import type { ReactNode } from 'react'
import { Box, Typography } from '@mui/material'

interface EmptyStateProps {
  mensaje: string
  /** Botón opcional («Nuevo …»). */
  accion?: ReactNode
}

/** Mensaje que se muestra cuando una lista no tiene elementos. */
export function EmptyState({ mensaje, accion }: EmptyStateProps) {
  return (
    <Box sx={{ textAlign: 'center', py: 6, px: 2 }}>
      <Typography color="text.secondary" sx={{ mb: accion ? 2 : 0 }}>
        {mensaje}
      </Typography>
      {accion}
    </Box>
  )
}
