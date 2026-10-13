package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;

import java.math.BigDecimal;

public record HabitacionResponse(Long id, String numero, TipoHabitacion tipoHabitacion, Integer piso,
                                 BigDecimal precioBase, Integer capacidad, EstadoHabitacion estado) {

    public static HabitacionResponse desde(Habitacion habitacion) {
        return new HabitacionResponse(habitacion.getId(), habitacion.getNumero(),
                habitacion.getTipoHabitacion(), habitacion.getPiso(), habitacion.getPrecioBase(),
                habitacion.getCapacidad(), habitacion.getEstado());
    }
}
