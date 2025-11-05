package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        WebElement element = ctx.getDriver().findElement(ctx.by());

        if (element.getText().isBlank()) {
            throw new AssertionError("Text not found");
        }

        else if (!element.getText().equals(ctx.getStep().inputValue())) {
            throw new AssertionError("Texts not matched!");
        }
    }
}
