package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Guarda los clientes en memoria; los datos se pierden al reiniciar el servicio.
 */
@Repository
public class ClienteRepositoryMemoria implements ClienteRepository {

    private final Map<Long, Cliente> almacen = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(secuencia.incrementAndGet());
        }
        almacen.put(cliente.getId(), cliente);
        return cliente;
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Cliente> findAll() {
        return almacen.values().stream().sorted(Comparator.comparing(Cliente::getId)).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return almacen.containsKey(id);
    }

    @Override
    public boolean existsByTipoDocIdentidadAndNumDocIdentidad(TipoDocIdentidad tipoDocIdentidad,
                                                               String numDocIdentidad) {
        return almacen.values().stream().anyMatch(cliente ->
                cliente.getTipoDocIdentidad() == tipoDocIdentidad
                        && cliente.getNumDocIdentidad().equals(numDocIdentidad));
    }

    @Override
    public void deleteById(Long id) {
        almacen.remove(id);
    }
}
