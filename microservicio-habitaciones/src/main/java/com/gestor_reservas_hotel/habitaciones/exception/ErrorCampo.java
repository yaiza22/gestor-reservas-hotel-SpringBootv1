package com.gestor_reservas_hotel.habitaciones.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Va para la lista de errores, se guarda el error asociado a un campo concreto, da estrcutura a los mensajes de error.
 * @param campo
 * @param mensaje
 */
public record ErrorCampo(
        @Schema(description = "Campo con el error", example = "precioBase")
        String campo,

        @Schema(description = "Mensaje del error", example = "El precio base debe ser mayor que 0.")
        String mensaje) {
}