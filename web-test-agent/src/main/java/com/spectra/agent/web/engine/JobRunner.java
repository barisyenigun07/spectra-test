package com.spectra.agent.web.engine;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JobRunner {
    private final StepExecutor stepExecutor;

    public void runJob(JobCreatedEvent evt) {
        WebDriver driver = WebDriverFactory.create(evt.config());
        WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            for (StepDTO step : evt.steps()) {
                var ctx = new ExecutionContext(driver, step, driverWait);
                stepExecutor.executeStep(ctx);
            }

            Thread.sleep(5000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            driver.quit();
        }
    }

}
