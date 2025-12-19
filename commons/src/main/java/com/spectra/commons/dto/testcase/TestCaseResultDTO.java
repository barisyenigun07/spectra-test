package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepResultDTO;

import java.time.Instant;
import java.util.List;

public record TestCaseResultDTO(
        Long testCaseId,
        Long runId,
        String targetPlatform,
        TestCaseStatus status,
        Instant startedAt,
        Instant finishedAt,
        long durationMillis,
        List<StepResultDTO> stepResults
) {
}
