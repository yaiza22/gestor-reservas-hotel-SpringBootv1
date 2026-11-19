package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Habitación que envia como respuesta la API.
 */
@Schema(description = "Habitación tal como la devuelve la API")
public record HabitacionResponse(
        @Schema(description = "Id generado por el servidor", example = "3") Long id,
        @Schema(description = "Número visible de la habitación", example = "103") String numero,
        @Schema(description = "Tipo de habitación", example = "DOBLE") TipoHabitacion tipoHabitacion,
        @Schema(description = "Piso", example = "1") Integer piso,
        @Schema(description = "Precio base por noche, en pesos colombianos", example = "150000.00") BigDecimal precioBase,
        @Schema(description = "Número máximo de huéspedes", example = "2") Integer capacidad,
        @Schema(description = "Estado actual", example = "DISPONIBLE") EstadoHabitacion estado) {

    public static HabitacionResponse desde(Habitacion habitacion) {
        return new HabitacionResponse(habitacion.getId(), habitacion.getNumero(),
                habitacion.getTipoHabitacion(), habitacion.getPiso(), habitacion.getPrecioBase(),
                habitacion.getCapacidad(), habitacion.getEstado());
    }
}