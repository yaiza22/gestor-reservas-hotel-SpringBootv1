package com.gestor_reservas_hotel.habitaciones.repository;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Acceso a la colección "habitaciones"; ahora es Spring Data quien genera la implementación.
 */
public interface HabitacionRepository extends MongoRepository<Habitacion, Long> {

    /** Si ya existe una habitación con ese número. */
    boolean existsByNumero(String numero);
}