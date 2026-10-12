package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Temporada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservaResponse(Long id, Long clienteId, List<Long> habitacionesId, LocalDate fechaInicio,
                              LocalDate fechaFin, Integer noches, Integer cantHuespedes, Temporada temporada,
                              EstadoReserva estado, BigDecimal precioTotal, LocalTime horaEntrada,
                              LocalTime horaSalida) {
    public static ReservaResponse desde(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getClienteId(),
                List.copyOf(reserva.getHabitacionesId()), reserva.getFechaInicio(), reserva.getFechaFin(),
                reserva.calcularNoches(), reserva.getCantHuespedes(), reserva.getTemporada(),
                reserva.getEstado(), reserva.getPrecioTotal(), reserva.getHoraEntrada(),
                reserva.getHoraSalida());
    }
}
