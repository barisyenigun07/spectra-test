package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component("assertText")
public class AssertTextAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        DesktopClient client = ctx.client();
        String actualText = normalizeUiText(client.getText(ctx.step().locator()));

        Map<String, Object> params = ctx.step().params();
        String expectedText = normalizeUiText(SafeConvert.toString(params, "expectedText"));

        if (!Objects.equals(actualText, expectedText)) {
            throw new AssertionError("Expected: " + expectedText + ", Actual: " + actualText);
        }
    }

    private String normalizeUiText(String text) {
        if (text == null) return null;
        String s = text.trim()
                .replaceAll("\\p{Cf}+", "")
                .replace('\u00A0', ' ');
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }
}
