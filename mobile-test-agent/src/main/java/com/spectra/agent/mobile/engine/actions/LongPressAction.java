package com.spectra.agent.mobile.engine.actions;

import com.google.common.collect.ImmutableMap;
import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Component("longPress")
public class LongPressAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        var element = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));

        ((JavascriptExecutor) ctx.driver()).executeScript("mobile: longClickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(), "duration", 500
        ));
    }
}
