package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Guarda los usuarios en memoria; los datos se pierden al reiniciar el servicio.
 */
@Repository
public class UsuarioRepositoryMemoria implements UsuarioRepository {

    private final Map<Long, Usuario> almacen = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(secuencia.incrementAndGet());
        }
        almacen.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Usuario> findAll() {
        return almacen.values().stream().sorted(Comparator.comparing(Usuario::getId)).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return almacen.containsKey(id);
    }

    @Override
    public boolean existsByTipoDocIdentidadAndNumDocIdentidad(TipoDocIdentidad tipoDocIdentidad,
                                                               String numDocIdentidad) {
        return almacen.values().stream().anyMatch(usuario ->
                usuario.getTipoDocIdentidad() == tipoDocIdentidad
                        && usuario.getNumDocIdentidad().equals(numDocIdentidad));
    }

    @Override
    public void deleteById(Long id) {
        almacen.remove(id);
    }
}
