package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("navigateBack")
public class NavigateBackAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        ctx.driver().navigate().back();
    }
}
