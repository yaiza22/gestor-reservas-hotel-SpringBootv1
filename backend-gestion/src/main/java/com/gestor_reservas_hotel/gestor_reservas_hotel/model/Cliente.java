package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Huésped que hace reservas en el hotel; cada uno es un documento de la colección "clientes".
 */
@Document("clientes")
// RN-11: el documento (tipo + número) no se repite dentro de los clientes
@CompoundIndex(name = "documento_unico", def = "{'tipoDocIdentidad': 1, 'numDocIdentidad': 1}", unique = true)
@Getter
@Setter
public class Cliente extends Persona {

    private String nacionalidad;
    private String paisResidencia;
}
