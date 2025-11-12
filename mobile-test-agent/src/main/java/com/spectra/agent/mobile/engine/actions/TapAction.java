package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("tap")
public class TapAction implements ActionHandler{
    @Override
    public void handle(ExecutionContext ctx) {
        var el = ctx.driver().findElement(ctx.by());
        el.click();
    }
}
