package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.Temporada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Precio por noche de una habitación dependiendo dee la temporada
 */
@Schema(description = "Precio por noche de una habitación en una temporada (RN-01)")
public record PrecioResponse(
        @Schema(description = "Id de la habitación", example = "3") Long id,
        @Schema(description = "Número visible de la habitación", example = "103") String numero,
        @Schema(description = "Temporada usada en el cálculo", example = "ALTA") Temporada temporada,
        @Schema(description = "Precio por noche en pesos colombianos (150000 × 1.4 × 1.3)", example = "273000.00")
        BigDecimal precioPorNoche) {

    public static PrecioResponse desde(Habitacion habitacion, Temporada temporada) {
        return new PrecioResponse(habitacion.getId(), habitacion.getNumero(), temporada,
                habitacion.calcularPrecioPorNoche(temporada));
    }
}