package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;


@Component("moveMouseToElement")
public class MoveMouseToElementAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        ctx.client().moveMouseToElement(ctx.step().locator());
    }
}
