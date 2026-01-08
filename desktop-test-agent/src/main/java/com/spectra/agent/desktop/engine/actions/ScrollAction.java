package com.spectra.agent.desktop.engine.actions;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.context.ExecutionContext;
import com.spectra.commons.dto.locator.LocatorDTO;
import com.spectra.commons.util.SafeConvert;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("scroll")
public class ScrollAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();
        String direction = SafeConvert.toString(params, "direction");
        int amount = SafeConvert.toInt(params, "amount", 1);
        LocatorDTO target = ctx.step().locator();

        DesktopClient client = ctx.client();

        switch (direction) {
            case "up" -> client.scrollUp(target, amount);
            case "down" -> client.scrollDown(target, amount);
            case "left" -> client.scrollLeft(target, amount);
            case "right" -> client.scrollRight(target, amount);
            default -> throw new IllegalArgumentException("Direction invalid: " + direction);
        }
    }
}
