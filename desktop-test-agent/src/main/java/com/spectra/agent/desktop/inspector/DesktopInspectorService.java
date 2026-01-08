package com.spectra.agent.desktop.inspector;

import com.spectra.agent.desktop.inspector.backend.AppiumDesktopInspectorBackend;
import com.spectra.agent.desktop.inspector.backend.DesktopInspectorBackend;
import com.spectra.agent.desktop.inspector.backend.LdtpDesktopInspectorBackend;
import com.spectra.commons.dto.inspector.LocatorSuggestionDTO;
import com.spectra.commons.dto.inspector.UiNodeDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DesktopInspectorService {

    private final DesktopInspectorRuntimeManager runtime;
    private final AppiumDesktopInspectorBackend appiumBackend;
    private final LdtpDesktopInspectorBackend ldtpBackend;

    public DesktopInspectorService(
            DesktopInspectorRuntimeManager runtime,
            AppiumDesktopInspectorBackend appiumBackend,
            LdtpDesktopInspectorBackend ldtpBackend
    ) {
        this.runtime = runtime;
        this.appiumBackend = appiumBackend;
        this.ldtpBackend = ldtpBackend;
    }

    public void initOrReplace(Map<String, Object> config) {
        runtime.createOrReplace(config);
    }

    public void cleanup() {
        runtime.closeIfPresent();
    }

    private DesktopInspectorBackend backend() {
        DesktopInspectorRuntimeManager.BackendKind kind = runtime.getOrThrow().kind();
        return switch (kind) {
            case APPIUM -> appiumBackend;
            case LDTP -> ldtpBackend;
        };
    }

    public UiNodeDTO snapshotTree(int maxDepth, int maxChildren, int maxNodes) {
        return backend().snapshotTree(maxDepth, maxChildren, maxNodes);
    }

    public List<LocatorSuggestionDTO> suggestionsForNode(String nodeId) {
        return backend().suggestionsForNode(nodeId);
    }

    public String pageTitle() {
        return backend().pageTitle();
    }

    public String pageUrl() {
        return backend().pageUrl();
    }
}