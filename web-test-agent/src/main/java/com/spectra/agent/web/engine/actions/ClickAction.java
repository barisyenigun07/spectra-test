package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.WebDriverFactory;
import com.spectra.agent.web.engine.context.ExecutionContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component("click")
public class ClickAction implements ActionHandler{

    @Override
    public void handle(ExecutionContext ctx) {
        WebElement el = ctx.driverWait().until(ExpectedConditions.visibilityOfElementLocated(ctx.by()));
        el.click();
    }
}
