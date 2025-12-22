package com.spectra.agent.web.engine.runner;

import com.spectra.agent.web.engine.actions.ActionsRegistry;
import com.spectra.agent.web.engine.context.ExecutionContext;
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
