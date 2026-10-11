import { useState } from 'react'
import { IconButton, InputAdornment, TextField } from '@mui/material'
import Visibility from '@mui/icons-material/Visibility'
import VisibilityOff from '@mui/icons-material/VisibilityOff'

interface PasswordFieldProps {
  valor: string
  error?: string
  onChange: (valor: string) => void
}

/** Campo de contraseña con el botón «ver» (MODELO-DE-DATOS.md §8). */
export function PasswordField({ valor, error, onChange }: PasswordFieldProps) {
  const [visible, setVisible] = useState(false)

  return (
    <TextField
      label="Contraseña"
      type={visible ? 'text' : 'password'}
      value={valor}
      onChange={(e) => onChange(e.target.value)}
      error={Boolean(error)}
      helperText={error ?? 'Entre 8 y 60 caracteres.'}
      required
      fullWidth
      autoComplete="new-password"
      slotProps={{
        input: {
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                onClick={() => setVisible((v) => !v)}
                edge="end"
              >
                {visible ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </InputAdornment>
          ),
        },
      }}
    />
  )
}
