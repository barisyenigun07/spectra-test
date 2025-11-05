package com.spectra.agent.web.engine;

import com.spectra.commons.dto.LocatorDTO;
import org.openqa.selenium.By;


public class LocatorResolver {
    public By resolve(LocatorDTO locator) {
        String locatorType = locator.type();

        return switch (locatorType) {
            case "id" -> By.id(locator.value());
            case "name" -> By.name(locator.value());
            case "className" -> By.className(locator.value());
            case "cssSelector" -> By.cssSelector(locator.value());
            case "xpath" -> By.xpath(locator.value());
            default -> throw new IllegalArgumentException("Unknown locator type: " + locatorType);
        };
    }
}
