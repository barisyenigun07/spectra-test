package com.spectra.commons.dto.step;

import java.time.Instant;
import java.util.Map;

public record StepResultDTO(
        Long id,
        String status,
        String message,
        Instant startedAt,
        Instant finishedAt,
        long durationMillis,
        String errorMessage,
        String errorType,
        Map<String, Object> extra
) {}
