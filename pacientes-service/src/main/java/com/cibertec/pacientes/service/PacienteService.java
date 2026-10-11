
package com.cibertec.pacientes.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.pacientes.model.Paciente;
import com.cibertec.pacientes.repository.PacienteRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    // Listar pacientes
    public Flux<Paciente> listar() {
        return repository.findAll();
    }

    // Buscar paciente por ID
    public Mono<Paciente> buscarPorId(Long id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente no encontrado"
                )
            ));
    }

    // Registrar paciente
    public Mono<Paciente> registrar(Paciente paciente) {

        if (paciente.getId() != null) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No debe enviar un ID al registrar"
            ));
        }

        if (vacio(paciente.getTipoDocumento())
            || vacio(paciente.getNumeroDocumento())
            || vacio(paciente.getNombres())
            || vacio(paciente.getApellidos())
            || paciente.getFechaNacimiento() == null) {

            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Complete los datos obligatorios"
            ));
        }

        return repository
            .findByTipoDocumentoAndNumeroDocumento(
                paciente.getTipoDocumento(),
                paciente.getNumeroDocumento()
            )
            .hasElement()
            .flatMap(existe -> {

                if (existe) {
                    return Mono.error(
                        new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "El paciente ya esta registrado"
                        )
                    );
                }

                paciente.setEstado("ACTIVO");
                paciente.setFechaRegistro(LocalDateTime.now());

                return repository.save(paciente);
            });
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
