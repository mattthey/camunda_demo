package org.example;

import java.util.List;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;

public class CorrelationExample {

    private RuntimeService runtimeService;

    public CorrelationExample(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void startProcessInstanceByKeyAndBusinessKey(String processDefinitionKey, String businessKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(processDefinitionKey)
                .businessKey(businessKey)
                .execute();
        System.out.println("Started process instance with id " + processInstance.getId() + " and business key " + businessKey);
    }

    public ProcessInstance findProcessInstanceByKeyAndBusinessKey(String processDefinitionKey, String businessKey) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .processInstanceBusinessKey(businessKey)
                .active();
        return query.singleResult();
    }

    public void correlateMessage(String messageName, String businessKey) {
        final List<MessageCorrelationResult> messageCorrelationResults = runtimeService.createMessageCorrelation(messageName)
                .processInstanceBusinessKey(businessKey)
                .correlateAllWithResult();
        System.out.println("Correlated message " + messageName + " with process instance " + "processInstance.getId()" + " and business key " + businessKey);
    }

}