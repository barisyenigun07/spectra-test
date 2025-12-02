package com.spectra.agent.desktop.engine.client.windows;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.commons.dto.LocatorDTO;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.windows.WindowsDriver;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

@RequiredArgsConstructor
public class WindowsDesktopClient implements DesktopClient {
    private final WindowsDriver driver;

    @Override
    public void click(LocatorDTO locator) {
        driver.findElement(resolve(locator)).click();
    }

    @Override
    public void doubleClick(LocatorDTO locator) {
        new Actions(driver)
                .doubleClick(driver.findElement(resolve(locator)))
                .perform();
    }

    @Override
    public void type(LocatorDTO locator, String text) {
        driver.findElement(resolve(locator)).sendKeys(text);
    }

    @Override
    public void sendShortcut(String shortcut) {
        new Actions(driver)
                .sendKeys(Keys.CONTROL + shortcut)
                .perform();
    }

    @Override
    public String getText(LocatorDTO locator) {
        return driver.findElement(resolve(locator)).getText();
    }

    @Override
    public boolean isVisible(LocatorDTO locator) {
        Dimension size = driver.findElement(resolve(locator)).getSize();
        return size.width > 0 && size.height > 0;
    }

    private By resolve(LocatorDTO locator) {
        return switch (locator.type()) {
            case "id" -> AppiumBy.id(locator.value());
            case "name" -> AppiumBy.name(locator.value());
            case "className" -> AppiumBy.className(locator.value());
            case "accessibilityId" -> AppiumBy.accessibilityId(locator.value());
            default -> throw new IllegalArgumentException("Unsupported locator type!");
        };
    }
}
