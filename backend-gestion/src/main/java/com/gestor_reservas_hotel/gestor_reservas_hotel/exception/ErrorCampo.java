package com.gestor_reservas_hotel.gestor_reservas_hotel.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Va para la lista de errores, se guarda el error asociado a un campo concreto, da estrcutura a los mensajes de error.
 * @param campo
 * @param mensaje
 */
public record ErrorCampo(
        @Schema(description = "Campo con el error", example = "correo")
        String campo,

        @Schema(description = "Mensaje del error", example = "El formato del correo electrónico no es válido.")
        String mensaje) {
}