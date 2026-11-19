package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Nuevo estado de una habitación.
 */
@Schema(description = "Nuevo estado de una habitación")
public record CambioEstadoRequest(
        @Schema(description = "Estado al que pasa la habitación", example = "OCUPADA")
        @NotNull(message = "Debe seleccionar un estado.")
        EstadoHabitacion estado) {
}