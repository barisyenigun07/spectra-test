package com.spectra.agent.desktop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String RESULTS_EX = "results.exchange";

    @Bean
    public TopicExchange resultsExchange() {
        return new TopicExchange(RESULTS_EX ,true, false);
    }

    @Bean
    public MessageConverter jackson2JsonConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(mc);
        return t;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, MessageConverter mc
    ) {
        var f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMissingQueuesFatal(false);
        f.setFailedDeclarationRetryInterval(5000L);
        f.setMessageConverter(mc);
        f.setConcurrentConsumers(1);
        f.setMaxConcurrentConsumers(4);
        f.setPrefetchCount(1);
        f.setDefaultRequeueRejected(false);
        f.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000L)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return f;
    }
}
