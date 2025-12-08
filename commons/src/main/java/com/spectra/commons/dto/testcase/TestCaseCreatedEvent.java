package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepDTO;

import java.util.List;
import java.util.Map;

public record TestCaseCreatedEvent(Long jobId, String targetPlatform, List<StepDTO> steps, Map<String, String> config) {
}
