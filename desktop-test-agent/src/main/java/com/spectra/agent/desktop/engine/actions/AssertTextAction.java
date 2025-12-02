package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        DesktopClient client = ctx.client();
        String actualText = client.getText(ctx.step().locator());

        Map<String, Object> params = ctx.step().params();
        String expectedText = (String) params.get("text");

        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Texts not matched!");
        }
    }
}
