package com.cibertec.citas.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cibertec.citas.model.Cita;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CitaRepository
        extends ReactiveCrudRepository<Cita, Long> {

    // Buscar citas de un paciente
    Flux<Cita> findByPacienteId(Long pacienteId);

    // Verificar si un horario ya esta reservado
    Mono<Boolean> existsByProfesionalCodigoAndFechaHoraAndEstadoIn(
            String profesionalCodigo,
            LocalDateTime fechaHora,
            Collection<String> estados
    );
}
