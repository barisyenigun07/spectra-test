package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("moveMouseByOffset")
public class MoveMouseByOffsetAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        int xOffset = SafeConvert.toInt(params, "xOffset", 0);
        int yOffset = SafeConvert.toInt(params, "yOffset", 0);
        ctx.client().moveMouseByOffset(xOffset, yOffset);
    }
}
