package com.spectra.agent.desktop.inspector.backend;

import com.spectra.agent.desktop.inspector.runtime.LdtpFacade;
import com.spectra.commons.dto.inspector.LocatorSuggestionDTO;
import com.spectra.commons.dto.inspector.UiNodeDTO;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LdtpDesktopInspectorBackend implements DesktopInspectorBackend {

    private final LdtpFacade ldtp;

    public LdtpDesktopInspectorBackend(LdtpFacade ldtp) {
        this.ldtp = ldtp;
    }

    @Override
    public UiNodeDTO snapshotTree(int maxDepth, int maxChildrenPerNode, int maxNodes) {
        // MVP: LDTP gerçek hiyerarşi vermeyebilir -> flat list'i root altında children olarak göstereceğiz.
        List<LdtpFacade.LdtpObject> objs = safeList(ldtp.listObjects());

        Map<String, Object> rootAttrs = new LinkedHashMap<>();
        rootAttrs.put("windowTitle", safe(ldtp.activeWindowTitle()));
        rootAttrs.put("backend", "LDTP");
        rootAttrs.put("note", "MVP: LDTP tree is minimal (flat object list).");

        List<UiNodeDTO> children = new ArrayList<>();
        int limit = Math.min(objs.size(), Math.max(1, maxNodes));

        for (int i = 0; i < limit; i++) {
            LdtpFacade.LdtpObject o = objs.get(i);

            Map<String, Object> attrs = new LinkedHashMap<>();
            attrs.put("id", o.id());
            attrs.put("name", o.name());
            attrs.put("role", o.role());

            children.add(new UiNodeDTO(
                    "ldtp:" + o.id(),               // nodeId
                    safe(o.role(), "object"),       // type
                    safe(o.name(), ""),             // name
                    attrs,
                    List.of()
            ));
        }

        return new UiNodeDTO("ldtp:root", "root", "", rootAttrs, children);
    }

    @Override
    public List<LocatorSuggestionDTO> suggestionsForNode(String nodeId) {
        if (nodeId == null || nodeId.isBlank()) return List.of();

        String value = nodeId.startsWith("ldtp:") ? nodeId.substring("ldtp:".length()) : nodeId;

        return List.of(
                new LocatorSuggestionDTO("ldtp", value, 0.9, "LDTP locator (value-only)")
        );
    }

    private static <T> List<T> safeList(List<T> xs) {
        return xs == null ? List.of() : xs;
    }

    private static String safe(String s) { return s == null ? "" : s; }
    private static String safe(String s, String def) { return (s == null || s.isBlank()) ? def : s; }
}