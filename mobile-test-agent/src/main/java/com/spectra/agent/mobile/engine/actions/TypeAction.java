package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("type")
public class TypeAction implements ActionHandler{
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        String text = SafeConvert.toString(params, "text", "");
        ctx.driver().findElement(ctx.by()).sendKeys(text);
    }
}
