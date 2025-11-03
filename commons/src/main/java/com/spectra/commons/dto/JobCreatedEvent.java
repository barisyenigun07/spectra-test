package com.spectra.commons.dto;

import java.util.List;
import java.util.Map;

public record JobCreatedEvent(Long jobId, String targetPlatform, List<StepDTO> steps, Map<String, String> config) {
}
