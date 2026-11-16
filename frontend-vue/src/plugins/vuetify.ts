import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

export const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'hotel',
    themes: {
      hotel: {
        dark: false,
        colors: {
          primary: '#1565c0',
          secondary: '#455a64',
          background: '#f5f7fa',
          surface: '#ffffff',
          info: '#0288d1',
          success: '#2e7d32',
          warning: '#ed6c02',
          error: '#d32f2f',
        },
      },
    },
  },
  defaults: {
    VBtn: { variant: 'flat' },
    VTextField: { variant: 'outlined', density: 'comfortable' },
    VSelect: { variant: 'outlined', density: 'comfortable' },
  },
})
