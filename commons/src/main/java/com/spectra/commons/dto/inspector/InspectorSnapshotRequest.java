package com.spectra.commons.dto.inspector;

public record InspectorSnapshotRequest(
        int maxDepth,
        int maxChildrenPerNode,
        int maxNodes
) {
    public InspectorSnapshotRequest {
        // default değer setlemek istersen client’ta da yaparsın, burada koruyalım:
        if (maxDepth <= 0) maxDepth = 8;
        if (maxChildrenPerNode <= 0) maxChildrenPerNode = 40;
        if (maxNodes <= 0) maxNodes = 1500;
    }
}