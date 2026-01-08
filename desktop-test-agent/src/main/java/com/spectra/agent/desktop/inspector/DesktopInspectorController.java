package com.spectra.agent.desktop.inspector;

import com.spectra.agent.desktop.inspector.session.InspectorSessionRegistry;
import com.spectra.commons.dto.inspector.*;
import com.spectra.commons.dto.locator.LocatorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/inspector")
@RequiredArgsConstructor
public class DesktopInspectorController {

    private final InspectorSessionRegistry sessions;
    private final DesktopInspectorService inspector;

    @PostMapping("/sessions")
    public ResponseEntity<InspectorSessionDTO> createSession(@RequestBody InspectorCreateSessionRequest req) {
        if (req == null || req.platform() != InspectorPlatform.DESKTOP) {
            return ResponseEntity.badRequest().build();
        }

        // MVP tek-session: yeni session açmadan önce runtime'ı temizleyip yeniden başlat
        inspector.cleanup();

        // Desktop runtime/driver init burada yapılmalı (Appium veya LDTP)
        inspector.initOrReplace(req.config());

        // Registry session
        InspectorSessionDTO s = sessions.create(req.platform());
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> closeSession(@PathVariable String sessionId) {
        sessions.getOrThrow(sessionId);

        inspector.cleanup();
        sessions.remove(sessionId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sessions/{sessionId}/snapshot")
    public ResponseEntity<InspectorSnapshotDTO> snapshot(
            @PathVariable String sessionId,
            @RequestBody(required = false) InspectorSnapshotRequest req
    ) {
        sessions.getOrThrow(sessionId);

        InspectorSnapshotRequest r = (req == null)
                ? new InspectorSnapshotRequest(8, 40, 1500)
                : req;

        UiNodeDTO root = inspector.snapshotTree(r.maxDepth(), r.maxChildrenPerNode(), r.maxNodes());

        InspectorSnapshotDTO out = new InspectorSnapshotDTO(
                sessionId,
                Instant.now(),
                inspector.pageTitle(),
                inspector.pageUrl(),
                root
        );

        return ResponseEntity.ok(out);
    }

    @GetMapping("/sessions/{sessionId}/nodes/{nodeId}/suggestions")
    public ResponseEntity<InspectorSuggestionsDTO> suggestions(
            @PathVariable String sessionId,
            @PathVariable String nodeId
    ) {
        sessions.getOrThrow(sessionId);

        List<LocatorSuggestionDTO> sug = inspector.suggestionsForNode(nodeId);

        List<InspectorSuggestionsDTO.LocatorCandidateDTO> candidates = sug.stream()
                .map(s -> new InspectorSuggestionsDTO.LocatorCandidateDTO(
                        new LocatorDTO(s.strategy(), s.value()),
                        s.score(),
                        s.note()
                ))
                .sorted(Comparator.comparingDouble((InspectorSuggestionsDTO.LocatorCandidateDTO c) -> c.score()).reversed())
                .toList();

        return ResponseEntity.ok(new InspectorSuggestionsDTO(sessionId, nodeId, candidates));
    }

    @PostMapping("/sessions/{sessionId}/pick")
    public ResponseEntity<InspectorPickResponse> pick(
            @PathVariable String sessionId,
            @RequestBody InspectorPickRequest req
    ) {
        sessions.getOrThrow(sessionId);

        List<LocatorSuggestionDTO> sug = inspector.suggestionsForNode(req.nodeId());
        if (sug.isEmpty()) {
            return ResponseEntity.ok(new InspectorPickResponse(
                    sessionId,
                    req.nodeId(),
                    new LocatorDTO("", ""),
                    List.of()
            ));
        }

        LocatorSuggestionDTO picked = chooseBest(sug, req.preferredStrategy());

        return ResponseEntity.ok(new InspectorPickResponse(
                sessionId,
                req.nodeId(),
                new LocatorDTO(picked.strategy(), picked.value()),
                sug
        ));
    }

    private LocatorSuggestionDTO chooseBest(List<LocatorSuggestionDTO> all, String preferredStrategy) {
        if (preferredStrategy != null && !preferredStrategy.isBlank()) {
            return all.stream()
                    .filter(s -> preferredStrategy.equalsIgnoreCase(s.strategy()))
                    .max(Comparator.comparingDouble(LocatorSuggestionDTO::score))
                    .orElseGet(() -> all.stream()
                            .max(Comparator.comparingDouble(LocatorSuggestionDTO::score))
                            .orElse(all.get(0)));
        }
        return all.stream()
                .max(Comparator.comparingDouble(LocatorSuggestionDTO::score))
                .orElse(all.get(0));
    }
}