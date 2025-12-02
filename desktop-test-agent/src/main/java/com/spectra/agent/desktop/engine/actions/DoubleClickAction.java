package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("doubleClick")
public class DoubleClickAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        ctx.client().doubleClick(ctx.step().locator());
    }
}
