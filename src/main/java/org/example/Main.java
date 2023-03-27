package org.example;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class Main
{
    private static final List<String> PROCESS_RESOURCES = List.of(
            "processes/simple.bpmn",
            "processes/simple2.bpmn",
            "processes/signal1.bpmn",
            "processes/signal2.bpmn"
    );

    public static void main(String[] args)
    {
        // Get the RuntimeService instance from the Camunda BPM engine
        RuntimeService runtimeService = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("camunda.cfg.xml")
                .buildProcessEngine()
                .getRuntimeService();

        final List<ProcessDefinition> processDefinitions = deployProcess();
        signalTest(runtimeService, processDefinitions);
    }

    private static void signalTest(
            RuntimeService runtimeService,
            List<ProcessDefinition> processDefinitions
    )
    {
        final SignalExample signalExample = new SignalExample(runtimeService);
        final Map<String, Object> processVariables = Map.of("var1", 1L);

        signalExample.sendSignal("signal2", processVariables);
        signalExample.sendSignal("signal1", processVariables);
        signalExample.sendSignal("signal2", processVariables);
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