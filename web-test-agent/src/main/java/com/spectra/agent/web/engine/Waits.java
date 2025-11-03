package com.spectra.agent.web.engine;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Waits {
    private final WebDriverWait wait;
}
