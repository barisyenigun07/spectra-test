package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepDTO;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record TestCaseDTO(
        Long testCaseId,
        String targetPlatform,
        Instant createdAt,
        Instant updatedAt,
        List<StepDTO> steps,
        Map<String, Object> config
) {
}
