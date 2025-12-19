package com.spectra.control.service;

import com.spectra.commons.dto.testcase.TestCaseRunRequestedEvent;
import com.spectra.control.config.mq.AmqpConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseRunPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void send(TestCaseRunRequestedEvent evt) {
        String rk = "testcase.run.requested." + evt.targetPlatform() + ".#";
        rabbitTemplate.convertAndSend(AmqpConfig.TESTCASES_EX, rk, evt, m -> {
            m.getMessageProperties().setContentType("application/json");
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            m.getMessageProperties().setCorrelationId(evt.testCaseId().toString());
            return m;
        });
    }
}
