package com.spectra.agent.mobile.engine.context;

import com.spectra.commons.dto.step.StepDTO;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

public record ExecutionContext(AppiumDriver driver, StepDTO step, WebDriverWait driverWait) {
    public By by() {
        String locatorType = step.locator().type();
        String locatorValue = step.locator().value();

        return switch (locatorType) {
            case "id" -> AppiumBy.id(locatorValue);
            case "name" -> AppiumBy.name(locatorValue);
            case "className" -> AppiumBy.className(locatorValue);
            case "cssSelector" -> AppiumBy.cssSelector(locatorValue);
            case "accessibilityId" -> AppiumBy.accessibilityId(locatorValue);
            default -> throw new IllegalArgumentException("Unsupported locator type!");
        };
    }
}
