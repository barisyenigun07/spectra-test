package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        WebElement element = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        Map<String, Object> params = ctx.step().params();
        String text = SafeConvert.toString(params,"text", null);

        if (element.getText().isBlank()) {
            throw new AssertionError("Text not found");
        }

        else if (!element.getText().equals(text)) {
            throw new AssertionError("Texts not matched!");
        }
    }
}
