package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;

/**
 * Datos de un cliente que devuelve la API (sin contraseña).
 */
public record ClienteResponse(Long id, TipoDocIdentidad tipoDocIdentidad, String numDocIdentidad, String nombre,
                              String telefono, String correo, String nacionalidad, String paisResidencia) {

    public static ClienteResponse desde(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getTipoDocIdentidad(), cliente.getNumDocIdentidad(),
                cliente.getNombre(), cliente.getTelefono(), cliente.getCorreo(), cliente.getNacionalidad(),
                cliente.getPaisResidencia());
    }
}
