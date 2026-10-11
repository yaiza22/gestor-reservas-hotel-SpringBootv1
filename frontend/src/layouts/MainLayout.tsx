import { NavLink, Outlet } from 'react-router'
import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material'
import { NOMBRE_APP, SECCIONES } from '../utils/constantes'

/** Marco común de todas las pantallas: barra superior con las 4 secciones y el contenido debajo. */
export function MainLayout() {
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="static">
        <Toolbar sx={{ flexWrap: 'wrap', gap: 1, py: 1 }}>
          <Typography variant="h6" component="div" sx={{ mr: 3 }}>
            {NOMBRE_APP}
          </Typography>
          <Box component="nav" aria-label="Secciones" sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {SECCIONES.map((seccion) => (
              <Button
                key={seccion.ruta}
                component={NavLink}
                to={seccion.ruta}
                color="inherit"
                sx={{
                  '&.active': { bgcolor: 'rgba(255,255,255,0.2)', fontWeight: 700 },
                }}
              >
                {seccion.etiqueta}
              </Button>
            ))}
          </Box>
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" component="main" sx={{ py: 4 }}>
        <Outlet />
      </Container>
    </Box>
  )
}
