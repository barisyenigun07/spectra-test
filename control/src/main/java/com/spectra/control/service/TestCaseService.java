package com.spectra.control.service;

import com.spectra.commons.dto.step.StepCreateDTO;
import com.spectra.commons.dto.testcase.TestCaseCreateRequest;
import com.spectra.commons.dto.testcase.TestCaseCreatedEvent;
import com.spectra.commons.dto.locator.LocatorDTO;
import com.spectra.commons.dto.step.StepDTO;
import com.spectra.commons.dto.testcase.TestCaseDTO;
import com.spectra.control.mapper.TestCaseMapper;
import com.spectra.control.model.TestCase;
import com.spectra.control.model.Locator;
import com.spectra.control.model.Step;
import com.spectra.control.repository.TestCaseRepository;
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
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final TestCaseMapper testCaseMapper;
    private final TestCasePublisher testCasePublisher;

    @Transactional
    public void createTestCase(TestCaseCreateRequest req) {
        TestCase testCase = new TestCase();
        testCase.setTargetPlatform(req.targetPlatform());
        testCase.setStatus("QUEUED");

        List<Step> steps = new ArrayList<>();

        for (StepCreateDTO s : req.steps()) {
            Step step = new Step();
            step.setOrderIndex(s.orderIndex());
            step.setAction(s.action());
            step.setStatus("PENDING");
            step.setLocator(new Locator(s.locator().type(), s.locator().value()));
            step.setParams(s.params());
            step.setTestCase(testCase);
            steps.add(step);
        }

        testCase.setSteps(steps);
        testCase.setConfig(req.config());
        testCase = testCaseRepository.save(testCase);

        List<StepDTO> orderedSteps = testCase.getSteps().stream()
                .sorted(Comparator.comparing(Step::getOrderIndex))
                .map(st -> new StepDTO(
                        st.getId(),
                        st.getOrderIndex(),
                        st.getAction(),
                        new LocatorDTO(st.getLocator().getType(), st.getLocator().getValue()),
                        st.getParams()
                )).toList();

        TestCaseCreatedEvent evt = new TestCaseCreatedEvent(testCase.getId(), testCase.getTargetPlatform(), orderedSteps, testCase.getConfig());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                testCasePublisher.send(evt);
            }
        });
    }

    public TestCaseDTO getTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return testCaseMapper.toDto(testCase);
    }

    public void deleteTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        testCaseRepository.delete(testCase);
    }
}
