package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que se pueden cambiar de un cliente existente (el documento y la contraseña no se editan).
 */
@Schema(description = "Datos editables de un cliente; el documento y la contraseña no se editan")
public record ClienteUpdateRequest(
        @Schema(description = "Nombre completo", example = "Ana María Pérez")
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
        String nombre,

        @Schema(description = "Teléfono", example = "3001234567")
        @NotBlank(message = "El teléfono es obligatorio.")
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres.")
        String telefono,

        @Schema(description = "Correo electrónico", example = "ana@correo.com")
        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "El formato del correo electrónico no es válido.")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
        String correo,

        @Schema(description = "Nacionalidad", example = "Colombiana")
        @NotBlank(message = "La nacionalidad es obligatoria.")
        @Size(max = 50, message = "La nacionalidad no puede superar 50 caracteres.")
        String nacionalidad,

        @Schema(description = "País de residencia", example = "Colombia")
        @NotBlank(message = "El país de residencia es obligatorio.")
        @Size(max = 50, message = "El país de residencia no puede superar 50 caracteres.")
        String paisResidencia) {
}