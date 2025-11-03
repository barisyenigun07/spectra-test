package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.LocatorResolver;
import com.spectra.agent.web.engine.model.ExecutionContext;
import com.spectra.commons.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NavigateBackAction implements ActionHandler {
    private final LocatorResolver locatorResolver;

    @Override
    public void handle(ExecutionContext ctx, StepDTO step) {
        ctx.driver().navigate().back();
    }
}
