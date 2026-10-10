package com.gestor_reservas_hotel.habitaciones.dto;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.Temporada;

import java.math.BigDecimal;

public record PrecioResponse(Long id, String numero, Temporada temporada, BigDecimal precioPorNoche) {

    public static PrecioResponse desde(Habitacion habitacion, Temporada temporada) {
        return new PrecioResponse(habitacion.getId(), habitacion.getNumero(), temporada,
                habitacion.calcularPrecioPorNoche(temporada));
    }
}
