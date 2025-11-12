package com.spectra.agent.web.mq;

import com.spectra.agent.web.engine.StepExecutor;
import com.spectra.agent.web.engine.WebDriverFactory;
import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class WebJobListener {
    private final StepExecutor stepExecutor;

    @RabbitListener(queues = "jobs.web")
    public void onMessage(JobCreatedEvent evt) {
        WebDriver driver = WebDriverFactory.create(evt.config());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            for (StepDTO step : evt.steps()) {
                var ctx = new ExecutionContext(driver, step, wait);
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

        System.out.println("StepExecutor executed");
    }
}
