package com.spectra.control.service;

import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.control.config.mq.AmqpConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void send(JobCreatedEvent evt) {
        String rk = "job.created." + evt.targetPlatform() + ".#";
        rabbitTemplate.convertAndSend(AmqpConfig.JOBS_EX, rk, evt, m -> {
            m.getMessageProperties().setContentType("application/json");
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            m.getMessageProperties().setCorrelationId(evt.jobId().toString());
            return m;
        });
    }
}
