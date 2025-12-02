package com.spectra.agent.desktop.mq;


import com.spectra.agent.desktop.engine.JobRunner;
import com.spectra.commons.dto.JobCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DesktopJobListener {
    private final JobRunner jobRunner;

    @RabbitListener(queues = "jobs.desktop")
    public void onMessage(JobCreatedEvent evt) {
        jobRunner.runJob(evt);
    }
}
