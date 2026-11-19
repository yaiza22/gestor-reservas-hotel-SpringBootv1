package com.gestor_reservas_hotel.habitaciones.controller;

import com.gestor_reservas_hotel.habitaciones.dto.CambioEstadoRequest;
import com.gestor_reservas_hotel.habitaciones.dto.ErrorResponse;
import com.gestor_reservas_hotel.habitaciones.dto.HabitacionRequest;
import com.gestor_reservas_hotel.habitaciones.dto.HabitacionResponse;
import com.gestor_reservas_hotel.habitaciones.dto.HabitacionUpdateRequest;
import com.gestor_reservas_hotel.habitaciones.dto.PrecioResponse;
import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.EstadoHabitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.Temporada;
import com.gestor_reservas_hotel.habitaciones.model.enums.TipoHabitacion;
import com.gestor_reservas_hotel.habitaciones.services.HabitacionService;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
 * Endpoints REST de habitaciones
 */
@Tag(name = "Habitaciones",
        description = "Alta, consulta, edición, cambio de estado y baja de habitaciones, y precio por noche")
@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {
    private final HabitacionService service;

    @Operation(summary = "Listar habitaciones",
            description = "Devuelve las habitaciones ordenadas por id. Filtros opcionales por estado y por tipo.")
    @ApiResponse(responseCode = "200", description = "Lista de habitaciones (vacía si no hay ninguna)")
    @ApiResponse(responseCode = "400", description = "Valor de filtro no válido",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public List<HabitacionResponse> listar(
            @Parameter(description = "Solo las habitaciones con este estado")
            @RequestParam(required = false) EstadoHabitacion estado,
            @Parameter(description = "Solo las habitaciones de este tipo")
            @RequestParam(required = false) TipoHabitacion tipoHabitacion) {
        return service.listar(estado, tipoHabitacion).stream().map(HabitacionResponse::desde).toList();
    }

    @Operation(summary = "Consultar una habitación", description = "Devuelve la habitación con ese id.")
    @ApiResponse(responseCode = "200", description = "Habitación encontrada")
    @ApiResponse(responseCode = "404", description = "No existe una habitación con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public HabitacionResponse obtener(
            @Parameter(description = "Id de la habitación", example = "3") @PathVariable Long id) {
        return HabitacionResponse.desde(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear una habitación",
            description = "El número no puede repetirse. Si no se envía estado, queda DISPONIBLE.")
    @ApiResponse(responseCode = "201", description = "Habitación creada; la cabecera Location apunta a ella")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe una habitación con ese número",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<HabitacionResponse> crear(@Valid @RequestBody HabitacionRequest request) {
        Habitacion creada = service.crear(request);
        URI ubicacion = URI.create("/api/habitaciones/" + creada.getId());
        return ResponseEntity.created(ubicacion).body(HabitacionResponse.desde(creada));
    }

    @Operation(summary = "Actualizar una habitación",
            description = "Cambia tipo, piso, precio base, capacidad y estado. El número no se edita, si no se envía estado, se conserva el actual.")
    @ApiResponse(responseCode = "200", description = "Habitación actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe una habitación con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public HabitacionResponse actualizar(
            @Parameter(description = "Id de la habitación", example = "3") @PathVariable Long id,
            @Valid @RequestBody HabitacionUpdateRequest request) {
        return HabitacionResponse.desde(service.actualizar(id, request));
    }

    @Operation(summary = "Cambiar el estado de una habitación",
            description = "Cambia solo el estado. Gestión lo usa en el check-in (OCUPADA) y en el check-out (LIMPIEZA).")
    @ApiResponse(responseCode = "200", description = "Estado cambiado")
    @ApiResponse(responseCode = "400", description = "Estado ausente o no válido",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe una habitación con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{id}/estado")
    public HabitacionResponse cambiarEstado(
            @Parameter(description = "Id de la habitación", example = "3") @PathVariable Long id,
            @Valid @RequestBody CambioEstadoRequest request) {
        return HabitacionResponse.desde(service.cambiarEstado(id, request.estado()));
    }

    @Operation(summary = "Eliminar una habitación",
            description = "No se puede eliminar una habitación OCUPADA. No revisa reservas existentes.")
    @ApiResponse(responseCode = "204", description = "Habitación eliminada (sin cuerpo)")
    @ApiResponse(responseCode = "404", description = "No existe una habitación con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "La habitación está ocupada",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "Id de la habitación", example = "3") @PathVariable Long id) {
        service.eliminar(id);
    }

    @Operation(summary = "Calcular el precio por noche",
            description = "precioBase × multiplicador del tipo × multiplicador de la temporada, redondeado a 2 decimales. Gestión lo usa para calcular el total de una reserva.")
    @ApiResponse(responseCode = "200", description = "Precio por noche calculado")
    @ApiResponse(responseCode = "400", description = "Temporada ausente o no válida",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe una habitación con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/precio")
    public PrecioResponse precio(
            @Parameter(description = "Id de la habitación", example = "3") @PathVariable Long id,
            @Parameter(description = "Temporada para el cálculo", example = "ALTA") @RequestParam Temporada temporada) {
        return PrecioResponse.desde(service.obtenerPorId(id), temporada);
    }
}