package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import org.springframework.stereotype.Component;

@Component("openUrl")
public class OpenUrlAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        String url = (String) ctx.step().params().get("url");
        ctx.driver().get(url);
    }
}
