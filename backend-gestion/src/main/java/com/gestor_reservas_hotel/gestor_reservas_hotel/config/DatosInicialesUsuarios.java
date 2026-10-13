package com.gestor_reservas_hotel.gestor_reservas_hotel.config;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.UsuarioRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.Rol;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.TipoDocIdentidad;
import com.gestor_reservas_hotel.gestor_reservas_hotel.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Carga los 2 usuarios de demostración al arrancar si {@code app.datos-iniciales=true} (solo para desarrollo).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.datos-iniciales", havingValue = "true")
public class DatosInicialesUsuarios implements ApplicationRunner {

    private final UsuarioService usuarioService;

    @Override
    public void run(ApplicationArguments args) {
        usuarioService.crear(new UsuarioRequest(TipoDocIdentidad.CC, "1090000001", "Administrador del Hotel",
                "3000000001", "admin@hotel.com", "Admin1234", Rol.ADMINISTRADOR));
        usuarioService.crear(new UsuarioRequest(TipoDocIdentidad.CC, "1090000002", "Asistente del Hotel",
                "3000000002", "asistente@hotel.com", "Asist1234", Rol.ASISTENTE));
    }
}
