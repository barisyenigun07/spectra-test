package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Component("type")
public class TypeAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        WebElement el = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        el.sendKeys((String) ctx.step().params().get("text"));
    }
}
