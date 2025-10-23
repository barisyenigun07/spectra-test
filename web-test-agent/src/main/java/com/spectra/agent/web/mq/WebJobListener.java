package com.spectra.agent.web.mq;

import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WebJobListener {
    @RabbitListener(queues = "jobs.web")
    public void onMessage(JobCreatedEvent evt) {
        System.out.println("Received JobCreatedEvent ID: " + evt.jobId());
        System.out.println("Received JobCreatedEvent Target Platform:" + evt.targetPlatform());
        System.out.println("=== Steps ===");

        for (StepDTO s : evt.steps()) {
            System.out.println("Order Index: " + s.orderIndex());
            System.out.println("Action: " + s.action());
            System.out.println("Locator Type: " + s.locator().type());
            System.out.println("Locator Value: " + s.locator().value());
        }
    }
}
