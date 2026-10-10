package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HabitacionUpdateRequest(
    @NotNull(message = "Debe seleccionar un tipo de habitación.")
    TipoHabitacion tipoHabitacion,

    @NotNull(message = "El piso debe ser un número entero.")
    @Min(value = 0, message = "El piso no puede ser negativo.")
    Integer piso,

    @NotNull(message = "El precio base debe ser un número.")
    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor que 0.")
    @Digits(integer = 10, fraction = 2, message = "El precio base admite hasta 10 dígitos y máximo 2 decimales.")
    BigDecimal precioBase,

    @NotNull(message = "La capacidad debe ser un número entero.")
    @Min(value = 1, message = "La capacidad debe estar entre 1 y 10.")
    @Max(value = 10, message = "La capacidad debe estar entre 1 y 10.")
    Integer capacidad,

    EstadoHabitacion estado) {
}
