package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Acceso a la colección "clientes"; Spring Data genera la implementación.
 */
public interface ClienteRepository extends MongoRepository<Cliente, Long> {
    /** Para indicar si ya existe un cliente con ese tipo y número de documento. */
    boolean existsByTipoDocIdentidadAndNumDocIdentidad(TipoDocIdentidad tipoDocIdentidad, String numDocIdentidad);
}