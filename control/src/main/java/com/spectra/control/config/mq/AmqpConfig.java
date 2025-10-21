package com.spectra.control.config.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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

    private Declarables agentQueues(String agent, int retryTtlMs) {
        String main = "jobs." + agent;
        String retry = "jobs." + agent + ".retry";
        String dead = "jobs." + agent + ".dead";

        Queue qMain = QueueBuilder.durable(main)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey("job.created." + agent + ".retry")
                .build();

        Queue qRetry = QueueBuilder.durable(retry)
                .withArgument("x-message-ttl", retryTtlMs)
                .deadLetterExchange(JOBS_EX)
                .deadLetterRoutingKey("job.created." + agent)
                .build();

        Queue qDead = QueueBuilder.durable(dead).build();

        return new Declarables(
                qMain, qRetry, qDead,
                BindingBuilder.bind(qMain).to(jobsExchange()).with("job.created." + agent + ".#"),
                BindingBuilder.bind(qRetry).to(dlx()).with("job.created." + agent + ".retry"),
                BindingBuilder.bind(qDead).to(dlx()).with("job.created." + agent + ".dead")
        );
    }

    @Bean
    public Declarables webQueues() {
        return agentQueues("web", 15_000);
    }

    @Bean
    public Declarables mobileQueues() {
        return agentQueues("mobile", 15_000);
    }

    @Bean
    public Declarables desktopQueues() {
        return agentQueues("desktop", 15_000);
    }

    @Bean
    public Queue resultsControl() {
        return QueueBuilder.durable("results.control").build();
    }

    @Bean
    public Declarables resultBindings() {
        return new Declarables(
                BindingBuilder.bind(resultsControl()).to(resultsExchange()).with("#")
        );
    }

    @Bean
    public MessageConverter jackson2JsonConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cf);
        rabbitTemplate.setMessageConverter(mc);
        return rabbitTemplate;
    }

}
