package com.spectra.agent.mobile.mq;

import com.spectra.agent.mobile.engine.MobileDriverFactory;
import com.spectra.agent.mobile.engine.context.ExecutionContext;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import io.appium.java_client.AppiumDriver;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MobileJobListener {
    @RabbitListener(queues = "jobs.mobile")
    public void onMessage(JobCreatedEvent evt) {
        AppiumDriver driver = MobileDriverFactory.create(evt.config());

        for (StepDTO step : evt.steps()) {
            var ctx = new ExecutionContext(driver, step);

        }
    }
}
