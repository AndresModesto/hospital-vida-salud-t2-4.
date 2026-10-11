package com.cibertec.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerT2Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerT2Application.class, args);
    }
}