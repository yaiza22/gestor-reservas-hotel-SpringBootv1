package com.gestor_reservas_hotel.gestor_reservas_hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.habitaciones")
public record PropiedadesHabitaciones(String urlBase, int timeoutConexionMs, int timeoutLecturaMs) {
}
