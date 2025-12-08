package com.spectra.agent.web.mq;

import com.spectra.agent.web.engine.TestCaseRunner;
import com.spectra.commons.dto.testcase.TestCaseCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebTestCaseListener {
    private final TestCaseRunner testCaseRunner;

    @RabbitListener(queues = "testcases.web")
    public void onMessage(TestCaseCreatedEvent evt) {
        testCaseRunner.runTestCase(evt);
    }
}
