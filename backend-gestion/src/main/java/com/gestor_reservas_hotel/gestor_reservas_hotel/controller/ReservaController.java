package com.gestor_reservas_hotel.gestor_reservas_hotel.controller;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ErrorResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ReservaRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ReservaResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de reservas
 */
@Tag(name = "Reservas",
        description = "Reservas de habitaciones: alta, consulta, edición, cambios de estado y baja. "
                + "Consulta al microservicio de habitaciones")
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService service;

    @Operation(summary = "Listar reservas",
            description = "Devuelve las reservas ordenadas por id. Filtros opcionales por estado y por cliente.")
    @ApiResponse(responseCode = "200", description = "Lista de reservas (vacía si no hay ninguna)")
    @ApiResponse(responseCode = "400", description = "Valor de filtro no válido",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public List<ReservaResponse> listar(
            @Parameter(description = "Solo las reservas con este estado")
            @RequestParam(required = false) EstadoReserva estado,
            @Parameter(description = "Solo las reservas de este cliente")
            @RequestParam(required = false) Long clienteId) {
        return service.listar(estado, clienteId).stream().map(ReservaResponse::desde).toList();
    }

    @Operation(summary = "Consultar una reserva", description = "Devuelve la reserva con ese id.")
    @ApiResponse(responseCode = "200", description = "Reserva encontrada")
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ReservaResponse obtener(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id) {
        return ReservaResponse.desde(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear una reserva",
            description = "Crea la reserva en estado PENDIENTE y calcula precioTotal. Valida que fechaFin sea "
                    + "posterior a fechaInicio, que el cliente exista, que cada habitación exista y no esté en "
                    + "mantenimiento (consulta al microservicio), la capacidad y el cruce de fechas.")
    @ApiResponse(responseCode = "201", description = "Reserva creada; la cabecera Location apunta a ella")
    @ApiResponse(responseCode = "400", description = "Datos inválidos, o cliente o habitación inexistentes",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Habitación en mantenimiento, capacidad insuficiente o fechas cruzadas",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "El microservicio de habitaciones no responde",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        Reserva creada = service.crear(request);
        URI ubicacion = URI.create("/api/reservas/" + creada.getId());
        return ResponseEntity.created(ubicacion).body(ReservaResponse.desde(creada));
    }

    @Operation(summary = "Editar una reserva",
            description = "Solo una reserva PENDIENTE o CONFIRMADA. Repite las validaciones de crear y recalcula el precio.")
    @ApiResponse(responseCode = "200", description = "Reserva actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos, o cliente o habitación inexistentes",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409",
            description = "La reserva no se puede editar en su estado, o mantenimiento, capacidad o fechas cruzadas",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "El microservicio de habitaciones no responde",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ReservaResponse actualizar(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id,
                                      @Valid @RequestBody ReservaRequest request) {
        return ReservaResponse.desde(service.actualizar(id, request));
    }

    @Operation(summary = "Confirmar una reserva",
            description = "PENDIENTE pasa a CONFIRMADA. No cambia el estado de las habitaciones.")
    @ApiResponse(responseCode = "200", description = "Reserva confirmada")
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Transición no válida desde el estado actual",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/confirmar")
    public ReservaResponse confirmar(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id) {
        return ReservaResponse.desde(service.confirmar(id));
    }

    @Operation(summary = "Hacer el check-in",
            description = "CONFIRMADA pasa a ENCURSO. Todas las habitaciones deben estar DISPONIBLE; el microservicio las pasa a OCUPADA y se guarda horaEntrada.")
    @ApiResponse(responseCode = "200", description = "Check-in realizado")
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Transición no válida o alguna habitación no está DISPONIBLE",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "El microservicio de habitaciones no responde",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/check-in")
    public ReservaResponse checkIn(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id) {
        return ReservaResponse.desde(service.checkIn(id));
    }

    @Operation(summary = "Hacer el check-out",
            description = "ENCURSO pasa a FINALIZADA; el microservicio pasa las habitaciones a LIMPIEZA "
                    + "y se guarda horaSalida.")
    @ApiResponse(responseCode = "200", description = "Check-out realizado")
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Transición no válida desde el estado actual",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "El microservicio de habitaciones no responde",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/check-out")
    public ReservaResponse checkOut(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id) {
        return ReservaResponse.desde(service.checkOut(id));
    }

    @Operation(summary = "Cancelar una reserva",
            description = "PENDIENTE o CONFIRMADA pasa a CANCELADA. No cambia el estado de las habitaciones.")
    @ApiResponse(responseCode = "200", description = "Reserva cancelada")
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Transición no válida desde el estado actual",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id) {
        return ReservaResponse.desde(service.cancelar(id));
    }

    @Operation(summary = "Eliminar una reserva", description = "No se puede eliminar una reserva ENCURSO.")
    @ApiResponse(responseCode = "204", description = "Reserva eliminada (sin cuerpo)")
    @ApiResponse(responseCode = "404", description = "No existe una reserva con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "La reserva está en curso",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@Parameter(description = "Id de la reserva", example = "1") @PathVariable Long id) {
        service.eliminar(id);
    }
}