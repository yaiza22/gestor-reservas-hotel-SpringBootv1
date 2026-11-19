package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que se envían para crear un cliente.
 */
@Schema(description = "Datos para crear un cliente")
public record ClienteRequest(
        @Schema(description = "Tipo de documento", example = "CC")
        @NotNull(message = "Debe seleccionar un tipo de documento.")
        TipoDocIdentidad tipoDocIdentidad,

        @Schema(description = "Número de documento; con el tipo, no puede repetirse entre los clientes",
                example = "1000000001")
        @NotBlank(message = "El número de documento es obligatorio.")
        @Size(max = 20, message = "El número de documento no puede superar 20 caracteres.")
        String numDocIdentidad,

        @Schema(description = "Nombre completo", example = "Ana Pérez")
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

        @Schema(description = "Contraseña de 8 a 60 caracteres; nunca se devuelve (RN-13)", example = "Clave1234",
                accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres.")
        String password,

        @Schema(description = "Nacionalidad", example = "Colombiana")
        @NotBlank(message = "La nacionalidad es obligatoria.")
        @Size(max = 50, message = "La nacionalidad no puede superar 50 caracteres.")
        String nacionalidad,

        @Schema(description = "País de residencia", example = "Colombia")
        @NotBlank(message = "El país de residencia es obligatorio.")
        @Size(max = 50, message = "El país de residencia no puede superar 50 caracteres.")
        String paisResidencia) {

    // La contraseña nunca debe terminar en un log
    @Override
    public String toString() {
        return "ClienteRequest[tipoDocIdentidad=" + tipoDocIdentidad + ", numDocIdentidad=" + numDocIdentidad + ", nombre=" + nombre + "]";
    }
}