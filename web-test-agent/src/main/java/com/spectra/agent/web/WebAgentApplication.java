package com.spectra.agent.web;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class WebAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAgentApplication.class, args);
    }
}