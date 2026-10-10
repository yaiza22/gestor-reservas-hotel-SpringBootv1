package com.gestor_reservas_hotel.gestor_reservas_hotel.client.dto;

import java.math.BigDecimal;

public record PrecioRemoto(Long id, String numero, String temporada, BigDecimal precioPorNoche) {
}
