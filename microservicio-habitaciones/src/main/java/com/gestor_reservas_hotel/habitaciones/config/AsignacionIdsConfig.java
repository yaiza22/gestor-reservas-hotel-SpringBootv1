package com.gestor_reservas_hotel.habitaciones.config;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.repository.GeneradorIds;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

/**
 * Pone el id numérico a las habitaciones nuevas justo antes de guardarlas en MongoDB.
 */
@Configuration
public class AsignacionIdsConfig {

    // MongoDB solo autogenera ObjectId; para conservar los ids Long de la API se piden al contador.
    // @Lazy evita el ciclo mappingMongoConverter -> callback -> GeneradorIds -> mongoTemplate -> mappingMongoConverter:
    // el generador real se busca la primera vez que se guarda, cuando mongoTemplate ya existe.
    @Bean
    BeforeConvertCallback<Habitacion> asignarIdHabitacion(@Lazy GeneradorIds generador) {
        return (habitacion, coleccion) -> {
            if (habitacion.getId() == null) {
                habitacion.setId(generador.siguiente("habitaciones"));
            }
            return habitacion;
        };
    }
}