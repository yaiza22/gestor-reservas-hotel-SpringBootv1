package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Datos de un cliente que devuelve la API (sin contraseña).
 */
@Schema(description = "Cliente tal como lo devuelve la API (sin contraseña)")
public record ClienteResponse(
        @Schema(description = "Id generado por el servidor", example = "1") Long id,
        @Schema(description = "Tipo de documento", example = "CC") TipoDocIdentidad tipoDocIdentidad,
        @Schema(description = "Número de documento", example = "1000000001") String numDocIdentidad,
        @Schema(description = "Nombre completo", example = "Ana Pérez") String nombre,
        @Schema(description = "Teléfono", example = "3001234567") String telefono,
        @Schema(description = "Correo electrónico", example = "ana@correo.com") String correo,
        @Schema(description = "Nacionalidad", example = "Colombiana") String nacionalidad,
        @Schema(description = "País de residencia", example = "Colombia") String paisResidencia) {

    public static ClienteResponse desde(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getTipoDocIdentidad(), cliente.getNumDocIdentidad(),
                cliente.getNombre(), cliente.getTelefono(), cliente.getCorreo(), cliente.getNacionalidad(),
                cliente.getPaisResidencia());
    }
}