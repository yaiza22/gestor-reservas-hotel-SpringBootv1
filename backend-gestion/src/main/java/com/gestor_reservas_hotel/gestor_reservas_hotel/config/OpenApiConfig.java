package com.gestor_reservas_hotel.gestor_reservas_hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos generales de la documentación Swagger del backend de gestión.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI apiGestion() {
        return new OpenAPI().info(new Info()
                .title("API de gestión del hotel")
                .version("2.0.0")
                .description("Backend de gestión del Gestor de Reservas de Hotel: clientes, usuarios (empleados) y "
                        + "reservas. Los datos se guardan en MongoDB (base hotel_gestion). Para validar habitaciones, "
                        + "calcular precios y cambiar su estado consulta al microservicio de habitaciones (puerto 8081). "
                        + "Si la base de datos o el microservicio no responden, devuelve 503."));
    }
}