package com.spectra.agent.web.engine.model;

import com.spectra.commons.dto.StepDTO;
import org.openqa.selenium.WebDriver;

public record ExecutionContext(Long jobId, int orderIndex, WebDriver driver, StepDTO step) {
}
