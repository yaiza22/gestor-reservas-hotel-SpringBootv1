package com.gestor_reservas_hotel.habitaciones.repository;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository {
    Habitacion save(Habitacion habitacion);
    Optional<Habitacion> findById(Long id);
    List<Habitacion> findAll();
    boolean existsByNumero(String numero);
    void deleteById(Long id);
}
