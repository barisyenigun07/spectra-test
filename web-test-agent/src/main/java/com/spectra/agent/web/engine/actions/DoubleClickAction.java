package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Component("doubleClick")
public class DoubleClickAction implements ActionHandler {

    @Override
    public void handle(ExecutionContext ctx) {
        new Actions(ctx.driver())
                .doubleClick(ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by())))
                .perform();
    }
}
