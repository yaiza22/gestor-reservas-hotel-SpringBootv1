package com.gestor_reservas_hotel.habitaciones.controller;

import com.gestor_reservas_hotel.habitaciones.dto.*;
import com.gestor_reservas_hotel.habitaciones.model.Habitacion;
import com.gestor_reservas_hotel.habitaciones.model.enums.*;
import com.gestor_reservas_hotel.habitaciones.services.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Endpoints Habitaciones REST
 */
@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {
    private final HabitacionService service;

    @GetMapping
    public List<HabitacionResponse> listar(@RequestParam(required = false) EstadoHabitacion estado,
                                           @RequestParam(required = false) TipoHabitacion tipoHabitacion) {
        return service.listar(estado, tipoHabitacion).stream().map(HabitacionResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public HabitacionResponse obtener(@PathVariable Long id) {
        return HabitacionResponse.desde(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<HabitacionResponse> crear(@Valid @RequestBody HabitacionRequest request) {
        Habitacion creada = service.crear(request);
        URI ubicacion = URI.create("/api/habitaciones/" + creada.getId());
        return ResponseEntity.created(ubicacion).body(HabitacionResponse.desde(creada));
    }

    @PutMapping("/{id}")
    public HabitacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody HabitacionUpdateRequest request) {
        return HabitacionResponse.desde(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public HabitacionResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return HabitacionResponse.desde(service.cambiarEstado(id, request.estado()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/{id}/precio")
    public PrecioResponse precio(@PathVariable Long id, @RequestParam Temporada temporada) {
        return PrecioResponse.desde(service.obtenerPorId(id), temporada);
    }
}
