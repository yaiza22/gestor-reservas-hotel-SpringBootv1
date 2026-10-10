package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que se pueden cambiar de un cliente existente (el documento y la contraseña no se editan).
 */
public record ClienteUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
        String nombre,

        @NotBlank(message = "El teléfono es obligatorio.")
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres.")
        String telefono,

        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "El formato del correo electrónico no es válido.")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
        String correo,

        @NotBlank(message = "La nacionalidad es obligatoria.")
        @Size(max = 50, message = "La nacionalidad no puede superar 50 caracteres.")
        String nacionalidad,

        @NotBlank(message = "El país de residencia es obligatorio.")
        @Size(max = 50, message = "El país de residencia no puede superar 50 caracteres.")
        String paisResidencia) {
}
