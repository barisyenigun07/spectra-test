package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("moveMouseToLocation")
public class MoveMouseToLocationAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        int x = SafeConvert.toInt(params, "x", 0);
        int y = SafeConvert.toInt(params, "y", 0);
        ctx.client().moveMouseToLocation(x, y);
    }
}
