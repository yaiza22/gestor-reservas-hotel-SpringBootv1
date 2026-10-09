package com.gestor_reservas_hotel.habitaciones.exception;

/**
 * Cuando se rompe una regla de negocio (problemas con estados de reserva o habitaciones)
 */
public class ConflictoNegocioException extends RuntimeException {
    public ConflictoNegocioException(String mensaje) {
        super(mensaje);
    }
}
