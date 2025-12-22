package com.spectra.agent.web.mq;

import com.spectra.agent.web.engine.runner.TestCaseRunner;
import com.spectra.commons.dto.testcase.TestCaseRunRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestCaseRunListener {
    private final TestCaseRunner testCaseRunner;

    @RabbitListener(queues = "testcases.web")
    public void onMessage(TestCaseRunRequestedEvent evt) {
        testCaseRunner.runTestCase(evt);
    }
}
