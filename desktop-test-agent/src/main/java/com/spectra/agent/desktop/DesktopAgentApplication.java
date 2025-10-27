package com.spectra.agent.desktop;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class DesktopAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(DesktopAgentApplication.class, args);
    }
}