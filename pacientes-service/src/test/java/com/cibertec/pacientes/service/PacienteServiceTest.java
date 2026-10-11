
package com.cibertec.pacientes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.pacientes.model.Paciente;

public class PacienteServiceTest {

    @Test
    void rechazarPacienteSinDatos() {

        PacienteService service = new PacienteService(null);
        Paciente paciente = new Paciente();

        ResponseStatusException error = assertThrows(
            ResponseStatusException.class,
            () -> service.registrar(paciente).block()
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void rechazarPacienteConIdManual() {

        PacienteService service = new PacienteService(null);
        Paciente paciente = new Paciente();
        paciente.setId(10L);

        ResponseStatusException error = assertThrows(
            ResponseStatusException.class,
            () -> service.registrar(paciente).block()
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }
}
