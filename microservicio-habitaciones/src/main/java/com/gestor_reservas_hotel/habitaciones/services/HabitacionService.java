package com.gestor_reservas_hotel.habitaciones.services;

import com.gestor_reservas_hotel.habitaciones.dto.HabitacionRequest;
import com.gestor_reservas_hotel.habitaciones.dto.HabitacionUpdateRequest;
import com.gestor_reservas_hotel.habitaciones.exception.ConflictoNegocioException;
import com.gestor_reservas_hotel.habitaciones.exception.RecursoNoEncontradoException;
import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;
import com.gestor_reservas_hotel.habitaciones.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitacionService {
    public static final int ESCALA_DINERO = 2;

    private final HabitacionRepository repository;

    private BigDecimal normalizarPrecio(BigDecimal precio) {
        return precio.setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
    }

    /**
     * Lista las habitaciones, con filtros opcionales.
     * @param estado si no es null, solo las que tienen ese estado
     * @param tipoHabitacion si no es null, solo las de ese tipo
     * @return habitaciones ordenadas por id
     */
    public List<Habitacion> listar(EstadoHabitacion estado, TipoHabitacion tipoHabitacion) {
        return repository.findAll().stream()
                .filter(h -> estado == null || h.getEstado() == estado)
                .filter(h -> tipoHabitacion == null || h.getTipoHabitacion() == tipoHabitacion)
                .toList();
    }

    /**
     * Busca una habitación por su id.
     * @throws RecursoNoEncontradoException si no existe
     */
    public Habitacion obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una habitación con ese ID."));
    }

    /**
     * Crea una habitación (estado DISPONIBLE si no se indica otro).
     * @throws ConflictoNegocioException si ya existe una habitación con ese número (RN-11)
     */
    public Habitacion crear(HabitacionRequest request) {
        if (repository.existsByNumero(request.numero())) {
            throw new ConflictoNegocioException("Ya existe una habitación con el número " + request.numero() + ".");
        }
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(request.numero());
        habitacion.setTipoHabitacion(request.tipoHabitacion());
        habitacion.setPiso(request.piso());
        habitacion.setPrecioBase(normalizarPrecio(request.precioBase()));
        habitacion.setCapacidad(request.capacidad());
        habitacion.setEstado(request.estado() != null ? request.estado() : EstadoHabitacion.DISPONIBLE);
        Habitacion guardada = repository.save(habitacion);
        log.info("Habitación creada id={} numero={}", guardada.getId(), guardada.getNumero());
        return guardada;
    }

    /**
     * Actualiza una habitación; el número no cambia y, si no llega estado, se conserva el actual.
     * @throws RecursoNoEncontradoException si no existe
     */
    public Habitacion actualizar(Long id, HabitacionUpdateRequest request) {
        Habitacion habitacion = obtenerPorId(id);
        habitacion.setTipoHabitacion(request.tipoHabitacion());
        habitacion.setPiso(request.piso());
        habitacion.setPrecioBase(normalizarPrecio(request.precioBase()));
        habitacion.setCapacidad(request.capacidad());
        if (request.estado() != null) {
            habitacion.setEstado(request.estado());
        }
        Habitacion guardada = repository.save(habitacion);
        log.info("Habitación actualizada id={}", guardada.getId());
        return guardada;
    }

    /**
     * Cambia solo el estado de una habitación.
     * @throws RecursoNoEncontradoException si no existe
     */
    public Habitacion cambiarEstado(Long id, EstadoHabitacion nuevoEstado) {
        Habitacion habitacion = obtenerPorId(id);
        EstadoHabitacion anterior = habitacion.getEstado();
        habitacion.setEstado(nuevoEstado);
        Habitacion guardada = repository.save(habitacion);
        log.info("Habitación id={} cambió de estado {} -> {}", id, anterior, nuevoEstado);
        return guardada;
    }

    /**
     * Elimina una habitación.
     * @throws RecursoNoEncontradoException si no existe
     * @throws ConflictoNegocioException si está OCUPADA (RN-12)
     */
    public void eliminar(Long id) {
        Habitacion habitacion = obtenerPorId(id);
        if (habitacion.getEstado() == EstadoHabitacion.OCUPADA) {
            throw new ConflictoNegocioException("No se puede eliminar una habitación ocupada.");
        }
        repository.deleteById(id);
        log.info("Habitación eliminada id={}", id);
    }
}
