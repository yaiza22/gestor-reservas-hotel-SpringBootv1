package com.gestor_reservas_hotel.habitaciones.config;

import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;
import com.gestor_reservas_hotel.habitaciones.repository.HabitacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Se cargan habitaciones de prueba, si app.datos-iniciales=true).
 */
@Component
@ConditionalOnProperty(name = "app.datos-iniciales", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DatosIniciales implements ApplicationRunner {
    private final HabitacionRepository repository;

    private void agregar(String numero, TipoHabitacion tipo, int piso, String precioBase, int capacidad,
                         EstadoHabitacion estado) {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(numero);
        habitacion.setTipoHabitacion(tipo);
        habitacion.setPiso(piso);
        habitacion.setPrecioBase(new BigDecimal(precioBase));
        habitacion.setCapacidad(capacidad);
        habitacion.setEstado(estado);
        repository.save(habitacion);
    }

    @Override
    public void run(ApplicationArguments args) {
        agregar("101", TipoHabitacion.INDIVIDUAL, 1, "100000.00", 1, EstadoHabitacion.DISPONIBLE);
        agregar("102", TipoHabitacion.INDIVIDUAL, 1, "100000.00", 1, EstadoHabitacion.DISPONIBLE);
        agregar("103", TipoHabitacion.DOBLE, 1, "150000.00", 2, EstadoHabitacion.DISPONIBLE);
        agregar("104", TipoHabitacion.TWIN, 1, "150000.00", 2, EstadoHabitacion.DISPONIBLE);
        agregar("201", TipoHabitacion.DOBLE, 2, "160000.00", 2, EstadoHabitacion.DISPONIBLE);
        agregar("202", TipoHabitacion.TRIPLE, 2, "180000.00", 3, EstadoHabitacion.DISPONIBLE);
        agregar("203", TipoHabitacion.TRIPLE, 2, "180000.00", 3, EstadoHabitacion.MANTENIMIENTO);
        agregar("301", TipoHabitacion.FAMILIAR, 3, "200000.00", 4, EstadoHabitacion.DISPONIBLE);
        agregar("302", TipoHabitacion.FAMILIAR, 3, "200000.00", 5, EstadoHabitacion.LIMPIEZA);
        agregar("303", TipoHabitacion.DOBLE, 3, "160000.00", 2, EstadoHabitacion.DISPONIBLE);
        log.info("Datos de demostración cargados: {} habitaciones", repository.findAll().size());
    }
}
