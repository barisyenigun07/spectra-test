package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("type")
public class TypeAction implements ActionHandler{
    @Override
    public void handle(ExecutionContext ctx) {
        ctx.driver().findElement(ctx.by()).sendKeys((String) ctx.step().params().get("text"));
    }
}
