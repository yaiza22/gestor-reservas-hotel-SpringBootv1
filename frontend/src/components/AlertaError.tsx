import { Alert } from '@mui/material'
import type { ApiError } from '../types/common'
import { mensajeDeError } from '../utils/errores'

interface AlertaErrorProps {
  error: ApiError
  /** Servicio que se estaba llamando («gestión», «habitaciones»), para el mensaje de «sin conexión». */
  servicio: string
}

/** Alerta roja con el mensaje que corresponde al tipo de error (409, 503, sin conexión, 500…). */
export function AlertaError({ error, servicio }: AlertaErrorProps) {
  return (
    <Alert severity="error" sx={{ mb: 2 }}>
      {mensajeDeError(error, servicio)}
    </Alert>
  )
}
