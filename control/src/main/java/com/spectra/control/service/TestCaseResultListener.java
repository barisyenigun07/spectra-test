package com.spectra.control.service;

import com.spectra.commons.dto.step.StepResultDTO;
import com.spectra.commons.dto.step.StepStatus;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import com.spectra.commons.dto.testcase.TestCaseStatus;
import com.spectra.control.model.Step;
import com.spectra.control.model.StepRun;
import com.spectra.control.model.TestCaseRun;
import com.spectra.control.repository.StepRepository;
import com.spectra.control.repository.StepRunRepository;
import com.spectra.control.repository.TestCaseRunRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestCaseResultListener {
    private final TestCaseRunRepository testCaseRunRepository;
    private final StepRunRepository stepRunRepository;
    private final StepRepository stepRepository;

    @Transactional
    @RabbitListener(queues = "testcase.results")
    public void onResult(TestCaseResultDTO res) {
        TestCaseRun testCaseRun = testCaseRunRepository.findById(res.runId()).orElseThrow(() -> new RuntimeException("Test Case Run not found!"));

        if (testCaseRun.getStatus() != TestCaseStatus.RUNNING) return;

        if (testCaseRun.getStartedAt() == null) {
            testCaseRun.setStartedAt(res.startedAt());
        }

        testCaseRun.setFinishedAt(res.finishedAt());

        if (testCaseRun.getStartedAt() != null && testCaseRun.getFinishedAt() != null) {
            testCaseRun.setDurationMillis(Duration.between(testCaseRun.getStartedAt(), testCaseRun.getFinishedAt()).toMillis());
        }

        testCaseRun.setStatus(res.status());

        long failed = res.stepResults().stream().filter(s -> StepStatus.FAILED.equals(s.status())).count();
        long passed = res.stepResults().stream().filter(s -> StepStatus.PASSED.equals(s.status())).count();
        long skipped = res.stepResults().stream().filter(s -> StepStatus.SKIPPED.equals(s.status())).count();

        testCaseRun.setFailedSteps((int) failed);
        testCaseRun.setPassedSteps((int) passed);
        testCaseRun.setSkippedSteps((int) skipped);

        var stepIds = res.stepResults().stream().map(StepResultDTO::stepId).toList();
        var stepMap = stepRepository.findAllById(stepIds).stream().collect(Collectors.toMap(Step::getId, s -> s));

        for (StepResultDTO sr : res.stepResults()) {
            Step step = stepMap.get(sr.stepId());
            if (step == null) continue;
            StepRun stepRun = stepRunRepository.findByTestCaseRunIdAndStepId(testCaseRun.getId(), step.getId()).orElseGet(StepRun::new);
            stepRun.setTestCaseRun(testCaseRun);
            stepRun.setStep(step);
            stepRun.setStatus(sr.status());
            stepRun.setMessage(sr.message());
            stepRun.setStartedAt(sr.startedAt());
            stepRun.setFinishedAt(sr.finishedAt());
            stepRun.setDurationMillis(sr.durationMillis());
            stepRun.setErrorType(sr.errorType());
            stepRun.setErrorMessage(sr.errorMessage());
            stepRun.setExtra(sr.extra());
            stepRunRepository.save(stepRun);
        }

        testCaseRunRepository.save(testCaseRun);
    }
}
