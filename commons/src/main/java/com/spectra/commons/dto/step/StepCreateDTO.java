package com.spectra.commons.dto.step;

import com.spectra.commons.dto.locator.LocatorDTO;

import java.util.Map;

public record StepCreateDTO(
        int orderIndex,
        String action,
        LocatorDTO locator,
        Map<String, Object> params
) {
}
