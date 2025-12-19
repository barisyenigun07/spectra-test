package com.spectra.commons.dto.step;

import com.spectra.commons.dto.locator.LocatorDTO;

import java.time.Instant;
import java.util.Map;

public record StepResultDTO(
        Long stepId,
        Long runId,
        int orderIndex,
        String action,
        LocatorDTO locator,
        StepStatus status,
        String message,
        Instant startedAt,
        Instant finishedAt,
        long durationMillis,
        String errorMessage,
        String errorType,
        Map<String, Object> extra
) {}
