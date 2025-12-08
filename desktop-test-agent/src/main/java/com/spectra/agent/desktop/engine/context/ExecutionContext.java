package com.spectra.agent.desktop.engine.context;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.commons.dto.step.StepDTO;

public record ExecutionContext(DesktopClient client, StepDTO step) {
}
