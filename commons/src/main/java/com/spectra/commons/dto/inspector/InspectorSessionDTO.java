package com.spectra.commons.dto.inspector;

import java.time.Instant;

public record InspectorSessionDTO(
        String sessionId,
        InspectorPlatform platform,
        Instant createdAt
) {}