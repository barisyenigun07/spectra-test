package com.spectra.agent.web.mq;

import com.spectra.agent.web.engine.JobRunner;
import com.spectra.commons.dto.JobCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebJobListener {
    private final JobRunner jobRunner;

    @RabbitListener(queues = "jobs.web")
    public void onMessage(JobCreatedEvent evt) {
        jobRunner.runJob(evt);
    }
}
