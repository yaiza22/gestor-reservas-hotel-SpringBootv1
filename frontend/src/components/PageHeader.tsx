import type { ReactNode } from 'react'
import { Box, Typography } from '@mui/material'

interface PageHeaderProps {
  titulo: string
  /** Botón principal («Nuevo …»), a la derecha. */
  accion?: ReactNode
}

/** Título de la página a la izquierda y la acción principal a la derecha. */
export function PageHeader({ titulo, accion }: PageHeaderProps) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexWrap: 'wrap',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 2,
        mb: 3,
      }}
    >
      <Typography variant="h1" component="h1">
        {titulo}
      </Typography>
      {accion}
    </Box>
  )
}
