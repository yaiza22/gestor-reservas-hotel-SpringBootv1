import { BrowserRouter } from 'react-router'
import { CssBaseline, ThemeProvider } from '@mui/material'
import { NotifyProvider } from './hooks/useNotify'
import { AppRoutes } from './routes'
import { theme } from './theme'

/** Une el tema, los avisos y las rutas. */
function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <NotifyProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </NotifyProvider>
    </ThemeProvider>
  )
}

export default App
