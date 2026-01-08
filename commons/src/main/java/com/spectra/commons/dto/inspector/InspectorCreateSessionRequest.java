package com.spectra.commons.dto.inspector;

import java.util.Map;

public record InspectorCreateSessionRequest(
        InspectorPlatform platform,
        Map<String, Object> config, // driver config (web için url vs. ekleyebilirsin)
        String initialUrl           // web için opsiyonel
) {}
