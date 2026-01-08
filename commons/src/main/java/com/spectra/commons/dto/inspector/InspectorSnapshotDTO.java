package com.spectra.commons.dto.inspector;

import java.time.Instant;

public record InspectorSnapshotDTO(
        String sessionId,
        Instant capturedAt,
        String pageTitle,
        String pageUrl,
        UiNodeDTO root
) {}