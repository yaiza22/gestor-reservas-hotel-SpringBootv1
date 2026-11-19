package com.gestor_reservas_hotel.gestor_reservas_hotel.config;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.repository.GeneradorIds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

/**
 * Pone el id numérico a los clientes y usuarios nuevos justo antes de guardarlos en MongoDB.
 */
@Configuration
public class AsignacionIdsConfig {

    // MongoDB solo autogenera ObjectId; para conservar los ids Long de la API se piden al contador.
    // @Lazy evita el ciclo mappingMongoConverter -> callback -> GeneradorIds -> mongoTemplate -> mappingMongoConverter.
    @Bean
    BeforeConvertCallback<Cliente> asignarIdCliente(@Lazy GeneradorIds generador) {
        return (cliente, coleccion) -> {
            if (cliente.getId() == null) {
                cliente.setId(generador.siguiente("clientes"));
            }
            return cliente;
        };
    }

    @Bean
    BeforeConvertCallback<Usuario> asignarIdUsuario(@Lazy GeneradorIds generador) {
        return (usuario, coleccion) -> {
            if (usuario.getId() == null) {
                usuario.setId(generador.siguiente("usuarios"));
            }
            return usuario;
        };
    }
}
