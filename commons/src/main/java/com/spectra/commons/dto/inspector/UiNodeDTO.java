package com.spectra.commons.dto.inspector;

import java.util.List;
import java.util.Map;

public record UiNodeDTO(
        String nodeId,
        String type,                 // tagName
        String name,                 // textContent kısaltılmış
        Map<String, Object> attrs,   // id, class, data-testid...
        List<UiNodeDTO> children
) {}