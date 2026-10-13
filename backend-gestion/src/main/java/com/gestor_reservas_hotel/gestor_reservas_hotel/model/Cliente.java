package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Huésped que hace reservas en el hotel.
 */
@Getter
@Setter
public class Cliente extends Persona {

    private String nacionalidad;
    private String paisResidencia;
}
