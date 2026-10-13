package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Temporada;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

// Se crea una nueva reserva con estos datos
public record ReservaRequest(
        @NotNull(message = "El cliente es obligatorio.")
        Long clienteId,

        @NotEmpty(message = "Debe agregar al menos una habitación a la lista.")
        @Size(max = 10, message = "Las habitaciones no pueden repetirse (máximo 10).")
        List<@NotNull(message = "El ID de cada habitación es obligatorio.") Long> habitacionesId,

        @NotNull(message = "La fecha de inicio es obligatoria.")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria.")
        LocalDate fechaFin,

        @NotNull(message = "La cantidad de huéspedes debe ser un número entero.")
        @Min(value = 1, message = "La cantidad de huéspedes debe ser al menos 1.")
        Integer cantHuespedes,

        @NotNull(message = "Debe seleccionar una temporada.")
        Temporada temporada) {

}