package org.example;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class Main
{
    private static final List<String> PROCESS_RESOURCES = List.of(
            "processes/simple.bpmn",
            "processes/simple2.bpmn",
            "processes/signal1.bpmn",
            "processes/signal2.bpmn",
            "processes/signal3.bpmn",
            "processes/signal4.bpmn",
            "processes/signal5.bpmn"
    );

    public static void main(String[] args) throws InterruptedException
    {
        // Get the RuntimeService instance from the Camunda BPM engine
        RuntimeService runtimeService = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("camunda.cfg.xml")
                .buildProcessEngine()
                .getRuntimeService();

        final List<ProcessDefinition> processDefinitions = deployProcess();
//        signalTest(runtimeService, processDefinitions);
        parallelSignalTest(runtimeService, processDefinitions);
//        parallelSignalTest2(runtimeService, processDefinitions);
        System.out.println("DONE");
    }

    private static void parallelSignalTest2(
            RuntimeService runtimeService,
            List<ProcessDefinition> processDefinitions
    ) throws InterruptedException
    {
        final List<ProcessDefinition> processDefFromSig4 = processDefinitions.stream()
                .filter(processDefinition -> processDefinition.getResourceName().endsWith("signal5.bpmn"))
                .toList();
        if (processDefFromSig4.size() != 1)
            throw new RuntimeException("Expected size 1");
        final ProcessDefinition procDef = processDefFromSig4.get(0);

        final SignalExample signalExample = new SignalExample(runtimeService);
        signalExample.startProcessInstanceByKey(procDef.getKey());

        signalExample.sendSignal("sig5", Map.of("sys_id", "1"));
        signalExample.sendSignal("sig5", Map.of("sys_id", "2"));

        final List<Execution> executions = runtimeService.createExecutionQuery()
                .processDefinitionKey(procDef.getKey())
                .list();
        for (Execution execution : executions)
        {
            final Map<String, Object> variables = runtimeService.getVariables(execution.getId());
            System.out.printf("Execution with id %s has variables: [%s]%n",
                    execution.getId(),
                    variables.entrySet().stream()
                            .map(entry -> entry.getKey() + ":" + entry.getValue())
                            .collect(Collectors.joining(", "))
            );
        }
    }

    private static void parallelSignalTest(
            RuntimeService runtimeService,
            List<ProcessDefinition> processDefinitions
    ) throws InterruptedException
    {
        final List<ProcessDefinition> processDefFromSig4 = processDefinitions.stream()
                .filter(processDefinition -> processDefinition.getResourceName().endsWith("signal4.bpmn"))
                .toList();
        if (processDefFromSig4.size() != 1)
            throw new RuntimeException("Expected size 1");
        final ProcessDefinition procDef = processDefFromSig4.get(0);

        final SignalExample signalExample = new SignalExample(runtimeService);
        signalExample.startProcessInstanceByKey(procDef.getKey());

        signalExample.sendSignal("signal4", Map.of("var", List.of("1", "2")));
        signalExample.sendSignal("signal44", Map.of("var", List.of("3", "4")));

        final List<Execution> executions = runtimeService.createExecutionQuery()
                .processDefinitionKey(procDef.getKey())
                .list();
        for (Execution execution : executions)
        {
            final Map<String, Object> variables = runtimeService.getVariables(execution.getId());
            System.out.printf("Execution with id %s has variables: [%s]%n",
                    execution.getId(),
                    variables.entrySet().stream()
                            .map(entry -> entry.getKey() + ":" + entry.getValue())
                            .collect(Collectors.joining(", "))
            );
        }
    }

    private static void signalTest(
            RuntimeService runtimeService,
            List<ProcessDefinition> processDefinitions
    )
    {
        final String signal1 = "signal1";
        final String signal2 = "signal2";
        final SignalExample signalExample = new SignalExample(runtimeService);
        final Map<String, Object> processVariables = Map.of("var1", 1L);

        List<EventSubscription> eventSubscriptions = signalExample.getEventSubscriptions(signal2);
        if (!eventSubscriptions.isEmpty()) {
            throw new RuntimeException("По идее нет стартового события, которое начинается с " + signal2);
        }
        eventSubscriptions = signalExample.getEventSubscriptions(signal1);
        if (eventSubscriptions.isEmpty()) {
            throw new RuntimeException("Должно найтись хотя бы одно (2) событие, которое ждет сигнала " + signal1);
        }
        for (EventSubscription eventSubscription : eventSubscriptions)
        {
            final String processInstanceId = eventSubscription.getProcessInstanceId();
            if (processInstanceId == null)
                continue;
            final Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            System.out.printf("For process id %s need variables: ", processInstanceId);
        }

        signalExample.sendSignal(signal2, processVariables);
        signalExample.sendSignal(signal1, processVariables);
        signalExample.sendSignal(signal2, processVariables);
    }

    private static void correlationMessageTest(
            RuntimeService runtimeService,
            List<ProcessDefinition> processDefinitions
    )
    {
        final ProcessDefinition firstProcessDefinition = processDefinitions.get(0);

        // Create an instance of CorrelationExample
        CorrelationExample correlationExample = new CorrelationExample(runtimeService);

        // Start a process instance with a specific process definition key and business key
        correlationExample.startProcessInstanceByKeyAndBusinessKey(firstProcessDefinition.getKey(), "myBusinessKey");

        // Find a process instance with a specific process definition key and business key
        ProcessInstance processInstance = correlationExample.findProcessInstanceByKeyAndBusinessKey(firstProcessDefinition.getKey(), "myBusinessKey");
        if (processInstance != null) {
            System.out.println("Found process instance with id " + processInstance.getId() + " and business key " + processInstance.getBusinessKey());
        } else {
            System.out.println("Could not find process instance with specified keys");
        }

        // Correlate a message to a process instance with a specific business key
        correlationExample.correlateMessage("myMessage", "myBusinessKey");
        correlationExample.correlateMessage("myMessage2", "myBusinessKey");
    }

    private static List<ProcessDefinition> deployProcess()
    {
        // Get the default ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // Get the RepositoryService to deploy the process definition
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // Create a DeploymentBuilder and add the BPMN file to it
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        PROCESS_RESOURCES.forEach(deploymentBuilder::addClasspathResource);

        // Deploy the process definition
        Deployment deployment = deploymentBuilder.deploy();

        // Get the deployed ProcessDefinition
        return repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .list();
    }
}