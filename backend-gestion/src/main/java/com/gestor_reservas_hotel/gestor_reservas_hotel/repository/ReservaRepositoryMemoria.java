package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Se guardan las reservas en la memoria local, se borra al reiniciar el servico
@Repository
public class ReservaRepositoryMemoria implements ReservaRepository {
    private static final Set<EstadoReserva> ESTADOS_QUE_NO_OCUPAN = EnumSet.of(EstadoReserva.CANCELADA, EstadoReserva.FINALIZADA);
    private final Map<Long, Reserva> almacen = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Reserva save(Reserva reserva) {
        if (reserva.getId() == null) {
            reserva.setId(secuencia.incrementAndGet());
        }
        almacen.put(reserva.getId(), reserva);
        return reserva;
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Reserva> findAll() {
        return almacen.values().stream()
                .sorted(Comparator.comparing(Reserva::getId))
                .toList();
    }

    @Override
    public boolean existsCruceDeFechas(Long habitacionId, LocalDate inicioNueva, LocalDate finNueva) {
        return almacen.values().stream()
                .filter(r -> !ESTADOS_QUE_NO_OCUPAN.contains(r.getEstado()))
                .filter(r -> r.getHabitacionesId().contains(habitacionId))
                .anyMatch(r -> inicioNueva.isBefore(r.getFechaFin()) && r.getFechaInicio().isBefore(finNueva));
    }

    @Override
    public void deleteById(Long id) {
        almacen.remove(id);
    }
}
