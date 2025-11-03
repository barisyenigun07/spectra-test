package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.model.ExecutionContext;
import com.spectra.commons.dto.StepDTO;

public interface ActionHandler {
    void handle(ExecutionContext ctx, StepDTO step);
}
