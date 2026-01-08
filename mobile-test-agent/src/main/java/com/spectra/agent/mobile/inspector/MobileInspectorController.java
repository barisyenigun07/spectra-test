package com.spectra.agent.mobile.inspector;

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
public class MobileInspectorController {

    private final InspectorSessionRegistry sessions;
    private final MobileInspectorService inspector;

    @PostMapping("/sessions")
    public ResponseEntity<InspectorSessionDTO> createSession(@RequestBody InspectorCreateSessionRequest req) {
        if (req.platform() != InspectorPlatform.MOBILE) return ResponseEntity.badRequest().build();
        InspectorSessionDTO s = sessions.create(req.platform());
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> closeSession(@PathVariable String sessionId) {
        sessions.getOrThrow(sessionId);
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

        UiNodeDTO root = inspector.getUiTree(r.maxDepth(), r.maxChildrenPerNode(), r.maxNodes());

        return ResponseEntity.ok(new InspectorSnapshotDTO(
                sessionId,
                Instant.now(),
                inspector.getPageTitle(),
                inspector.getPageUrl(),
                root
        ));
    }

    @GetMapping("/sessions/{sessionId}/nodes/{nodeId}/suggestions")
    public ResponseEntity<InspectorSuggestionsDTO> suggestions(
            @PathVariable String sessionId,
            @PathVariable String nodeId
    ) {
        sessions.getOrThrow(sessionId);

        List<LocatorSuggestionDTO> sug = inspector.getSuggestions(nodeId);

        List<InspectorSuggestionsDTO.LocatorCandidateDTO> candidates = sug.stream()
                .map(s -> new InspectorSuggestionsDTO.LocatorCandidateDTO(
                        new LocatorDTO(toLocatorType(s.strategy()), s.value()),
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
            return ResponseEntity.ok(new InspectorPickResponse(sessionId, req.nodeId(), new LocatorDTO("", ""), List.of()));
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
            case "xpath" -> "xpath";
            case "accessibilityid" -> "accessibilityId";
            // Mobile’da cssSelector yok
            default -> strategy;
        };
    }
}