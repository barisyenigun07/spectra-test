package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("type")
public class TypeAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        ctx.getDriver().findElement(ctx.by()).sendKeys(ctx.getStep().inputValue());
    }
}
