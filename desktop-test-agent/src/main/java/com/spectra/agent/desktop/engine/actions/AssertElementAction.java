package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("assertElement")
public class AssertElementAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        boolean isElementVisible = ctx.client().isVisible(ctx.step().locator());
        if (!isElementVisible) {
            throw new AssertionError("Elemet is not visible!");
        }
    }
}
