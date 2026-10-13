import type { MouseEvent } from 'react'
import { Chip } from '@mui/material'
import type { ChipProps } from '@mui/material'
import type { EstadoHabitacion } from '../types/habitacion'
import type { EstadoReserva } from '../types/reserva'
import type { Rol } from '../types/usuario'

type ValorEstado = EstadoReserva | EstadoHabitacion | Rol

interface Apariencia {
  texto: string
  color: ChipProps['color']
}

/** Mapa fijo: cada valor siempre se ve igual en toda la aplicación. */
const APARIENCIA: Record<ValorEstado, Apariencia> = {
  // Reservas
  PENDIENTE: { texto: 'Pendiente', color: 'warning' },
  CONFIRMADA: { texto: 'Confirmada', color: 'info' },
  ENCURSO: { texto: 'En curso', color: 'primary' },
  FINALIZADA: { texto: 'Finalizada', color: 'success' },
  CANCELADA: { texto: 'Cancelada', color: 'error' },
  // Habitaciones
  DISPONIBLE: { texto: 'Disponible', color: 'success' },
  OCUPADA: { texto: 'Ocupada', color: 'error' },
  MANTENIMIENTO: { texto: 'Mantenimiento', color: 'warning' },
  LIMPIEZA: { texto: 'Limpieza', color: 'info' },
  // Roles
  ADMINISTRADOR: { texto: 'Administrador', color: 'primary' },
  ASISTENTE: { texto: 'Asistente', color: 'default' },
}

interface EstadoChipProps {
  valor: ValorEstado
  /** Si se pasa, la etiqueta se vuelve un botón (por ejemplo, para cambiar el estado). */
  onClick?: (evento: MouseEvent<HTMLElement>) => void
  /** Texto para lectores de pantalla; solo se usa cuando la etiqueta es un botón. */
  ariaLabel?: string
}

/** Etiqueta de color para cualquier estado o rol. */
export function EstadoChip({ valor, onClick, ariaLabel }: EstadoChipProps) {
  const { texto, color } = APARIENCIA[valor]
  return <Chip label={texto} color={color} size="small" onClick={onClick} aria-label={onClick ? ariaLabel : undefined} />
}
