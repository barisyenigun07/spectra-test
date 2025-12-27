package com.spectra.control.controller;

import com.spectra.commons.dto.testcase.TestCaseCreateRequest;
import com.spectra.commons.dto.testcase.TestCaseDTO;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import com.spectra.control.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testcases")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @PostMapping
    public ResponseEntity<TestCaseDTO> createTestCase(@RequestBody TestCaseCreateRequest req) {
        TestCaseDTO testCaseDTO = testCaseService.createTestCase(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(testCaseDTO);
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<Map<String, Long>> runTestCase(@PathVariable("id") Long id) {
        Long runId = testCaseService.runTestCase(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("runId", runId));
    }

    @GetMapping("/{id}/run/results")
    public ResponseEntity<List<TestCaseResultDTO>> getTestCaseRunResults(@PathVariable("id") Long testCaseId) {
        return ResponseEntity.ok(testCaseService.getTestCaseRunResults(testCaseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseDTO> getTestCase(@PathVariable("id") Long id) {
        return ResponseEntity.ok(testCaseService.getTestCase(id));
    }

    @GetMapping
    public ResponseEntity<List<TestCaseDTO>> getTestCases() {
        return ResponseEntity.ok(testCaseService.getTestCases());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Long>> deleteTestCase(@PathVariable("id") Long id) {
        Long deletedTestCaseId = testCaseService.deleteTestCase(id);
        return ResponseEntity.ok(Map.of("deletedTestCaseId", deletedTestCaseId));
    }
}
