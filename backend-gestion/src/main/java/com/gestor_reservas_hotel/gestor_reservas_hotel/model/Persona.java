package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos comunes de cualquier persona del sistema; de ella heredan {@code Cliente} y {@code Usuario}.
 */
@Getter
@Setter
public abstract class Persona {

    private Long id;
    private TipoDocIdentidad tipoDocIdentidad;
    private String numDocIdentidad;
    private String nombre;
    private String telefono;
    private String correo;
    private String password;
}
