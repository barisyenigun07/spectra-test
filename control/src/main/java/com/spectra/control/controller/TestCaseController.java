package com.spectra.control.controller;

import com.spectra.commons.dto.testcase.TestCaseCreateRequest;
import com.spectra.control.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testcases")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @PostMapping
    public void createJob(@RequestBody TestCaseCreateRequest req) {
        testCaseService.createJob(req);
    }
}
