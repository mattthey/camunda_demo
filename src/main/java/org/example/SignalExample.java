package org.example;

import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;

public class SignalExample
{
    private final RuntimeService runtimeService;

    public SignalExample(RuntimeService runtimeService)
    {
        this.runtimeService = runtimeService;
    }

    public void sendSignal(
            String signalName,
            Map<String, Object> processVariables
    )
    {
        runtimeService.signalEventReceived(signalName, processVariables);
    }
}
