package com.spectra.agent.desktop.engine.client.windows;

import com.spectra.agent.desktop.engine.client.AbstractAppiumDriverClient;
import com.spectra.commons.dto.locator.LocatorDTO;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.By;

public class WindowsDesktopClient extends AbstractAppiumDriverClient<WindowsDriver> {
    public WindowsDesktopClient(WindowsDriver driver) {
        super(driver);
    }


    @Override
    protected By resolve(LocatorDTO locator) {
        return switch (locator.type()) {
            case "automationId" -> AppiumBy.id(locator.value());
            case "name" -> AppiumBy.name(locator.value());
            case "className" -> AppiumBy.className(locator.value());
            case "accessibilityId" -> AppiumBy.accessibilityId(locator.value());
            case "xpath" -> AppiumBy.xpath(locator.value());
            default -> throw new IllegalArgumentException("Unsupported locator type!");
        };
    }
}
