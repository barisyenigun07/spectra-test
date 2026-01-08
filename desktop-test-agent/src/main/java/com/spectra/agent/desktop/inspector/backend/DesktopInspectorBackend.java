package com.spectra.agent.desktop.inspector.backend;

import com.spectra.commons.dto.inspector.UiNodeDTO;
import com.spectra.commons.dto.inspector.LocatorSuggestionDTO;

import java.util.List;

public interface DesktopInspectorBackend {

    UiNodeDTO snapshotTree(int maxDepth, int maxChildrenPerNode, int maxNodes);

    List<LocatorSuggestionDTO> suggestionsForNode(String nodeId);

    default String pageTitle() { return ""; }

    default String pageUrl() { return ""; }

    default void cleanup() {
        // desktop'ta web'deki gibi DOM attribute temizliği yok.
    }
}