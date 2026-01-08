package com.spectra.agent.web.inspector;

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
public class WebInspectorController {

    private final InspectorSessionRegistry sessions;
    private final WebInspectorService inspector;
    private final WebInspectorDriverManager driverManager;

    @PostMapping("/sessions")
    public ResponseEntity<InspectorSessionDTO> createSession(@RequestBody InspectorCreateSessionRequest req) {
        if (req.platform() != InspectorPlatform.WEB) {
            return ResponseEntity.badRequest().build();
        }

        // 1) driver yoksa zaten burada init edelim (ve önceki driver’ı kapatsın)
        driverManager.createOrReplace(req.config());

        // 2) sonra cleanup (artık driver var)
        inspector.cleanupInspectorIds();

        InspectorSessionDTO s = sessions.create(req.platform());

        // 3) opsiyonel initialUrl
        if (req.initialUrl() != null && !req.initialUrl().isBlank()) {
            inspector.openUrl(req.initialUrl());
        }

        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> closeSession(@PathVariable String sessionId) {
        sessions.getOrThrow(sessionId);

        // session kapanınca DOM’a bastığımız attribute’ları temizle
        try {inspector.cleanupInspectorIds();} catch (Exception ignored) {}
        driverManager.closeIfPresent();
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
                : new InspectorSnapshotRequest(
                        req.maxDepth() > 0 ? req.maxDepth() : 8,
                        req.maxChildrenPerNode() > 0 ? req.maxChildrenPerNode() : 40,
                        req.maxNodes() > 0 ? req.maxNodes() : 1500
        );

        UiNodeDTO root = inspector.getDomTree(r.maxDepth(), r.maxChildrenPerNode(), r.maxNodes());

        InspectorSnapshotDTO out = new InspectorSnapshotDTO(
                sessionId,
                Instant.now(),
                inspector.getPageTitle(),
                inspector.getPageUrl(),
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

        List<LocatorSuggestionDTO> sug = inspector.getSuggestions(nodeId);

        // LocatorSuggestionDTO -> LocatorDTO candidate’a çevir
        List<InspectorSuggestionsDTO.LocatorCandidateDTO> candidates = sug.stream()
                .map(s -> new InspectorSuggestionsDTO.LocatorCandidateDTO(
                        new LocatorDTO(toLocatorType(s.strategy()), s.value()), // type=strategy, value=locator string
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

        List<LocatorSuggestionDTO> sug = inspector.getSuggestions(req.nodeId());
        if (sug.isEmpty()) {
            return ResponseEntity.ok(new InspectorPickResponse(
                    sessionId,
                    req.nodeId(),
                    new LocatorDTO("", ""), // picked boş
                    List.of()
            ));
        }

        LocatorSuggestionDTO picked = chooseBest(sug, req.preferredStrategy());

        return ResponseEntity.ok(new InspectorPickResponse(
                sessionId,
                req.nodeId(),
                new LocatorDTO(toLocatorType(picked.strategy()), picked.value()),
                sug
        ));
    }

    private LocatorSuggestionDTO chooseBest(List<LocatorSuggestionDTO> all, String preferredStrategy) {
        if (preferredStrategy != null && !preferredStrategy.isBlank()) {
            return all.stream()
                    .filter(s -> preferredStrategy.equalsIgnoreCase(s.strategy()))
                    .max(Comparator.comparingDouble(LocatorSuggestionDTO::score))
                    .orElseGet(() -> all.stream().max(Comparator.comparingDouble(LocatorSuggestionDTO::score)).orElse(all.get(0)));
        }
        return all.stream().max(Comparator.comparingDouble(LocatorSuggestionDTO::score)).orElse(all.get(0));
    }

    private String toLocatorType(String strategy) {
        if (strategy == null) return "";
        return switch (strategy.toLowerCase()) {
            case "id" -> "id";
            case "css" -> "cssSelector";
            case "xpath" -> "xpath";
            default -> strategy; // fallback
        };
    }
}