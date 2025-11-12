package com.spectra.agent.web.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobExecutor {
    private final StepExecutor stepExecutor;


}
