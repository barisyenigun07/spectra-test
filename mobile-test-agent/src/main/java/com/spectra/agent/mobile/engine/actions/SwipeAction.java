package com.spectra.agent.mobile.engine.actions;

import com.spectra.agent.mobile.engine.context.ExecutionContext;
import org.openqa.selenium.Dimension;
import org.springframework.stereotype.Component;

@Component("swipe")
public class SwipeAction implements ActionHandler {
    @Override
    public void handle(ExecutionContext ctx) {
        Dimension size = ctx.driver().manage().window().getSize();
        int startx = (int) (size.width * 0.8);
        int endx = (int) (size.width * 0.20);
        int starty = size.height / 2;
    }
}
