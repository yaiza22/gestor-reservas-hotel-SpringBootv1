package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Acceso a la colección "usuarios"; Spring Data genera la implementación.
 */
public interface UsuarioRepository extends MongoRepository<Usuario, Long> {

    /** Para indicar si ya existe un usuario con ese tipo y número de documento. */
    boolean existsByTipoDocIdentidadAndNumDocIdentidad(TipoDocIdentidad tipoDocIdentidad, String numDocIdentidad);
}