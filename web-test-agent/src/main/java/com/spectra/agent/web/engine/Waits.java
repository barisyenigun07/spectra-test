package com.spectra.agent.web.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.support.ui.WebDriverWait;

@RequiredArgsConstructor
@Getter
public class Waits {
    private final WebDriverWait wait;
}
