package com.spectra.commons.dto;

import java.util.List;

public record JobCreatedEvent(Long jobId, String target, List<StepDTO> steps) {
}
