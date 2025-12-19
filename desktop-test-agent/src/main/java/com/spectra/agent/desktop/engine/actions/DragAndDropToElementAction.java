package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.dto.locator.LocatorDTO;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("dragAndDropToElement")
public class DragAndDropToElementAction implements ActionHandler{
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        LocatorDTO targetElementLocator = SafeConvert.toLocator(params, "targetElementLocator");
        ctx.client().dragAndDropToElement(ctx.step().locator(), targetElementLocator);
    }
}
