package com.spectra.commons.dto;

import java.util.Map;

public record StepDTO(int orderIndex, String action, LocatorDTO locator, Map<String, Object> params) {
}
