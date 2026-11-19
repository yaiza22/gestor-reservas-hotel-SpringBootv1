package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Datos para crear una habitación.
 */
@Schema(description = "Datos para crear una habitación")
public record HabitacionRequest(
        @Schema(description = "Número visible de la habitación; no puede repetirse", example = "401")
        @NotBlank(message = "El número de la habitación es obligatorio.")
        @Size(max = 10, message = "El número no puede superar 10 carácteres.")
        String numero,

        @Schema(description = "Tipo de habitación", example = "DOBLE")
        @NotNull(message = "Debe seleccionar un tipo de habitación.")
        TipoHabitacion tipoHabitacion,

        @Schema(description = "Piso donde está la habitación", example = "4")
        @NotNull(message = "El piso debe ser un número entero.")
        @Min(value = 0, message = "El piso no puede ser negativo.")
        Integer piso,

        @Schema(description = "Precio base por noche, en pesos colombianos", example = "170000.00")
        @NotNull(message = "El precio base debe ser un número.")
        @DecimalMin(value = "0.01", message = "El precio base debe ser mayor que 0.")
        @Digits(integer = 10, fraction = 2, message = "El precio base admite hasta 10 dígitos y máximo 2 decimales.")
        BigDecimal precioBase,

        @Schema(description = "Número máximo de huéspedes", example = "2")
        @NotNull(message = "La capacidad debe ser un número entero.")
        @Min(value = 1, message = "La capacidad debe estar entre 1 y 10.")
        @Max(value = 10, message = "La capacidad debe estar entre 1 y 10.")
        Integer capacidad,

        @Schema(description = "Estado inicial; si no se envía, queda DISPONIBLE", example = "DISPONIBLE")
        EstadoHabitacion estado) {
}