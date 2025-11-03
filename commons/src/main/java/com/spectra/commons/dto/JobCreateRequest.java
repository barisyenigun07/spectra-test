package com.spectra.commons.dto;

import java.util.List;
import java.util.Map;

public record JobCreateRequest(String targetPlatform, List<StepDTO> steps, Map<String, String> config) {
}
