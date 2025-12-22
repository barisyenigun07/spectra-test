package com.spectra.control.service;

import com.spectra.commons.dto.step.StepResultDTO;
import com.spectra.commons.dto.step.StepStatus;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
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

        testCaseRun.setStartedAt(res.startedAt());
        testCaseRun.setFinishedAt(res.finishedAt());
        long dur = Duration.between(testCaseRun.getStartedAt(), testCaseRun.getFinishedAt()).toMillis();
        testCaseRun.setDurationMillis(dur);

        testCaseRun.setStatus(res.status());

        long failed = res.stepResults().stream().filter(s -> StepStatus.FAILED.equals(s.status())).count();
        long passed = res.stepResults().stream().filter(s -> StepStatus.PASSED.equals(s.status())).count();
        long skipped = res.stepResults().stream().filter(s -> StepStatus.SKIPPED.equals(s.status())).count();

        testCaseRun.setFailedSteps((int) failed);
        testCaseRun.setPassedSteps((int) passed);
        testCaseRun.setSkippedSteps((int) skipped);

        for (StepResultDTO sr : res.stepResults()) {
            Step step = stepRepository.findById(sr.stepId()).orElse(null);
            if (step == null) continue;
            StepRun stepRun = new StepRun();
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
