package com.gestor_reservas_hotel.habitaciones.exception;

/**
 * Cuando una solicitud apunta a algo que no existe (un campo que no existe)
 */
public class ReferenciaInvalidaException extends RuntimeException {
    private final String campo;

    public ReferenciaInvalidaException(String mensaje, String campo) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}