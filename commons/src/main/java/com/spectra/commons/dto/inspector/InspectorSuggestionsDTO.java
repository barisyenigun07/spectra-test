package com.spectra.commons.dto.inspector;

import com.spectra.commons.dto.locator.LocatorDTO;

import java.util.List;

public record InspectorSuggestionsDTO(
        String sessionId,
        String nodeId,
        List<LocatorCandidateDTO> candidates
) {
    public record LocatorCandidateDTO(
            LocatorDTO locator,
            double score,
            String note
    ) {}
}