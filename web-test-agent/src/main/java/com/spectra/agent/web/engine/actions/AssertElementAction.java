package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Component("assertElement")
public class AssertElementAction implements ActionHandler{

    @Override
    public void handle(ExecutionContext ctx) {
        WebElement element = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));

        if (element.getSize().width == 0 || element.getSize().height == 0) {
            throw new AssertionError("Element not found!");
        }
    }
}
