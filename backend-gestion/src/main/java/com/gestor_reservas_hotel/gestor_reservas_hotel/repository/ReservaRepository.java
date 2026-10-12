package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {
    Reserva save(Reserva reserva);
    Optional<Reserva> findById(Long id);
    List<Reserva> findAll();
    boolean existsCruceDeFechas(Long habitacionId, LocalDate fechaInicio, LocalDate fechaFin);
    void deleteById(Long id);
}
