package com.spectra.agent.desktop.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "")
public class AmqpListener {
    @RabbitHandler
    public void process(String message) {
        System.out.println(message);
    }
}
