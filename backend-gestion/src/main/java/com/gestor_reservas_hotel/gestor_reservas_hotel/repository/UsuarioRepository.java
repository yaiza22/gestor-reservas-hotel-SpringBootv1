package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a los datos de los usuarios (con nombres de métodos al estilo Spring Data).
 */
public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    List<Usuario> findAll();

    boolean existsById(Long id);

    boolean existsByTipoDocIdentidadAndNumDocIdentidad(TipoDocIdentidad tipoDocIdentidad, String numDocIdentidad);

    void deleteById(Long id);
}
