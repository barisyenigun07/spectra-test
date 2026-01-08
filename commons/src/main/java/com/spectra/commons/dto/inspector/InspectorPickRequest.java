package com.spectra.commons.dto.inspector;

public record InspectorPickRequest(
        String nodeId,
        String preferredStrategy // "id" | "css" | "xpath" | null
) {}