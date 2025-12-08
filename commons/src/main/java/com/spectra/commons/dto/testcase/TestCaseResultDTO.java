package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepResultDTO;

import java.time.Instant;
import java.util.List;

public record TestCaseResultDTO(
        Long id,
        String targetPlatform,
        String status,
        Instant startTime,
        Instant endTime,
        List<StepResultDTO> stepResults
) {
}
