package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.LocatorResolver;
import com.spectra.agent.web.engine.model.ExecutionContext;
import com.spectra.commons.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickAction implements ActionHandler{
    private final LocatorResolver locatorResolver;

    @Override
    public void handle(ExecutionContext ctx, StepDTO step) {
        By by = locatorResolver.resolve(ctx.step().locator());
        ctx.driver().findElement(by).click();
    }
}
