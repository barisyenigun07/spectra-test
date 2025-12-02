package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("type")
public class TypeAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        ctx.client().type(ctx.step().locator(), (String) ctx.step().params().get("text"));
    }
}
