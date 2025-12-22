package com.spectra.agent.web.engine.context;

import com.spectra.commons.dto.step.StepDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


public record ExecutionContext(WebDriver driver, StepDTO step, WebDriverWait driverWait) {
    public By by() {
        if (step.locator() == null) {
            throw new IllegalArgumentException("Locator is required for action: " + step.action());
        }

        String locatorType = step.locator().type();
        String locatorValue = step.locator().value();

        if (locatorType == null || locatorType.isBlank()) {
            throw new IllegalArgumentException("Locator type is blank for action: " + step.action());
        }
        if (locatorValue == null || locatorValue.isBlank()) {
            throw new IllegalArgumentException("Locator value is blank for type: " + locatorType);
        }

        return switch (locatorType) {
            case "id" -> By.id(locatorValue);
            case "name" -> By.name(locatorValue);
            case "className" -> By.className(locatorValue);
            case "cssSelector" -> By.cssSelector(locatorValue);
            case "xpath" -> By.xpath(locatorValue);
            case "linkText" -> By.linkText(locatorValue);
            case "partialLinkText" -> By.partialLinkText(locatorValue);
            case "tagName" -> By.tagName(locatorValue);
            default -> throw new IllegalArgumentException("Unsupported locator type!");
        };

    }
}
