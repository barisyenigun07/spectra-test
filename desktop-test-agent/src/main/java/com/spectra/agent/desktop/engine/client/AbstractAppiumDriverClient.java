package com.spectra.agent.desktop.engine.client;

import com.spectra.commons.dto.locator.LocatorDTO;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class AbstractAppiumDriverClient<D extends AppiumDriver> implements DesktopClient {
    protected final D driver;
    protected final WebDriverWait wait;

    protected AbstractAppiumDriverClient(D driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Override
    public void click(LocatorDTO locator) {
        find(locator).click();
    }

    @Override
    public void doubleClick(LocatorDTO locator) {
        new Actions(driver)
                .doubleClick(find(locator))
                .perform();
    }

    @Override
    public void type(LocatorDTO locator, String text) {
        find(locator).sendKeys(text);
    }

    @Override
    public void sendShortcut(String shortcut) {
        String[] parts = shortcut.split("\\+");
        if (parts.length == 0) return;

        Actions actions = new Actions(driver);

        for (int i = 0; i < parts.length - 1; i++) {
            Keys key = mapShortcutKey(parts[i].trim());
            actions.keyDown(key);
        }

        Keys lastKey = mapShortcutKey(parts[parts.length - 1].trim());
        actions.sendKeys(lastKey);

        for (int i = parts.length - 2; i >= 0; i--) {
            Keys key = mapShortcutKey(parts[i].trim());
            actions.keyUp(key);
        }

        actions.perform();
    }

    @Override
    public String getText(LocatorDTO locator) {
        return find(locator).getText();
    }

    @Override
    public boolean isVisible(LocatorDTO locator) {
        try {
            WebElement el = find(locator);
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void moveMouseToElement(LocatorDTO locator) {
        WebElement el = find(locator);
        new Actions(driver)
                .moveToElement(el)
                .perform();
    }

    @Override
    public void moveMouseByOffset(int xOffset, int yOffset) {
        new Actions(driver)
                .moveByOffset(xOffset, yOffset)
                .perform();
    }

    @Override
    public void moveMouseToLocation(int x, int y) {
        new Actions(driver)
                .moveToLocation(x, y)
                .perform();
    }

    @Override
    public void dragAndDropToElement(LocatorDTO from, LocatorDTO to) {
        WebElement fromEl = find(from);
        WebElement toEl = find(to);

        new Actions(driver)
                .dragAndDrop(fromEl, toEl)
                .perform();
    }

    @Override
    public void dragAndDropByOffset(LocatorDTO el, int xOffset, int yOffset) {
        WebElement fromEl = driver.findElement(resolve(el));

        new Actions(driver)
                .dragAndDropBy(fromEl, xOffset, yOffset)
                .perform();
    }

    @Override
    public void dragAndDropToLocation(LocatorDTO el, int x, int y) {
        WebElement fromEl = find(el);
        Point origin = fromEl.getLocation();

        int offsetX = x - origin.getX();
        int offsetY = y - origin.getY();

        new Actions(driver)
                .dragAndDropBy(fromEl, offsetX, offsetY)
                .perform();
    }

    @Override
    public boolean isChecked(LocatorDTO locator) {
        WebElement el = find(locator);

        String checked = el.getAttribute("checked");
        if (checked != null) {
            return checked.equalsIgnoreCase("true") || checked.equals("1");
        }

        String value = el.getAttribute("value");
        if (value != null) {
            return value.equals("1") || value.equalsIgnoreCase("true");
        }

        String toggle = el.getAttribute("Toggle.ToggleState");
        if (toggle != null) {
            return toggle.equals("1") || toggle.equalsIgnoreCase("true");
        }

        return false;
    }

    @Override
    public void scrollUp(LocatorDTO locator, int amount) {
        WebElement el = find(locator);
        int n = Math.max(1, amount);

        for (int i = 0; i < n; i++) {
            new Actions(driver)
                    .moveToElement(el)
                    .scrollByAmount(0, -300)
                    .perform();
        }
    }

    @Override
    public void scrollDown(LocatorDTO locator, int amount) {
        WebElement el = find(locator);
        int n = Math.max(1, amount);

        for (int i = 0; i < n; i++) {
            new Actions(driver)
                    .moveToElement(el)
                    .scrollByAmount(0, 300)
                    .perform();
        }
    }

    @Override
    public void scrollLeft(LocatorDTO locator, int amount) {
        WebElement el = find(locator);
        int n = Math.max(1, amount);

        for (int i = 0; i < n; i++) {
            new Actions(driver)
                    .moveToElement(el)
                    .scrollByAmount(-300, 0)
                    .perform();
        }
    }

    @Override
    public void scrollRight(LocatorDTO locator, int amount) {
        WebElement el = find(locator);
        int n = Math.max(1, amount);

        for (int i = 0; i < n; i++) {
            new Actions(driver)
                    .moveToElement(el)
                    .scrollByAmount(300, 0)
                    .perform();
        }
    }

    @Override
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebElement find(LocatorDTO locator) {
        By by = resolve(locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected WebElement findPresent(LocatorDTO locator) {
        By by = resolve(locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected Keys mapShortcutKey(String token) {
        String t = token.toUpperCase();

        return switch (t) {
            case "CMD", "COMMAND", "META" -> Keys.META;
            case "CTRL", "CONTROL" -> Keys.CONTROL;
            case "ALT", "OPTION" -> Keys.ALT;
            case "SHIFT" -> Keys.SHIFT;
            case "ENTER", "RETURN" -> Keys.ENTER;
            case "ESC", "ESCAPE" -> Keys.ESCAPE;
            case "TAB" -> Keys.TAB;
            case "SPACE" -> Keys.SPACE;
            case "BACKSPACE" -> Keys.BACK_SPACE;
            case "DELETE", "DEL" -> Keys.DELETE;
            case "UP" -> Keys.ARROW_UP;
            case "DOWN" -> Keys.ARROW_DOWN;
            case "LEFT" -> Keys.ARROW_LEFT;
            case "RIGHT" -> Keys.ARROW_RIGHT;
            default -> {
                if (t.length() == 1) {
                    yield Keys.getKeyFromUnicode(t.charAt(0));
                }

                if (t.matches("^F\\d{1,2}$")) {
                    yield Keys.valueOf(t);
                }

                yield Keys.getKeyFromUnicode(t.charAt(0));
            }
        };
    }

    protected abstract By resolve(LocatorDTO locator);
}
