package com.gestor_reservas_hotel.gestor_reservas_hotel.services;

import com.gestor_reservas_hotel.gestor_reservas_hotel.client.HabitacionesClient;
import com.gestor_reservas_hotel.gestor_reservas_hotel.client.dto.EstadoHabitacionRemoto;
import com.gestor_reservas_hotel.gestor_reservas_hotel.client.dto.HabitacionRemota;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ReservaRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.*;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {
    private static final int ESCALA_DINERO = 2;
    private final ReservaRepository repository;
    private final ClienteService clienteService;
    private final HabitacionesClient habitacionesClient;

    private record ConsultaHabitaciones(List<HabitacionRemota> habitaciones, BigDecimal precioPorNoche) { }

    // Listar reservas,
    public List<Reserva> listar(EstadoReserva estado, Long clienteId) {
        return repository.findAll().stream()
                .filter(r -> estado == null || r.getEstado() == estado)
                .filter(r -> clienteId == null || r.getClienteId().equals(clienteId))
                .toList();
    }

    public Reserva obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una reserva con ese ID."));
    }

    // Se válida que mínimo haya una noche de reserva.
    private void validarFechas(ReservaRequest request) {
        if (!request.fechaFin().isAfter(request.fechaInicio())) {
            throw new ValidacionException("fechaFin", "La fecha de fin debe ser posterior a la fecha de inicio.");
        }
    }

    // No se deben repetir habitaciones en la misma reserva.
    private void validarHabitacionesSinRepetir(List<Long> habitacionesId) {
        if (new HashSet<>(habitacionesId).size() != habitacionesId.size()) {
            throw new ValidacionException("habitacionesId", "Las habitaciones no pueden repetirse (máximo 10).");
        }
    }

    // El cliente ya debe estar registrado
    private void validarClienteExiste(Long clienteId) {
        if (!clienteService.existePorId(clienteId)) {
            throw new ReferenciaInvalidaException("clienteId", "No existe un cliente con el ID: " + clienteId + ".");
        }
    }

    // Consulta cada habitación al microservicio, verifica que el estado no sea MANTENIMIENTO y suma los precios por noche
    private ConsultaHabitaciones consultarHabitaciones(ReservaRequest request) {
        List<HabitacionRemota> habitaciones = new ArrayList<>();
        BigDecimal precioPorNoche = BigDecimal.ZERO;
        for (Long habitacionId : request.habitacionesId()) {
            HabitacionRemota habitacion = habitacionesClient.obtenerPorId(habitacionId);
            // RN-06: no se reserva una habitación en mantenimiento
            if (habitacion.estado() == EstadoHabitacionRemoto.MANTENIMIENTO) {
                throw new ConflictoNegocioException("La habitación " + habitacion.numero() + " está en mantenimiento.");
            }
            BigDecimal precio = habitacionesClient
                    .obtenerPrecio(habitacionId, request.temporada().name())
                    .precioPorNoche();
            precioPorNoche = precioPorNoche.add(precio);
            habitaciones.add(habitacion);
        }
        return new ConsultaHabitaciones(habitaciones, precioPorNoche);
    }

    private void validarCapacidad(List<HabitacionRemota> habitaciones, int cantHuespedes) {
        // Compara la capacidad total de las habitaciones con la cantidad de huéspedes
        int capacidadTotal = habitaciones.stream().mapToInt(HabitacionRemota::capacidad).sum();
        if (capacidadTotal < cantHuespedes) {
            throw new ConflictoNegocioException("La capacidad de las habitaciones (" + capacidadTotal + ") no alcanza para " + cantHuespedes + " huéspedes.");
        }
    }

    private void validarSinCruceDeFechas(ReservaRequest request, Long reservaIdExcluida) {
        for (Long habitacionId : request.habitacionesId()) {
            if (repository.existsCruceDeFechas(habitacionId, request.fechaInicio(), request.fechaFin(), reservaIdExcluida)) {
                throw new ConflictoNegocioException("La habitación no está disponible en esas fechas.");
            }
        }
    }

    private BigDecimal validarYObtenerPrecioPorNoche(ReservaRequest request, Long reservaIdExcluida) {
        validarFechas(request);
        validarHabitacionesSinRepetir(request.habitacionesId());
        validarClienteExiste(request.clienteId());
        ConsultaHabitaciones consulta = consultarHabitaciones(request);
        validarCapacidad(consulta.habitaciones(), request.cantHuespedes());
        validarSinCruceDeFechas(request, reservaIdExcluida);
        return consulta.precioPorNoche();
    }

    private void copiarDatos(Reserva reserva, ReservaRequest request, BigDecimal precioPorNoche) {
        reserva.setClienteId(request.clienteId());
        reserva.setHabitacionesId(List.copyOf(request.habitacionesId()));
        reserva.setFechaInicio(request.fechaInicio());
        reserva.setFechaFin(request.fechaFin());
        reserva.setCantHuespedes(request.cantHuespedes());
        reserva.setTemporada(request.temporada());
        reserva.setPrecioTotal(precioPorNoche.multiply(BigDecimal.valueOf(reserva.calcularNoches())).setScale(ESCALA_DINERO, RoundingMode.HALF_UP));
    }
    /**
     * Crea una reserva en estado PENDIENTE y calcula su precio total.
     * @throws ValidacionException si la fecha de fin no es posterior a la de inicio o hay habitaciones repetidas
     * @throws ReferenciaInvalidaException si el cliente o alguna habitación no existen
     * @throws ConflictoNegocioException por habitación en mantenimiento, capacidad insuficiente o fechas cruzadas
     * @throws ServicioNoDisponibleException si el microservicio de habitaciones no responde
     */
    public Reserva crear(ReservaRequest request) {
        BigDecimal precioPorNoche = validarYObtenerPrecioPorNoche(request, null);
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE);
        copiarDatos(reserva, request, precioPorNoche);
        Reserva guardada = repository.save(reserva);
        log.info("Reserva creada id={} clienteId={} total={}", guardada.getId(), guardada.getClienteId(), guardada.getPrecioTotal());
        return guardada;
    }

    /**
     * Solo se puede editar en estado PENDIENTE o CONFIRMADA y recalcula el precio
     * @throws RecursoNoEncontradoException si no existe la reserva
     * @throws ConflictoNegocioException si la reserva no se puede editar en su estado actual, o por las mismas reglas de crear (mantenimiento, capacidad, fechas cruzadas)
     * @throws ValidacionException si las fechas son inválidas o hay habitaciones repetidas
     * @throws ReferenciaInvalidaException si el cliente o alguna habitación no existen
     * @throws ServicioNoDisponibleException si el microservicio de habitaciones no responde
     */
    public Reserva actualizar(Long id, ReservaRequest request) {
        Reserva reserva = obtenerPorId(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ConflictoNegocioException("Solo se puede editar una reserva pendiente o confirmada " + "(estado actual: " + reserva.getEstado() + ").");
        }
        BigDecimal precioPorNoche = validarYObtenerPrecioPorNoche(request, id);
        copiarDatos(reserva, request, precioPorNoche);
        Reserva guardada = repository.save(reserva);
        log.info("Reserva actualizada id={} total={}", id, guardada.getPrecioTotal());
        return guardada;
    }

    private void exigirEstado(Reserva reserva, EstadoReserva... permitidos) {
        if (!List.of(permitidos).contains(reserva.getEstado())) {
            throw new ConflictoNegocioException(
                    "Esa transición no es válida desde el estado actual (" + reserva.getEstado() + ").");
        }
    }

    // Confirma una reserva: PENDIENTE pasa a CONFIRMADA.
    public Reserva confirmar(Long id) {
        Reserva reserva = obtenerPorId(id);
        exigirEstado(reserva, EstadoReserva.PENDIENTE);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        log.info("Reserva confirmada id={}", id);
        return repository.save(reserva);
    }

    private void revertirEstado(List<Long> habitacionesId, EstadoHabitacionRemoto estado) {
        for (Long habitacionId : habitacionesId) {
            try {
                habitacionesClient.cambiarEstado(habitacionId, estado);
            } catch (ServicioNoDisponibleException | ReferenciaInvalidaException ex) {
                log.warn("No se pudo devolver la habitación id={} a {}: {}", habitacionId, estado, ex.getMessage());
            }
        }
    }

    // Cambia el estado de todas las habitaciones; si una falla, intenta devolver las ya cambiadas.
    private void cambiarEstadoDeHabitaciones(List<Long> habitacionesId, EstadoHabitacionRemoto nuevoEstado,
                                             EstadoHabitacionRemoto estadoDeCompensacion) {
        List<Long> yaCambiadas = new ArrayList<>();
        try {
            for (Long habitacionId : habitacionesId) {
                habitacionesClient.cambiarEstado(habitacionId, nuevoEstado);
                yaCambiadas.add(habitacionId);
            }
        } catch (RuntimeException ex) {
            revertirEstado(yaCambiadas, estadoDeCompensacion);
            throw ex;
        }
    }

    // Hace el check-in: CONFIRMADA pasa a ENCURSO y las habitaciones a OCUPADA
    public Reserva checkIn(Long id) {
        Reserva reserva = obtenerPorId(id);
        exigirEstado(reserva, EstadoReserva.CONFIRMADA);
        // Todas las habitaciones deben estar DISPONIBLE antes de ocuparlas
        for (Long habitacionId : reserva.getHabitacionesId()) {
            HabitacionRemota habitacion = habitacionesClient.obtenerPorId(habitacionId);
            if (habitacion.estado() != EstadoHabitacionRemoto.DISPONIBLE) {
                throw new ConflictoNegocioException("La habitación " + habitacion.numero() + " no está disponible para el check-in (estado actual: " + habitacion.estado() + ").");
            }
        }
        cambiarEstadoDeHabitaciones(reserva.getHabitacionesId(), EstadoHabitacionRemoto.OCUPADA,
                EstadoHabitacionRemoto.DISPONIBLE);
        reserva.setEstado(EstadoReserva.ENCURSO);
        reserva.setHoraEntrada(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        log.info("Check-in realizado id={}", id);
        return repository.save(reserva);
    }



    // Hace el check-out: ENCURSO pasa a FINALIZADA y las habitaciones a LIMPIEZA
    public Reserva checkOut(Long id) {
        Reserva reserva = obtenerPorId(id);
        exigirEstado(reserva, EstadoReserva.ENCURSO);
        cambiarEstadoDeHabitaciones(reserva.getHabitacionesId(), EstadoHabitacionRemoto.LIMPIEZA, EstadoHabitacionRemoto.OCUPADA);
        reserva.setEstado(EstadoReserva.FINALIZADA);
        reserva.setHoraSalida(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        log.info("Check-out realizado id={}", id);
        return repository.save(reserva);
    }

    // Cancela una reserva PENDIENTE o CONFIRMADA; no cambia el estado de las habitaciones.
    public Reserva cancelar(Long id) {
        Reserva reserva = obtenerPorId(id);
        exigirEstado(reserva, EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);
        reserva.setEstado(EstadoReserva.CANCELADA);
        log.info("Reserva cancelada id={}", id);
        return repository.save(reserva);
    }

    public void eliminar(Long id) {
        Reserva reserva = obtenerPorId(id);
        if (reserva.getEstado() == EstadoReserva.ENCURSO) {
            throw new ConflictoNegocioException("No se puede eliminar una reserva en curso.");
        }
        repository.deleteById(id);
        log.info("Reserva eliminada id={}", id);
    }
}
