package com.spectra.agent.desktop.engine;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.dto.testcase.TestCaseCreatedEvent;
import com.spectra.commons.dto.step.StepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestCaseRunner {
    private final StepExecutor stepExecutor;

    public void runTestCase(TestCaseCreatedEvent evt) {
        DesktopClient client = DesktopClientFactory.create(evt.config());

        for (StepDTO step : evt.steps()) {
            var ctx = new ExecutionContext(client, step);
            stepExecutor.executeStep(ctx);
        }
    }
}
