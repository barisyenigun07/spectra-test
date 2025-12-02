package com.spectra.agent.mobile.engine;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import io.appium.java_client.AppiumDriver;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JobRunner {
    private final StepExecutor stepExecutor;

    public void runJob(JobCreatedEvent evt) {
        AppiumDriver driver = MobileDriverFactory.create(evt.config());
        WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (StepDTO step : evt.steps()) {
            var ctx = new ExecutionContext(driver, step, driverWait);
            stepExecutor.executeStep(ctx);
        }
    }
}
