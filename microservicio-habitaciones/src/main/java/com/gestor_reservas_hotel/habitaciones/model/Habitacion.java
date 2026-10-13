package com.gestor_reservas_hotel.habitaciones.model;

import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.Temporada;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class Habitacion {
    private static final int ESCALA_DINERO = 2;

    private Long id;
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private Integer piso;
    private BigDecimal precioBase;
    private Integer capacidad;
    private EstadoHabitacion estado;

    /**
     * PrecioxNoche= precioBase * tipoHabitacion(%) * temporada(%)
     * Se escala el precio a 2 decimales (ESCALA_DINERO)
     * @param temporada
     * @return
     */
    public BigDecimal calcularPrecioPorNoche(Temporada temporada) {
        return precioBase.multiply(tipoHabitacion.getMultiplicador())
                .multiply(temporada.getMultiplicador())
                .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
    }
}
