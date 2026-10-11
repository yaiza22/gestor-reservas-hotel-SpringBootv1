package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Rol;
import lombok.Getter;
import lombok.Setter;

/**
 * Empleado del hotel que usa el sistema.
 */
@Getter
@Setter
public class Usuario extends Persona {

    private Rol rol;
}
