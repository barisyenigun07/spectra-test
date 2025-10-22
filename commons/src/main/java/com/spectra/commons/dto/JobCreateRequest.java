package com.spectra.commons.dto;

import java.util.List;

public record JobCreateRequest(String targetPlatform, List<StepDTO> steps) {
}
