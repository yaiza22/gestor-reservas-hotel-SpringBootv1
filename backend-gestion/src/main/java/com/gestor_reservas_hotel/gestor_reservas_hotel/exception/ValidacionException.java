package com.gestor_reservas_hotel.gestor_reservas_hotel.exception;

public class ValidacionException extends RuntimeException {
    private final String campo;

    public ValidacionException(String mensaje, String campo) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
