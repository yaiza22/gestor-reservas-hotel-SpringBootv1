package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Temporada;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos para crear o editar una reserva.
 */
@Schema(description = "Datos para crear o editar una reserva")
public record ReservaRequest(
        @Schema(description = "Id del cliente que reserva", example = "1")
        @NotNull(message = "El cliente es obligatorio.")
        Long clienteId,

        @Schema(description = "Ids de las habitaciones (del microservicio), sin repetir y máximo 10", example = "[1, 3]")
        @NotEmpty(message = "Debe agregar al menos una habitación a la lista.")
        @Size(max = 10, message = "Las habitaciones no pueden repetirse (máximo 10).")
        List<@NotNull(message = "El ID de cada habitación es obligatorio.") Long> habitacionesId,

        @Schema(description = "Fecha de llegada (yyyy-MM-dd)", example = "2026-11-10")
        @NotNull(message = "La fecha de inicio es obligatoria.")
        LocalDate fechaInicio,

        @Schema(description = "Fecha de salida (yyyy-MM-dd); debe ser posterior a fechaInicio",
                example = "2026-11-13")
        @NotNull(message = "La fecha de fin es obligatoria.")
        LocalDate fechaFin,

        @Schema(description = "Cantidad de huéspedes", example = "3")
        @NotNull(message = "La cantidad de huéspedes debe ser un número entero.")
        @Min(value = 1, message = "La cantidad de huéspedes debe ser al menos 1.")
        Integer cantHuespedes,

        @Schema(description = "Temporada para el precio", example = "ALTA")
        @NotNull(message = "Debe seleccionar una temporada.")
        Temporada temporada) {

}