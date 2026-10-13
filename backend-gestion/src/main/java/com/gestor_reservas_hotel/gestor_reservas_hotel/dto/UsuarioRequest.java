package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Rol;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que se envían para crear un usuario.
 */
public record UsuarioRequest(
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

        @NotNull(message = "Debe seleccionar un rol.")
        Rol rol) {

    // RN-13: la contraseña nunca debe terminar en un log, ni siquiera por un toString() accidental
    @Override
    public String toString() {
        return "UsuarioRequest[tipoDocIdentidad=" + tipoDocIdentidad + ", numDocIdentidad=" + numDocIdentidad
                + ", nombre=" + nombre + ", rol=" + rol + "]";
    }
}
