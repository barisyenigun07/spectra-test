package com.spectra.control.config.mq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String JOBS_EX = "jobs.exchange";
    public static final String RESULTS_EX = "results.exchange";
    public static final String DLX = "jobs.dlx";

    @Bean
    public TopicExchange jobsExchange() {
        return new TopicExchange(JOBS_EX, true, false);
    }

    @Bean
    public TopicExchange resultsExchange() {
        return new TopicExchange(RESULTS_EX, true, false);
    }

    @Bean
    public TopicExchange dlx() {
        return new TopicExchange(DLX, true, false);
    }

}
