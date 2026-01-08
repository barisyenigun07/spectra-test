package com.spectra.agent.desktop.engine.client.macos;

import com.spectra.agent.desktop.engine.client.AbstractAppiumDriverClient;
import com.spectra.commons.dto.locator.LocatorDTO;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.mac.Mac2Driver;
import org.openqa.selenium.*;


public class MacOSDesktopClient extends AbstractAppiumDriverClient<Mac2Driver> {
    public MacOSDesktopClient(Mac2Driver driver) {
        super(driver);
    }

    @Override
    protected By resolve(LocatorDTO locator) {
        return switch (locator.type()) {
            case "id" -> AppiumBy.id(locator.value());
            case "name" -> AppiumBy.name(locator.value());
            case "className" -> AppiumBy.className(locator.value());
            case "accessibilityId" -> AppiumBy.accessibilityId(locator.value());
            case "xpath" -> AppiumBy.xpath(locator.value());
            case "iOSClassChain" -> AppiumBy.iOSClassChain(locator.value());
            case "iOSNsPredicateString" -> AppiumBy.iOSNsPredicateString(locator.value());
            default -> throw new IllegalArgumentException("Unsupported locator type!");
        };
    }

    @Override
    public AppiumDriver getDriver() {
        return driver;
    }
}
