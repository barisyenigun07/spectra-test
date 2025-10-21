package com.spectra.control.controller;

import com.spectra.commons.dto.JobCreateRequest;
import com.spectra.control.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    public void createJob(@RequestBody JobCreateRequest req) {
        jobService.createJob(req);
    }
}
