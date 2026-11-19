package com.gestor_reservas_hotel.gestor_reservas_hotel;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestorReservasHotelApplication {
	public static void main(String[] args) {
		// MongoDB guarda las fechas en UTC; fijar la zona hace que cualquier computador las lea igual
		TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
		SpringApplication.run(GestorReservasHotelApplication.class, args);
	}
}
