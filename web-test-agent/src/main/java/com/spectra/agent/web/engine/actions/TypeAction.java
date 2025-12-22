package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("type")
public class TypeAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        WebElement el = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        el.sendKeys(SafeConvert.toString(params, "text", ""));
    }
}
