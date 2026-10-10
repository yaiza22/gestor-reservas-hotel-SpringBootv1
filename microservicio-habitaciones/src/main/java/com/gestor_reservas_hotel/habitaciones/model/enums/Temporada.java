package com.gestor_reservas_hotel.habitaciones.model.enums;

import java.math.BigDecimal;

public enum Temporada {
    BAJA("1.0"),
    ALTA("1.3");

    private final BigDecimal multiplicador;

    Temporada(String multiplicador) {
        this.multiplicador = new BigDecimal(multiplicador);
    }

    public BigDecimal getMultiplicador() {
        return multiplicador;
    }
}
