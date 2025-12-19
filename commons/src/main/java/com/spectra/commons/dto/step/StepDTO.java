package com.spectra.commons.dto.step;

import com.spectra.commons.dto.locator.LocatorDTO;

import java.util.Map;

public record StepDTO(
        Long stepId,
        int orderIndex,
        StepStatus status,
        String action,
        LocatorDTO locator,
        Map<String, Object> params
) {
}
