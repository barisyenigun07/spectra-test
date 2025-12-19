package com.spectra.commons.dto.testcase;

import com.spectra.commons.dto.step.StepCreateDTO;

import java.util.List;
import java.util.Map;

public record TestCaseCreateRequest(
        String targetPlatform,
        List<StepCreateDTO> steps,
        Map<String, Object> config
) {
}
