package com.spectra.control.service;

import com.spectra.commons.dto.JobCreateRequest;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import com.spectra.control.model.Job;
import com.spectra.control.model.Locator;
import com.spectra.control.model.Step;
import com.spectra.control.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final JobPublisher jobPublisher;

    public void createJob(JobCreateRequest req) {
        Job job = new Job();
        job.setTargetPlatform(req.targetPlatform());
        job.setStatus("QUEUED");

        List<Step> steps = new ArrayList<>();

        for (StepDTO s : req.steps()) {
            Step step = new Step();
            step.setOrderIndex(s.orderIndex());
            step.setAction(s.action());
            step.setStatus("PENDING");
            step.setLocator(new Locator(s.locator().type(), s.locator().value()));
            step.setJob(job);
            steps.add(step);
        }

        job.setSteps(steps);
        job = jobRepository.save(job);

        JobCreatedEvent evt = new JobCreatedEvent(job.getId(), req.targetPlatform(), req.steps());
        jobPublisher.send(evt);
    }
}
