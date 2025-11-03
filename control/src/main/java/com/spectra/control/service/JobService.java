package com.spectra.control.service;

import com.spectra.commons.dto.JobCreateRequest;
import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.LocatorDTO;
import com.spectra.commons.dto.StepDTO;
import com.spectra.control.model.Job;
import com.spectra.control.model.Locator;
import com.spectra.control.model.Step;
import com.spectra.control.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final JobPublisher jobPublisher;

    @Transactional
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
            step.setInputValue(s.inputValue());
            step.setJob(job);
            steps.add(step);
        }

        job.setSteps(steps);
        job = jobRepository.save(job);

        List<StepDTO> orderedSteps = job.getSteps().stream()
                .sorted(Comparator.comparing(Step::getOrderIndex))
                .map(st -> new StepDTO(
                        st.getOrderIndex(),
                        st.getAction(),
                        new LocatorDTO(st.getLocator().getType(), st.getLocator().getValue()),
                        st.getInputValue()
                )).toList();

        JobCreatedEvent evt = new JobCreatedEvent(job.getId(), req.targetPlatform(), orderedSteps, req.config());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jobPublisher.send(evt);
            }
        });
    }
}
