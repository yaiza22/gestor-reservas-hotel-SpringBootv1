const FORMATO_MONEDA = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
})

/** Muestra un valor en pesos colombianos, por ejemplo «$ 150.000». */
export function formatearMoneda(valor: number): string {
  return FORMATO_MONEDA.format(valor)
}

/** Convierte «yyyy-MM-dd» (lo que envía la API) en «dd/MM/yyyy» (lo que ve el usuario). */
export function formatearFecha(fechaIso: string): string {
  const [anio, mes, dia] = fechaIso.split('-')
  return `${dia}/${mes}/${anio}`
}
