package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a los datos de los clientes (con nombres de métodos al estilo Spring Data).
 */
public interface ClienteRepository {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(Long id);

    List<Cliente> findAll();

    boolean existsById(Long id);

    boolean existsByTipoDocIdentidadAndNumDocIdentidad(TipoDocIdentidad tipoDocIdentidad, String numDocIdentidad);

    void deleteById(Long id);
}
