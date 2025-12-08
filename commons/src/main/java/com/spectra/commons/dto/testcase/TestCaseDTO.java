package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepDTO;

import java.time.Instant;
import java.util.List;

public record TestCaseDTO(
        Long id,
        String targetPlatform,
        String status,
        Instant createdAt,
        Instant updatedAt,
        List<StepDTO> steps
) {
}
