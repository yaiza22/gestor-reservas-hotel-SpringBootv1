package com.gestor_reservas_hotel.gestor_reservas_hotel.config;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ClienteRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import com.gestor_reservas_hotel.gestor_reservas_hotel.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Carga los 4 clientes de demostración al arrancar si {@code app.datos-iniciales=true}.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.datos-iniciales", havingValue = "true")
public class DatosInicialesClientes implements ApplicationRunner {

    private static final String PASSWORD_DEMOSTRACION = "Clave1234";

    private final ClienteService clienteService;

    @Override
    public void run(ApplicationArguments args) {
        crear(TipoDocIdentidad.CC, "1000000001", "Ana Pérez", "3001234567", "ana@correo.com",
                "Colombiana", "Colombia");
        crear(TipoDocIdentidad.CC, "1000000002", "Carlos Ruiz", "3002345678", "carlos@correo.com",
                "Colombiano", "Colombia");
        crear(TipoDocIdentidad.PA, "AB123456", "María Gómez", "34600123456", "maria@correo.com",
                "Española", "España");
        crear(TipoDocIdentidad.PA, "X9876543", "John Smith", "12025550123", "john@correo.com",
                "Estadounidense", "Estados Unidos");
    }

    private void crear(TipoDocIdentidad tipo, String documento, String nombre, String telefono, String correo,
                       String nacionalidad, String paisResidencia) {
        clienteService.crear(new ClienteRequest(tipo, documento, nombre, telefono, correo,
                PASSWORD_DEMOSTRACION, nacionalidad, paisResidencia));
    }
}
