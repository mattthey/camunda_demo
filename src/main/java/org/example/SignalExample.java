package org.example;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class SignalExample
{
    private final RuntimeService runtimeService;

    public SignalExample(RuntimeService runtimeService)
    {
        this.runtimeService = runtimeService;
    }

    public void startProcessInstanceByKey(String processDefinitionKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(processDefinitionKey)
                .execute();
        System.out.println("Started process instance with id " + processInstance.getId());
    }

    public void sendSignal(
            String signalName,
            Map<String, Object> processVariables
    )
    {
        runtimeService.createSignalEvent(signalName)
                .setVariables(processVariables)
                .send();
    }

    public List<EventSubscription> getEventSubscriptions(String signalName)
    {
        return runtimeService.createEventSubscriptionQuery()
                .eventType("signal")
                .eventName(signalName)
                .list();
    }
}
