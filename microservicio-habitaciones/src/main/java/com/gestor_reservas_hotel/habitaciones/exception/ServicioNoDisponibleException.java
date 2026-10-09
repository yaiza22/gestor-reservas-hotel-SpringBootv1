package com.gestor_reservas_hotel.habitaciones.exception;

/**
 * Cuando no se puede comunicar con un microservicio (habitaciones)
 */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}