package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        String expectedText = SafeConvert.toString(params, "expectedText");

        var element = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        String actualText = element.getText();

        if (!Objects.equals(actualText, expectedText)) {
            throw new AssertionError("Expected: " + expectedText + ", Actual: " + actualText);
        }
    }
}
