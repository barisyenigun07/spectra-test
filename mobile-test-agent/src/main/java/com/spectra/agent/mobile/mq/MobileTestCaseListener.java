package com.spectra.agent.mobile.mq;

import com.spectra.agent.mobile.engine.TestCaseRunner;
import com.spectra.commons.dto.testcase.TestCaseCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MobileTestCaseListener {
    private final TestCaseRunner testCaseRunner;

    @RabbitListener(queues = "testcases.mobile")
    public void onMessage(TestCaseCreatedEvent evt) {
        testCaseRunner.runTestCase(evt);
    }
}
