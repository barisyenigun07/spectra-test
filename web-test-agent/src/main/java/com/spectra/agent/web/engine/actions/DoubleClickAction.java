package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

@Component("doubleClick")
@RequiredArgsConstructor
public class DoubleClickAction implements ActionHandler {

    @Override
    public void handle(ExecutionContext ctx) {

    }
}
