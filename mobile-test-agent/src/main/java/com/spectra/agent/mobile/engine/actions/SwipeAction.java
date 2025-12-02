package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component("swipe")
public class SwipeAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Map<String, Object> params = ctx.step().params();

        String direction = (String) params.getOrDefault("direction", "up");
        int distance = (Integer) params.getOrDefault("distance", 500);

        int startX = ctx.driver().manage().window().getSize().width / 2;
        int startY = ctx.driver().manage().window().getSize().height / 2;

        int endX = startX;
        int endY = startY;

        switch (direction.toLowerCase()) {
            case "up" -> endY = startY - distance;
            case "down" -> endY = startY + distance;
            case "left" -> endX = startX - distance;
            case "right" -> endX = startX + distance;
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ctx.driver().perform(List.of(swipe));
    }
}
