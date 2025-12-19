package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Component("assertElement")
public class AssertElementAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        WebElement el = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));

        if (!el.isDisplayed()) {
            throw new AssertionError("Element not found!");
        }
    }
}
