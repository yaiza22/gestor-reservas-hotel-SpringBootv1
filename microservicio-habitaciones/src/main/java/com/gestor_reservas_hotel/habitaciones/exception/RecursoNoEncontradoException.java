package com.gestor_reservas_hotel.habitaciones.exception;

/**
 * Cuando no existe el recurso solicitado (id no existe)
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
