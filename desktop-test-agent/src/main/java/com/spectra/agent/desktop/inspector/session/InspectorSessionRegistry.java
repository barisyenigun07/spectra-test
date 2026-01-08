package com.spectra.agent.desktop.inspector.session;

import com.spectra.commons.dto.inspector.InspectorPlatform;
import com.spectra.commons.dto.inspector.InspectorSessionDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InspectorSessionRegistry {

    private final Map<String, InspectorSessionDTO> sessions = new ConcurrentHashMap<>();

    public InspectorSessionDTO create(InspectorPlatform platform) {
        String id = UUID.randomUUID().toString();
        InspectorSessionDTO dto = new InspectorSessionDTO(id, platform, Instant.now());
        sessions.put(id, dto);
        return dto;
    }

    public InspectorSessionDTO getOrThrow(String sessionId) {
        InspectorSessionDTO s = sessions.get(sessionId);
        if (s == null) throw new IllegalArgumentException("Inspector session not found: " + sessionId);
        return s;
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}
