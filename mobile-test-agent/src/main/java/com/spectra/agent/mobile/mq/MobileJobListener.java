package com.spectra.agent.mobile.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "")
public class MobileJobListener {
    @RabbitHandler
    public void process(String message) {
        System.out.println(message);
    }
}
