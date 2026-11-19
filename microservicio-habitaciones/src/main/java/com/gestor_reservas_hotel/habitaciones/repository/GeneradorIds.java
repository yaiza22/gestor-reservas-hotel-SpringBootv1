package com.gestor_reservas_hotel.habitaciones.repository;

import com.gestor_reservas_hotel.habitaciones.model.Contador;

import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Entrega el siguiente id de una colección sin repetirlo, aunque lleguen dos solicitudes a la vez.
 */
@Component
@RequiredArgsConstructor
public class GeneradorIds {

    private final MongoOperations mongo;

    /**
     * Suma 1 al contador de la colección y devuelve el valor nuevo; si el contador no existe, lo crea.
     * @param coleccion nombre de la colección
     * @return el siguiente id libre
     */
    public long siguiente(String coleccion) {
        Contador contador = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(coleccion)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Contador.class);
        return contador.getSeq();
    }
}
