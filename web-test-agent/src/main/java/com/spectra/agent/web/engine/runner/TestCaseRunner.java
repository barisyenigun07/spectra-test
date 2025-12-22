package com.spectra.agent.web.engine.runner;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.agent.web.engine.factory.WebDriverFactory;
import com.spectra.agent.web.mq.TestCaseResultPublisher;
import com.spectra.commons.dto.step.StepResultDTO;
import com.spectra.commons.dto.step.StepStatus;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import com.spectra.commons.dto.testcase.TestCaseRunRequestedEvent;
import com.spectra.commons.dto.step.StepDTO;
import com.spectra.commons.dto.testcase.TestCaseStatus;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        WebDriver driver = WebDriverFactory.create(evt.config());
        WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Instant tcStart = Instant.now();

        List<StepResultDTO> stepResults = new ArrayList<>();
        boolean failed = false;

        for (StepDTO step : evt.steps()) {
            Instant stStart = Instant.now();
            StepStatus stepStatus;
            String message = null;
            String errorType = null;
            String errorMessage = null;

            try {
                if (!failed) {
                    var ctx = new ExecutionContext(driver, step, driverWait);
                    stepExecutor.executeStep(ctx);
                    stepStatus = StepStatus.PASSED;
                    message = "OK";
                }
                else {
                    stepStatus = StepStatus.SKIPPED;
                    message = "Skipped due to previous failure";
                }
            }
            catch (AssertionError ae) {
                failed = true;
                stepStatus = StepStatus.FAILED;
                errorType = "ASSERTION_FAILED";
                errorMessage = ae.getMessage();
            }
            catch (Exception e) {
                failed = true;
                stepStatus = StepStatus.FAILED;
                errorType = e.getClass().getSimpleName();
                errorMessage = e.getMessage();
            }

            Instant stEnd = Instant.now();
            long dur = Duration.between(stStart, stEnd).toMillis();

            stepResults.add(new StepResultDTO(
                    step.stepId(),
                    evt.testCaseRunId(),
                    step.orderIndex(),
                    step.action(),
                    step.locator(),
                    stepStatus,
                    message,
                    stStart,
                    stEnd,
                    dur,
                    errorMessage,
                    errorType,
                    Map.of()
            ));
        }

        Instant tcEnd = Instant.now();
        long tcDur = Duration.between(tcStart, tcEnd).toMillis();
        TestCaseStatus testCaseStatus = failed ? TestCaseStatus.FAILED : TestCaseStatus.PASSED;

        TestCaseResultDTO res = new TestCaseResultDTO(
                evt.testCaseId(),
                evt.testCaseRunId(),
                evt.targetPlatform(),
                testCaseStatus,
                tcStart,
                tcEnd,
                tcDur,
                stepResults
        );

        testCaseResultPublisher.send(res);

    }

}
