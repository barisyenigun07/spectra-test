package com.spectra.agent.web.engine.context;

import com.spectra.commons.dto.StepDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Map;


@RequiredArgsConstructor
@Getter
public class ExecutionContext {
    private final WebDriver driver;
    private final StepDTO step;
    private final Map<String, String> config;

    public By by() {
        String locatorType = step.locator().type();

        if (locatorType == null) {
            return null;
        }

        return switch (locatorType.toLowerCase()) {
            case "id" -> By.id(step.locator().value());
            case "name" -> By.name(step.locator().value());
            case "classname" -> By.className(step.locator().value());
            case "cssselector" -> By.cssSelector(step.locator().value());
            case "xpath" -> By.xpath(step.locator().value());
            default -> null;
        };
    }
}
