
package com.cibertec.notificaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String EXCHANGE = "hospital.eventos";

    public static final String COLA =
            "hospital.citas.confirmadas.notificaciones";

    public static final String ROUTING_KEY =
            "cita.confirmada";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue colaNotificaciones() {
        return new Queue(COLA, true);
    }

    @Bean
    public Binding bindingNotificaciones(
            Queue colaNotificaciones,
            DirectExchange exchange) {

        return BindingBuilder
                .bind(colaNotificaciones)
                .to(exchange)
                .with(ROUTING_KEY);
    }
}
