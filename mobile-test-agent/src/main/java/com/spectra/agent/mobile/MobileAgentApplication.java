package com.spectra.agent.mobile;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class MobileAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MobileAgentApplication.class, args);
    }
}