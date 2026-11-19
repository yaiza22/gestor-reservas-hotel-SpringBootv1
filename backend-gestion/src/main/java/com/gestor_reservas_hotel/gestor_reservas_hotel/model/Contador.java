package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Último id que se entrega para una colección; vive en la colección "contadores".
 */
@Document("contadores")
@Getter
@Setter
public class Contador {
    // nombre de la colección a la que da ids: "clientes", "usuarios" o "reservas"
    @Id
    private String id;
    private long seq;
}
