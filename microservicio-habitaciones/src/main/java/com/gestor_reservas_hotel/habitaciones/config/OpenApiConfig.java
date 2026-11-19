package com.gestor_reservas_hotel.habitaciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos generales de la documentación Swagger del microservicio de habitaciones.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI apiHabitaciones() {
        return new OpenAPI().info(new Info()
                .title("API de habitaciones del hotel")
                .version("2.0.0")
                .description("Microservicio de habitaciones del Gestor de Reservas de Hotel: creación, consulta, edición, "
                        + "cambio de estado, eliminación y calculo de precio por noche. Los datos se guardan en MongoDB "
                        + "(base hotel_habitaciones). Si la base de datos no responde, cualquier endpoint devuelve 503."));
    }
}
