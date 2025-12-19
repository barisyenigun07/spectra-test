package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepDTO;

import java.util.List;
import java.util.Map;

public record TestCaseRunRequestedEvent(
        Long testCaseId,
        Long testCaseRunId,
        String targetPlatform,
        List<StepDTO> steps,
        Map<String, Object> config
) {
}
