package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import com.spectra.commons.util.SafeConvert;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Component("swipeUntilVisible")
public class SwipeUntilVisibleAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        AppiumDriver driver = ctx.driver();
        Map<String, Object> params = ctx.step().params();

        String direction = SafeConvert.toString(params, "direction", "up");
        int maxSwipes = SafeConvert.toInt(params, "maxSwipes", 6);
        int distance = SafeConvert.toInt(params, "distance", 500);

        for (int i = 0; i < maxSwipes; i++) {
            try {
                WebElement el = driver.findElement(ctx.by());
                if (el.isDisplayed()) return;
            } catch (NoSuchElementException e) {}

            swipe(driver, direction, distance);
        }
    }

    private void swipe(AppiumDriver driver, String direction, int distance) {
        Dimension size = driver.manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();

        int startX = width / 2;
        int startY = height / 2;
        int endX = startX;
        int endY = startY;

        switch (direction.toLowerCase()) {
            case "up" -> {
                startY = (int) (height * 0.8);
                endY = startY - distance;
            }
            case "down" -> {
                startY = (int) (height * 0.2);
                endY = startY - distance;
            }
            case "left" -> {
                startY = height / 2;
                startX = (int) (width * 0.8);
                endX = startX - distance;
            }
            case "right" -> {
                startY = height / 2;
                startX = (int) (width * 0.2);
                endX = startX + distance;
            }
            default -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(
                Duration.ZERO,
                PointerInput.Origin.viewport(),
                startX,
                startY
        ));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        swipe.addAction(finger.createPointerMove(
                Duration.ofMillis(400),
                PointerInput.Origin.viewport(),
                endX,
                endY
        ));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }
}
