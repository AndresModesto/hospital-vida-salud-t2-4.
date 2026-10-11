
package com.cibertec.citas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.citas.model.Cita;
import com.cibertec.citas.repository.CitaRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CitaService {

    private final CitaRepository repository;
    private final WebClient pacientesClient;
    private final RabbitTemplate rabbitTemplate;

    public CitaService(
            CitaRepository repository,
            WebClient.Builder builder,
            RabbitTemplate rabbitTemplate) {

        this.repository = repository;
        this.pacientesClient = builder
                .baseUrl("http://localhost:8081")
                .build();
        this.rabbitTemplate = rabbitTemplate;
    }

    public Flux<Cita> listar() {
        return repository.findAll();
    }

    public Mono<Cita> buscarPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada"
                    )
                ));
    }

    public Mono<Cita> registrar(Cita cita) {

        if (cita.getId() != null ||
            cita.getPacienteId() == null ||
            vacio(cita.getEspecialidad()) ||
            vacio(cita.getProfesionalCodigo()) ||
            cita.getFechaHora() == null) {

            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Datos de cita invalidos"
            ));
        }

        if (!cita.getFechaHora().isAfter(LocalDateTime.now())) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha debe ser futura"
            ));
        }

        return pacientesClient.get()
                .uri("/api/pacientes/{id}", cita.getPacienteId())
                .retrieve()
                .onStatus(
                    status -> status.value() == 404,
                    response -> Mono.error(
                        new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "El paciente no existe"
                        )
                    )
                )
                .onStatus(
                    status -> status.is5xxServerError(),
                    response -> Mono.error(
                        new ResponseStatusException(
                            HttpStatus.SERVICE_UNAVAILABLE,
                            "Servicio Pacientes con error"
                        )
                    )
                )
                .toBodilessEntity()
                .onErrorMap(
                    WebClientRequestException.class,
                    error -> new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Servicio Pacientes no disponible"
                    )
                )
                .then(validarHorarioYGuardar(cita));
    }

    private Mono<Cita> validarHorarioYGuardar(Cita cita) {

        return repository
                .existsByProfesionalCodigoAndFechaHoraAndEstadoIn(
                    cita.getProfesionalCodigo(),
                    cita.getFechaHora(),
                    List.of("PENDIENTE", "CONFIRMADA")
                )
                .flatMap(ocupado -> {

                    if (ocupado) {
                        return Mono.error(
                            new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Horario ya reservado"
                            )
                        );
                    }

                    cita.setEstado("PENDIENTE");
                    cita.setFechaCreacion(LocalDateTime.now());
                    cita.setFechaActualizacion(LocalDateTime.now());

                    return repository.save(cita);
                });
    }

    public Mono<Cita> confirmar(Long id) {

        return buscarPorId(id).flatMap(cita -> {

            if (!"PENDIENTE".equals(cita.getEstado())) {
                return Mono.error(
                    new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Solo se confirma una cita pendiente"
                    )
                );
            }

            cita.setEstado("CONFIRMADA");
            cita.setFechaActualizacion(LocalDateTime.now());

            return repository.save(cita)
                .flatMap(guardada ->
                    Mono.fromRunnable(() -> {
                        String mensaje =
                            "Cita confirmada: ID=" + guardada.getId()
                            + ", paciente=" + guardada.getPacienteId();

                        rabbitTemplate.convertAndSend(
                            "hospital.eventos",
                            "cita.confirmada",
                            mensaje
                        );
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .thenReturn(guardada)
                );
        });
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
