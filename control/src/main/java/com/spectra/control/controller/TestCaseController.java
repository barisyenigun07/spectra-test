package com.spectra.control.controller;

import com.spectra.commons.dto.testcase.TestCaseCreateRequest;
import com.spectra.commons.dto.testcase.TestCaseDTO;
import com.spectra.control.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testcases")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @PostMapping
    public void createTestCase(@RequestBody TestCaseCreateRequest req) {
        testCaseService.createTestCase(req);
    }

    @GetMapping("/{id}")
    public TestCaseDTO getTestCase(@PathVariable("id") Long id) {
        return testCaseService.getTestCase(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTestCase(@PathVariable("id") Long id) {
        testCaseService.deleteTestCase(id);
    }
}
