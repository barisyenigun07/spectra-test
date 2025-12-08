package com.spectra.agent.web.engine.context;

import com.spectra.commons.dto.step.StepDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


public record ExecutionContext(WebDriver driver, StepDTO step, WebDriverWait driverWait) {
    public By by() {
        String locatorType = step.locator().type();
        String locatorValue = step.locator().value();

        if (locatorType == null) {
            return null;
        }

        return switch (locatorType.toLowerCase()) {
            case "id" -> By.id(locatorValue);
            case "name" -> By.name(locatorValue);
            case "classname" -> By.className(locatorValue);
            case "cssselector" -> By.cssSelector(locatorValue);
            case "xpath" -> By.xpath(locatorValue);
            default -> null;
        };

    }
}
