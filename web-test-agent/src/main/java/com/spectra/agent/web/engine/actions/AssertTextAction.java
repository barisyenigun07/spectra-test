package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        WebElement element = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        String actualText = element.getText();

        Map<String, Object> params = ctx.step().params();
        String expectedText = SafeConvert.toString(params,"text", null);

        if (actualText.isBlank()) {
            throw new AssertionError("Text not found!");
        }

        else if (!Objects.equals(actualText, expectedText)) {
            throw new AssertionError("Expected: " + expectedText + ", Actual: " + actualText);
        }
    }
}
