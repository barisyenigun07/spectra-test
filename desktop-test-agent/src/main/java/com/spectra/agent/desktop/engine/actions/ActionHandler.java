package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;


public interface ActionHandler {
    void handle(ExecutionContext ctx);
}
