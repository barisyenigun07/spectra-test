package com.spectra.agent.mobile.engine.context;

import com.spectra.commons.dto.step.StepDTO;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

public record ExecutionContext(AppiumDriver driver, StepDTO step, WebDriverWait driverWait) {
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
            case "id" -> AppiumBy.id(locatorValue);
            case "name" -> AppiumBy.name(locatorValue);
            case "className" -> AppiumBy.className(locatorValue);
            case "cssSelector" -> AppiumBy.cssSelector(locatorValue);
            case "accessibilityId" -> AppiumBy.accessibilityId(locatorValue);
            case "xpath" -> AppiumBy.xpath(locatorValue);
            case "androidUIAutomator" -> AppiumBy.androidUIAutomator(locatorValue);
            case "iOSClassChain" -> AppiumBy.iOSClassChain(locatorValue);
            case "iOSNsPredicateString" -> AppiumBy.iOSNsPredicateString(locatorValue);
            default -> throw new IllegalArgumentException("Unsupported locator type!");
        };
    }
}
