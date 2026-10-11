
package com.cibertec.pacientes.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.cibertec.pacientes.model.Paciente;
import reactor.core.publisher.Mono;

public interface PacienteRepository extends ReactiveCrudRepository<Paciente, Long> {

    Mono<Paciente> findByTipoDocumentoAndNumeroDocumento(
            String tipoDocumento,
            String numeroDocumento
    );
}
