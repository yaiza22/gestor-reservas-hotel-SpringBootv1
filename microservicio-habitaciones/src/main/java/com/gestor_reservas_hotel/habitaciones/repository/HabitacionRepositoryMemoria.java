package com.gestor_reservas_hotel.habitaciones.repository;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class HabitacionRepositoryMemoria implements HabitacionRepository {
    private final Map<Long, Habitacion> almacen = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Habitacion save(Habitacion habitacion) {
        if (habitacion.getId() == null) {
            habitacion.setId(secuencia.incrementAndGet());
        }
        almacen.put(habitacion.getId(), habitacion);
        return habitacion;
    }

    @Override
    public Optional<Habitacion> findById(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Habitacion> findAll() {
        return almacen.values().stream().sorted(Comparator.comparing(Habitacion::getId)).toList();
    }

    @Override
    public boolean existsByNumero(String numero) {
        return almacen.values().stream().anyMatch(h -> h.getNumero().equals(numero));
    }

    @Override
    public void deleteById(Long id) {
        almacen.remove(id);
    }
}
