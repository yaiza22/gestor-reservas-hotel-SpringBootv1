package com.gestor_reservas_hotel.gestor_reservas_hotel.controller;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ReservaRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ReservaResponse;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Reserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.enums.EstadoReserva;
import com.gestor_reservas_hotel.gestor_reservas_hotel.services.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService service;

    @GetMapping
    public List<ReservaResponse> listar(@RequestParam(required = false) EstadoReserva estado, @RequestParam(required = false) Long clienteId) {
        return service.listar(estado, clienteId).stream().map(ReservaResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public ReservaResponse obtener(@PathVariable Long id) {
        return ReservaResponse.desde(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        Reserva creada = service.crear(request);
        URI ubicacion = URI.create("/api/reservas/" + creada.getId());
        return ResponseEntity.created(ubicacion).body(ReservaResponse.desde(creada));
    }

    @PutMapping("/{id}")
    public ReservaResponse actualizar(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        return ReservaResponse.desde(service.actualizar(id, request));
    }

    @PostMapping("/{id}/confirmar")
    public ReservaResponse confirmar(@PathVariable Long id) {
        return ReservaResponse.desde(service.confirmar(id));
    }

    @PostMapping("/{id}/check-in")
    public ReservaResponse checkIn(@PathVariable Long id) {
        return ReservaResponse.desde(service.checkIn(id));
    }

    @PostMapping("/{id}/check-out")
    public ReservaResponse checkOut(@PathVariable Long id) {
        return ReservaResponse.desde(service.checkOut(id));
    }

    @PostMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id) {
        return ReservaResponse.desde(service.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
