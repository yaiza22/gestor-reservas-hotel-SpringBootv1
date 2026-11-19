package com.gestor_reservas_hotel.gestor_reservas_hotel.dto;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Temporada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Reserva tal como la devuelve la API.
 */
@Schema(description = "Reserva tal como la devuelve la API")
public record ReservaResponse(
        @Schema(description = "Id generado por el servidor", example = "1") Long id,
        @Schema(description = "Id del cliente", example = "1") Long clienteId,
        @Schema(description = "Ids de las habitaciones", example = "[1, 3]") List<Long> habitacionesId,
        @Schema(description = "Fecha de llegada", example = "2026-11-10") LocalDate fechaInicio,
        @Schema(description = "Fecha de salida", example = "2026-11-13") LocalDate fechaFin,
        @Schema(description = "Noches: días entre fechaInicio y fechaFin (calculado)", example = "3") Integer noches,
        @Schema(description = "Cantidad de huéspedes", example = "3") Integer cantHuespedes,
        @Schema(description = "Temporada", example = "ALTA") Temporada temporada,
        @Schema(description = "Estado de la reserva", example = "PENDIENTE") EstadoReserva estado,
        @Schema(description = "Precio total en pesos colombianos", example = "1209000.00") BigDecimal precioTotal,
        @Schema(description = "Hora del check-in; null hasta que se hace", example = "15:20:10") LocalTime horaEntrada,
        @Schema(description = "Hora del check-out; null hasta que se hace", example = "11:05:42") LocalTime horaSalida) {

    public static ReservaResponse desde(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getClienteId(),
                List.copyOf(reserva.getHabitacionesId()), reserva.getFechaInicio(), reserva.getFechaFin(),
                reserva.calcularNoches(), reserva.getCantHuespedes(), reserva.getTemporada(),
                reserva.getEstado(), reserva.getPrecioTotal(), reserva.getHoraEntrada(),
                reserva.getHoraSalida());
    }
}