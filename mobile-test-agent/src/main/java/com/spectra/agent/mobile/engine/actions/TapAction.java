package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Component("tap")
public class TapAction implements ActionHandler{
    @Override
    public void handle(ExecutionContext ctx) {
        var el = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        el.click();
    }
}
