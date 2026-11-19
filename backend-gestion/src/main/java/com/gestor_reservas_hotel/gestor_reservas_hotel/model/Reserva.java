package com.gestor_reservas_hotel.gestor_reservas_hotel.model;

import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Temporada;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Reserva de habitaciones para clientes (un cliente a 1 o muchas habitaciones); cada una es un documento de la colección "reservas".
 */
@Document("reservas")
@Getter
@Setter
public class Reserva {
    @Id
    private Long id;
    private Long clienteId;
    private List<Long> habitacionesId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantHuespedes;
    private Temporada temporada;
    private EstadoReserva estado;
    private BigDecimal precioTotal;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;

    // Se calculan la cantidad de noches de la reserva (diferencia entre fecha fin y fecha inicio).
    public int calcularNoches() {
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }
}
