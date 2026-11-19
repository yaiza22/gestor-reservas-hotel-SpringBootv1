package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Rol;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Datos de un usuario que devuelve la API (sin contraseña).
 */
@Schema(description = "Usuario tal como lo devuelve la API (sin contraseña)")
public record UsuarioResponse(
        @Schema(description = "Id generado por el servidor", example = "1") Long id,
        @Schema(description = "Tipo de documento", example = "CC") TipoDocIdentidad tipoDocIdentidad,
        @Schema(description = "Número de documento", example = "1098765432") String numDocIdentidad,
        @Schema(description = "Nombre completo", example = "Luis Gómez") String nombre,
        @Schema(description = "Teléfono", example = "3109876543") String telefono,
        @Schema(description = "Correo electrónico", example = "luis@hotel.com") String correo,
        @Schema(description = "Rol del empleado", example = "ASISTENTE") Rol rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getTipoDocIdentidad(), usuario.getNumDocIdentidad(),
                usuario.getNombre(), usuario.getTelefono(), usuario.getCorreo(), usuario.getRol());
    }
}