
package com.cibertec.citas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cibertec.citas.model.Cita;
import com.cibertec.citas.service.CitaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    // Listar todas las citas
    @GetMapping
    public Flux<Cita> listar() {
        return service.listar();
    }

    // Buscar una cita por ID
    @GetMapping("/{id}")
    public Mono<Cita> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // Registrar una nueva cita
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cita> registrar(@RequestBody Cita cita) {
        return service.registrar(cita);
    }

    // Confirmar una cita
    @PutMapping("/{id}/confirmar")
    public Mono<Cita> confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }
}
