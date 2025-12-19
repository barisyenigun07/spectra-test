package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("assertIsChecked")
public class AssertIsCheckedAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        boolean isChecked = ctx.client().isChecked(ctx.step().locator());
        if (!isChecked) throw new AssertionError("Element is not checked!");
    }
}
