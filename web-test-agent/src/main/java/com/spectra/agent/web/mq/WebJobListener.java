package com.spectra.agent.web.mq;

import com.spectra.agent.web.engine.StepExecutor;
import com.spectra.agent.web.engine.WebDriverFactory;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import org.openqa.selenium.WebDriver;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class WebJobListener {
    @RabbitListener(queues = "jobs.web")
    public void onMessage(JobCreatedEvent evt) {
        Map<String, String> config = evt.config();
        WebDriver driver = WebDriverFactory.create(config);
        StepExecutor stepExecutor = new StepExecutor(driver);

        for (StepDTO s : evt.steps()) {
            stepExecutor.executeStep(s);
        }
    }
}
