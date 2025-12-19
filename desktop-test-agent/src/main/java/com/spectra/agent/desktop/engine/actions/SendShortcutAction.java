package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("sendShortcut")
public class SendShortcutAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        String shortcut = SafeConvert.toString(params, "shortcut");
        ctx.client().sendShortcut(shortcut);
    }
}
