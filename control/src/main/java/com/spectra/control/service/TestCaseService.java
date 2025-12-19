package com.spectra.control.service;

import com.spectra.commons.dto.step.StepCreateDTO;
import com.spectra.commons.dto.step.StepStatus;
import com.spectra.commons.dto.testcase.*;
import com.spectra.commons.dto.locator.LocatorDTO;
import com.spectra.commons.dto.step.StepDTO;
import com.spectra.control.mapper.TestCaseMapper;
import com.spectra.control.model.TestCase;
import com.spectra.control.model.Locator;
import com.spectra.control.model.Step;
import com.spectra.control.model.TestCaseRun;
import com.spectra.control.repository.TestCaseRepository;
import com.spectra.control.repository.TestCaseRunRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final TestCaseRunRepository testCaseRunRepository;
    private final TestCaseMapper testCaseMapper;
    private final TestCaseRunPublisher testCaseRunPublisher;

    @Transactional
    public void createTestCase(TestCaseCreateRequest req) {
        TestCase testCase = new TestCase();
        testCase.setTargetPlatform(req.targetPlatform());
        testCase.setStatus(TestCaseStatus.DRAFT);

        List<Step> steps = new ArrayList<>();

        for (StepCreateDTO s : req.steps()) {
            Step step = new Step();
            step.setOrderIndex(s.orderIndex());
            step.setAction(s.action());
            step.setStatus(StepStatus.CREATED);
            step.setLocator(new Locator(s.locator().type(), s.locator().value()));
            step.setParams(s.params());
            step.setTestCase(testCase);
            steps.add(step);
        }

        testCase.setSteps(steps);
        testCase.setConfig(req.config());
        testCaseRepository.save(testCase);
    }

    @Transactional
    public void runTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Test Case not found!"));
        testCase.setStatus(TestCaseStatus.QUEUED);

        TestCaseRun run = new TestCaseRun();
        run.setTestCase(testCase);
        run.setStatus(TestCaseStatus.RUNNING);
        run.setStartedAt(Instant.now());
        run = testCaseRunRepository.save(run);

        List<StepDTO> orderedSteps = testCase.getSteps().stream()
                .sorted(Comparator.comparing(Step::getOrderIndex))
                .map(step -> new StepDTO(
                        step.getId(),
                        step.getOrderIndex(),
                        step.getStatus(),
                        step.getAction(),
                        step.getLocator() == null ? null : new LocatorDTO(step.getLocator().getType(), step.getLocator().getValue()),
                        step.getParams()
                )).toList();

        TestCaseRunRequestedEvent evt = new TestCaseRunRequestedEvent(
                testCase.getId(),
                run.getId(),
                testCase.getTargetPlatform(),
                orderedSteps,
                testCase.getConfig()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                testCaseRunPublisher.send(evt);
            }
        });

    }

    @Transactional
    public void handleTestCaseResult(TestCaseResultDTO res) {

    }

    public TestCaseDTO getTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Test Case not found!"));
        return testCaseMapper.toDto(testCase);
    }

    public void deleteTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Test Case not found!"));
        testCaseRepository.delete(testCase);
    }
}
