package com.gestor_reservas_hotel.gestor_reservas_hotel.controller;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ErrorResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.UsuarioRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.UsuarioResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.UsuarioUpdateRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.services.UsuarioService;
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
 * Endpoints REST de usuarios (API.md §4.2).
 */
@Tag(name = "Usuarios", description = "Creación, consulta, edición y eliminación de usuarios (empleados del hotel)")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @Operation(summary = "Listar usuarios", description = "Devuelve los usuarios ordenados por id, sin contraseña.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios (vacía si no hay ninguno)")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar().stream().map(UsuarioResponse::desde).toList();
    }

    @Operation(summary = "Consultar un usuario", description = "Devuelve el usuario con ese id, sin contraseña.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public UsuarioResponse obtener(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        return UsuarioResponse.desde(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un usuario",
            description = "El documento (tipo + número) no puede repetirse entre los usuarios. la contraseña se recibe, pero nunca se devuelve.")
    @ApiResponse(responseCode = "201", description = "Usuario creado; la cabecera Location apunta a él")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe un usuario con ese documento",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        Usuario creado = service.crear(request);
        URI ubicacion = URI.create("/api/usuarios/" + creado.getId());
        return ResponseEntity.created(ubicacion).body(UsuarioResponse.desde(creado));
    }

    @Operation(summary = "Actualizar un usuario",
            description = "Cambia nombre, teléfono, correo y rol. El documento y la contraseña no se editan.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id,
                                      @Valid @RequestBody UsuarioUpdateRequest request) {
        return UsuarioResponse.desde(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina el usuario con ese id.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado (sin cuerpo)")
    @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        service.eliminar(id);
    }
}