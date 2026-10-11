package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Rol;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;

/**
 * Datos de un usuario que devuelve la API (sin contraseña, RN-13).
 */
public record UsuarioResponse(Long id, TipoDocIdentidad tipoDocIdentidad, String numDocIdentidad, String nombre,
                              String telefono, String correo, Rol rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getTipoDocIdentidad(), usuario.getNumDocIdentidad(),
                usuario.getNombre(), usuario.getTelefono(), usuario.getCorreo(), usuario.getRol());
    }
}
