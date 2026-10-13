package com.gestor_reservas_hotel.habitaciones.exception;

/**
 * Va para la lista de errores, se guarda el error asociado a un campo concreto, da estrcutura a los mensajes de error.
 * @param campo
 * @param mensaje
 */
public record ErrorCampo(String campo, String mensaje) {
}