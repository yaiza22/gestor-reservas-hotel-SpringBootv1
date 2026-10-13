import { createTheme } from '@mui/material/styles'

/** Único lugar con colores, tipografía y bordes de la aplicación (CONVENCIONES.md §6.6). */
export const theme = createTheme({
  palette: {
    primary: { main: '#1565c0' },
    secondary: { main: '#455a64' },
    background: { default: '#f5f7fa' },
  },
  shape: { borderRadius: 8 },
  typography: {
    fontFamily: '"Roboto", "Segoe UI", system-ui, sans-serif',
    h1: { fontSize: '1.75rem', fontWeight: 600 },
    h2: { fontSize: '1.5rem', fontWeight: 600 },
  },
  components: {
    MuiButton: { defaultProps: { disableElevation: true } },
    MuiTableCell: {
      styleOverrides: { head: { fontWeight: 600, backgroundColor: '#eceff1' } },
    },
  },
})
