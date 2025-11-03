package com.spectra.agent.web.engine;

import com.spectra.commons.dto.LocatorDTO;
import com.spectra.commons.dto.StepDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class StepExecutor {
    private final WebDriver driver;

    public StepExecutor(WebDriver driver) {
        this.driver = driver;
    }

    private By resolveLocator(LocatorDTO locator) {
        return switch (locator.type()) {
            case "id" -> By.id(locator.value());
            case "name" -> By.name(locator.value());
            case "css" -> By.cssSelector(locator.value());
            case "className" -> By.className(locator.value());
            case "xpath" -> By.xpath(locator.value());
            default -> throw new IllegalArgumentException("Invalid locator type!");
        };
    }

    public void executeStep(StepDTO step) {
        String action = step.action().toLowerCase();
        boolean needsElement = switch (action) {
            case "click", "doubleclick", "type", "assertelement", "aserttext", "rightclick", "hover",
                 "clear", "selectbyvisibletext", "draganddrop", "scrollintoview"-> true;
            default -> false;
        };

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);
        WebElement element = null;

        if (needsElement) {
            By by = resolveLocator(step.locator());
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        }

        switch (action) {
            case "openurl":
                driver.get(required(step.inputValue(), "inputValue (url)"));
                break;
            case "navigateforward":
                driver.navigate().forward();
                break;
            case "navigateback":
                driver.navigate().back();
                break;
            case "navigatetourl":
                driver.navigate().to(step.inputValue());
                break;
            case "refresh":
                driver.navigate().refresh();
                break;
            case "click":
                element.click();
                break;
            case "doubleclick":
                actions.doubleClick(element).perform();
                break;
            case "type":
                element.sendKeys(step.inputValue());
                break;
            case "asserttext":
                if (!element.getText().equals(step.inputValue())) {
                    throw new AssertionError("Text mismatch: Expected: " + step.inputValue() + " Found: " + element.getText());
                }
                break;
            case "assertelement":
                if (element.getSize().width == 0 || element.getSize().height == 0) {
                    throw new AssertionError("Element not found!");
                }
                break;
            default:
                System.out.println("Improper action type!");
        }
    }

    private static String required(String val, String name) {
        if (val == null || val.isBlank()) throw new IllegalArgumentException("Missing " + name);
        return val;
    }
}
