package com.spectra.agent.desktop.engine;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.agent.desktop.mq.TestCaseResultPublisher;
import com.spectra.commons.dto.step.StepResultDTO;
import com.spectra.commons.dto.step.StepStatus;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import com.spectra.commons.dto.testcase.TestCaseRunRequestedEvent;
import com.spectra.commons.dto.step.StepDTO;
import com.spectra.commons.dto.testcase.TestCaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TestCaseRunner {
    private final StepExecutor stepExecutor;
    private final TestCaseResultPublisher testCaseResultPublisher;

    public void runTestCase(TestCaseRunRequestedEvent evt) {
        DesktopClient client = DesktopClientFactory.create(evt.config());
        Instant start = Instant.now();

        List<StepResultDTO> stepResults = new ArrayList<>();
        boolean failed = false;

        for (StepDTO step : evt.steps()) {
            Instant s0 = Instant.now();
            StepStatus status;
            String message = null;
            String errorType = null;
            String errorMessage = null;
            try {
                if (!failed) {
                    var ctx = new ExecutionContext(client, step);
                    stepExecutor.executeStep(ctx);
                    status = StepStatus.PASSED;
                    message = "OK";
                }
                else {
                    status = StepStatus.SKIPPED;
                    message = "Skipped due to previous failure";
                }
            }
            catch (Exception e) {
                failed = true;
                status = StepStatus.FAILED;
                errorType = e.getClass().getSimpleName();
                errorMessage = e.getMessage();
            }

            Instant s1 = Instant.now();
            long dur = Duration.between(s0, s1).toMillis();

            stepResults.add(new StepResultDTO(
                    step.stepId(),
                    evt.testCaseRunId(),
                    step.orderIndex(),
                    step.action(),
                    step.locator(),
                    status,
                    message,
                    s0,
                    s1,
                    dur,
                    errorMessage,
                    errorType,
                    Map.of()
            ));
        }

        Instant end = Instant.now();
        TestCaseStatus testCaseStatus = failed ? TestCaseStatus.FAILED : TestCaseStatus.PASSED;

        TestCaseResultDTO result = new TestCaseResultDTO(
                evt.testCaseId(),
                evt.testCaseRunId(),
                evt.targetPlatform(),
                testCaseStatus,
                start,
                end,
                Duration.between(start, end).toMillis(),
                stepResults
        );

        testCaseResultPublisher.send(result);
    }
}
