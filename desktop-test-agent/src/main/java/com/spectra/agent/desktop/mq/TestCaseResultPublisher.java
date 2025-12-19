package com.spectra.agent.desktop.mq;

import com.spectra.agent.desktop.config.AmqpConfig;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseResultPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void send(TestCaseResultDTO res) {
        String rk = "testcase.result.completed.#";
        rabbitTemplate.convertAndSend(AmqpConfig.RESULTS_EX, rk, res);
    }
}
