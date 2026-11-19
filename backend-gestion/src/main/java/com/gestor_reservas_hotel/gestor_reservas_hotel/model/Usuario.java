package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Rol;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Empleado del hotel que usa el sistema; cada uno es un documento de la colección "usuarios".
 */
@Document("usuarios")
// El documento (tipo + número) no se repite dentro de los usuarios
@CompoundIndex(name = "documento_unico", def = "{'tipoDocIdentidad': 1, 'numDocIdentidad': 1}", unique = true)
@Getter
@Setter
public class Usuario extends Persona {
    private Rol rol;
}