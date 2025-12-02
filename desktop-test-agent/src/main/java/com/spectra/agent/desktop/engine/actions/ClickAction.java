package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("click")
public class ClickAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        DesktopClient client = ctx.client();
        client.click(ctx.step().locator());
    }
}
