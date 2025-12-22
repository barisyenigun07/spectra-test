package com.spectra.agent.mobile.engine.runner;

import com.spectra.agent.mobile.engine.actions.ActionsRegistry;
import com.spectra.agent.mobile.engine.context.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StepExecutor {
    private final ActionsRegistry registry;

    public void executeStep(ExecutionContext ctx) {
        registry.resolveHandler(ctx.step().action()).handle(ctx);
    }
}
