package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;



@Component("click")
public class ClickAction implements ActionHandler{

    @Override
    public void handle(ExecutionContext ctx) {
        WebElement el = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        el.click();
    }
}
