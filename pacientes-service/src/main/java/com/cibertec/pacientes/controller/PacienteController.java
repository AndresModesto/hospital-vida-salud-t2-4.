package com.cibertec.pacientes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cibertec.pacientes.model.Paciente;
import com.cibertec.pacientes.service.PacienteService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    // Listar todos los pacientes
    @GetMapping
    public Flux<Paciente> listar() {
        return service.listar();
    }

    // Buscar paciente por ID
    @GetMapping("/{id}")
    public Mono<Paciente> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // Registrar un nuevo paciente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Paciente> registrar(
            @RequestBody Mono<Paciente> paciente) {
        return paciente.flatMap(service::registrar);
    }
}