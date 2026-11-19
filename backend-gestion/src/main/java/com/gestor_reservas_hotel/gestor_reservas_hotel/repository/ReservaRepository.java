package com.gestor_reservas_hotel.gestor_reservas_hotel.repository;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * Acceso a la colección "reservas"; Spring Data genera la implementación.
 */
public interface ReservaRepository extends MongoRepository<Reserva, Long> {

    /**
     * Reservas que incluyen la habitación y cuyo estado no está en la lista dada.
     * @param habitacionId id de la habitación (se busca dentro del arreglo habitacionesId)
     * @param estados      estados que se descartan
     * @return las reservas encontradas
     */
    List<Reserva> findByHabitacionesIdAndEstadoNotIn(Long habitacionId, Collection<EstadoReserva> estados);

    /**
     * Indica si la habitación ya está reservada en un rango que se cruza con el nuevo.
     * @param habitacionId      id de la habitación
     * @param inicioNueva       fecha de inicio de la reserva nueva o editada
     * @param finNueva          fecha de fin de la reserva nueva o editada
     * @param reservaIdExcluida id de la reserva que se está editando ({@code null} al crear)
     * @return {@code true} si hay cruce de fechas
     */
    default boolean existsCruceDeFechas(Long habitacionId, LocalDate inicioNueva, LocalDate finNueva,
                                        Long reservaIdExcluida) {
        // Se ignoran CANCELADA y FINALIZADA, y la propia reserva al editar; el día de salida queda libre, por eso se compara con "isBefore" (<) y no con "<="
        return findByHabitacionesIdAndEstadoNotIn(habitacionId,
                EnumSet.of(EstadoReserva.CANCELADA, EstadoReserva.FINALIZADA)).stream()
                .filter(r -> !r.getId().equals(reservaIdExcluida))
                .anyMatch(r -> inicioNueva.isBefore(r.getFechaFin()) && r.getFechaInicio().isBefore(finNueva));
    }
}
