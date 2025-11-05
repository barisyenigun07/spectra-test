package com.spectra.agent.web.engine.actions;

import com.spectra.agent.web.engine.context.ExecutionContext;
import com.spectra.commons.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component("click")
@RequiredArgsConstructor
public class ClickAction implements ActionHandler{

    @Override
    public void handle(ExecutionContext ctx) {

    }
}
