package com.spectra.control;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ControlApplication {
    public static void main(String[] args) {
        SpringApplication.run(ControlApplication.class, args);
    }
}