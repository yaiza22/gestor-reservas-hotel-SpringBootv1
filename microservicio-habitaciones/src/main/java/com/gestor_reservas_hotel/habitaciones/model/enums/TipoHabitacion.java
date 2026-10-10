package com.gestor_reservas_hotel.habitaciones.model.enums;

import lombok.Getter;

import java.math.BigDecimal;

public enum TipoHabitacion {
    INDIVIDUAL("1.0"),
    DOBLE("1.4"),
    TWIN("1.4"),
    TRIPLE("1.8"),
    FAMILIAR("2.2");

    private final BigDecimal multiplicador;

    TipoHabitacion(String multiplicador) {
        this.multiplicador = new BigDecimal(multiplicador);
    }

    public BigDecimal getMultiplicador() {
        return multiplicador;
    }
}
