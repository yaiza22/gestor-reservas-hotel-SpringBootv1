package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
    @NotNull(message = "Debe seleccionar un estado.")
    EstadoHabitacion estado) {
}
