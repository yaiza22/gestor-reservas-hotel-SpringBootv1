package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.ErrorCampo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Forma del JSON de error (ProblemDetail + codigo, errores y timestamp); para documentar en Swagger.
 */
@Schema(description = "Formato común de los errores (application/problem+json)")
public record ErrorResponse(
        @Schema(description = "Título corto del error", example = "Solicitud inválida")
        String title,

        @Schema(description = "Código de estado HTTP", example = "400")
        Integer status,

        @Schema(description = "Mensaje en español que se puede mostrar al usuario", example = "Hay campos con errores")
        String detail,

        @Schema(description = "Ruta de la solicitud", example = "/api/clientes")
        String instance,

        @Schema(description = "Código del error; el frontend decide con él", example = "VALIDACION",
                allowableValues = {"VALIDACION", "REFERENCIA_INEXISTENTE", "NO_ENCONTRADO", "METODO_NO_PERMITIDO",
                        "CONFLICTO", "TIPO_NO_SOPORTADO", "ERROR_INTERNO", "SERVICIO_NO_DISPONIBLE"})
        String codigo,

        @Schema(description = "Errores por campo (solo en los 400)")
        List<ErrorCampo> errores,

        @Schema(description = "Momento del error, en UTC", example = "2026-11-19T15:04:05Z")
        String timestamp) {
}