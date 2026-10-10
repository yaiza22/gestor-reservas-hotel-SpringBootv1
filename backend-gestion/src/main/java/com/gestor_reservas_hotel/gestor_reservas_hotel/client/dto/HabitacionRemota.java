package com.gestor_reservas_hotel.gestor_reservas_hotel.client.dto;

import java.math.BigDecimal;

public record HabitacionRemota(Long id, String numero, String tipoHabitacion, Integer piso,
                               BigDecimal precioBase, Integer capacidad, EstadoHabitacionRemoto estado) {
}
