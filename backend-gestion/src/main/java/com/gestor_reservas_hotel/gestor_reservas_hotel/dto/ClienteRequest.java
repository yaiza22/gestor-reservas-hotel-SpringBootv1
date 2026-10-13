package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que se envían para crear un cliente.
 */
public record ClienteRequest(
        @NotNull(message = "Debe seleccionar un tipo de documento.")
        TipoDocIdentidad tipoDocIdentidad,

        @NotBlank(message = "El número de documento es obligatorio.")
        @Size(max = 20, message = "El número de documento no puede superar 20 caracteres.")
        String numDocIdentidad,

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

        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres.")
        String password,

        @NotBlank(message = "La nacionalidad es obligatoria.")
        @Size(max = 50, message = "La nacionalidad no puede superar 50 caracteres.")
        String nacionalidad,

        @NotBlank(message = "El país de residencia es obligatorio.")
        @Size(max = 50, message = "El país de residencia no puede superar 50 caracteres.")
        String paisResidencia) {

    // RN-13: la contraseña nunca debe terminar en un log, ni siquiera por un toString() accidental
    @Override
    public String toString() {
        return "ClienteRequest[tipoDocIdentidad=" + tipoDocIdentidad + ", numDocIdentidad=" + numDocIdentidad
                + ", nombre=" + nombre + "]";
    }
}
