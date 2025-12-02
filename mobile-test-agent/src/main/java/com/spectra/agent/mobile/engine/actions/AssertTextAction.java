package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        String expectedText = (String) params.get("expectedText");

        var element = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        String elementText = element.getText();

        if (!elementText.equals(expectedText)) {
            throw new AssertionError("Texts not matched!");
        }
    }
}
