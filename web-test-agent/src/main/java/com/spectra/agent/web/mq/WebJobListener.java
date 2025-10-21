package com.spectra.agent.web.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebJobListener {
    @RabbitListener(queues = "jobs.web")
    public void onJob(Map<String, Object> payload) {
        System.out.println("WEB AGENT GOT MESSAGE, " + payload);
    }
}
