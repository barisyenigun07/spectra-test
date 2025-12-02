package com.spectra.agent.mobile.mq;

import com.spectra.agent.mobile.engine.JobRunner;
import com.spectra.commons.dto.JobCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MobileJobListener {
    private final JobRunner jobRunner;

    @RabbitListener(queues = "jobs.mobile")
    public void onMessage(JobCreatedEvent evt) {
        jobRunner.runJob(evt);
    }
}
