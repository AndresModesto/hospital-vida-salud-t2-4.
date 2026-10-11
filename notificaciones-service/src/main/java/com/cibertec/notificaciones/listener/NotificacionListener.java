
package com.cibertec.notificaciones.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacionListener {

    @RabbitListener(
        queues = "hospital.citas.confirmadas.notificaciones"
    )
    public void recibirCitaConfirmada(String mensaje) {

        System.out.println("==============================");
        System.out.println("NOTIFICACION RECIBIDA");
        System.out.println("Evento: CitaConfirmada");
        System.out.println("Mensaje: " + mensaje);
        System.out.println("==============================");
    }
}
