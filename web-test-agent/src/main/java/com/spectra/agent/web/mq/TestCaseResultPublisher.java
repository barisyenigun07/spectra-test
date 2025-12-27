package com.spectra.agent.web.mq;

import com.spectra.agent.web.config.AmqpConfig;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseResultPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void send(TestCaseResultDTO res) {
        String rk = "testcase.result.completed";
        rabbitTemplate.convertAndSend(AmqpConfig.RESULTS_EX, rk, res, m -> {
            m.getMessageProperties().setContentType("application/json");
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            m.getMessageProperties().setCorrelationId(res.runId().toString());
            return m;
        });
    }
}
