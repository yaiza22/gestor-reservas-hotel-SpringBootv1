package com.gestor_reservas_hotel.habitaciones.model;

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
    @Id
    private String id;      // nombre de la colección a la que da ids, por ejemplo "habitaciones"
    private long seq;
}