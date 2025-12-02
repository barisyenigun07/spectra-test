package com.spectra.agent.desktop.engine.actions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ActionsRegistry {
    private final Map<String, ActionHandler> handlers;

    public ActionHandler resolveHandler(String action) {
        var handler = handlers.get(action);
        if (handler == null) throw new IllegalArgumentException("Unsupported action type!");
        return handler;
    }
}
