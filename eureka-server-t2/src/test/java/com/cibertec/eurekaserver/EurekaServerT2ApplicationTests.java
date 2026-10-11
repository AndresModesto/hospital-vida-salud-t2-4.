
package com.cibertec.eurekaserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

class EurekaServerT2ApplicationTests {

    @Test
    void verificarEurekaHabilitado() {
        assertNotNull(
            EurekaServerT2Application.class
                .getAnnotation(EnableEurekaServer.class)
        );
    }
}
