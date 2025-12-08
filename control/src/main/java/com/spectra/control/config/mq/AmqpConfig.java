package com.spectra.control.config.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String TESTCASES_EX = "testcases.exchange";
    public static final String RESULTS_EX = "results.exchange";
    public static final String DLX = "testcases.dlx";

    @Bean
    public TopicExchange testCasesExchange() {
        return new TopicExchange(TESTCASES_EX, true, false);
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
        String main = "testcases." + agent;
        String retry = "testcases." + agent + ".retry";
        String dead = "testcases." + agent + ".dead";

        Queue qMain = QueueBuilder.durable(main)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey("testcase.created." + agent + ".retry")
                .build();

        Queue qRetry = QueueBuilder.durable(retry)
                .withArgument("x-message-ttl", retryTtlMs)
                .deadLetterExchange(TESTCASES_EX)
                .deadLetterRoutingKey("testcase.created." + agent)
                .build();

        Queue qDead = QueueBuilder.durable(dead).build();

        return new Declarables(
                qMain, qRetry, qDead,
                BindingBuilder.bind(qMain).to(testCasesExchange()).with("testcase.created." + agent + ".#"),
                BindingBuilder.bind(qRetry).to(dlx()).with("testcase.created." + agent + ".retry"),
                BindingBuilder.bind(qDead).to(dlx()).with("testcase.created." + agent + ".dead")
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

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(cf);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

}
