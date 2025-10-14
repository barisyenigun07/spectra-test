package com.spectra.control.service;

import com.spectra.control.model.Job;
import com.spectra.control.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;

    public void createJob(Job job) {
        jobRepository.save(job);
    }
}
