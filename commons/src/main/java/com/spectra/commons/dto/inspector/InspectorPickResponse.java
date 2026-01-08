package com.spectra.commons.dto.inspector;

import com.spectra.commons.dto.locator.LocatorDTO;

import java.util.List;

public record InspectorPickResponse(
        String sessionId,
        String nodeId,
        LocatorDTO picked,
        List<LocatorSuggestionDTO> allSuggestions
) {}