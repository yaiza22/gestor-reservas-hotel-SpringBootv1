package com.gestor_reservas_hotel.gestor_reservas_hotel.controller;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ClienteRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ClienteResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ClienteUpdateRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ErrorResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST de clientes
 */
@Tag(name = "Clientes", description = "Creación, consulta, edición y eliminación de clientes")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @Operation(summary = "Listar clientes", description = "Devuelve los clientes ordenados por id, sin contraseña.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes (vacía si no hay ninguno)")
    @GetMapping
    public List<ClienteResponse> listar() {
        return service.listar().stream().map(ClienteResponse::desde).toList();
    }

    @Operation(summary = "Consultar un cliente", description = "Devuelve el cliente con ese id, sin contraseña.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "No existe un cliente con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ClienteResponse obtener(@Parameter(description = "Id del cliente", example = "1") @PathVariable Long id) {
        return ClienteResponse.desde(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un cliente",
            description = "El documento (tipo + número) no puede repetirse entre los clientes. La contraseña se recibe, pero nunca se devuelve.")
    @ApiResponse(responseCode = "201", description = "Cliente creado; la cabecera Location apunta a él")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe un cliente con ese documento",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        Cliente creado = service.crear(request);
        URI ubicacion = URI.create("/api/clientes/" + creado.getId());
        return ResponseEntity.created(ubicacion).body(ClienteResponse.desde(creado));
    }

    @Operation(summary = "Actualizar un cliente",
            description = "Cambia nombre, teléfono, correo, nacionalidad y país de residencia. El documento y la contraseña no se editan.")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe un cliente con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ClienteResponse actualizar(@Parameter(description = "Id del cliente", example = "1") @PathVariable Long id,
                                      @Valid @RequestBody ClienteUpdateRequest request) {
        return ClienteResponse.desde(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un cliente",
            description = "No revisa si el cliente tiene reservas (limitación conocida).")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado (sin cuerpo)")
    @ApiResponse(responseCode = "404", description = "No existe un cliente con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@Parameter(description = "Id del cliente", example = "1") @PathVariable Long id) {
        service.eliminar(id);
    }
}