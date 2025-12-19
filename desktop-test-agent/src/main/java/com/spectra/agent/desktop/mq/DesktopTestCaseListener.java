package com.spectra.agent.desktop.mq;


import com.spectra.agent.desktop.engine.TestCaseRunner;
import com.spectra.commons.dto.testcase.TestCaseRunRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DesktopTestCaseListener {
    private final TestCaseRunner testCaseRunner;

    @RabbitListener(queues = "testcases.desktop")
    public void onMessage(TestCaseRunRequestedEvent evt) {
        testCaseRunner.runTestCase(evt);
    }
}
