package org.example;

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
    public static void main(String[] args)
    {
        // Get the RuntimeService instance from the Camunda BPM engine
        RuntimeService runtimeService = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("camunda.cfg.xml")
                .buildProcessEngine()
                .getRuntimeService();

        // Get the default ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // Get the RepositoryService to deploy the process definition
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // Create a DeploymentBuilder and add the BPMN file to it
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("processes/simple.bpmn");

        // Deploy the process definition
        Deployment deployment = deploymentBuilder.deploy();

        // Get the deployed ProcessDefinition
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        // Create an instance of CorrelationExample
        CorrelationExample correlationExample = new CorrelationExample(runtimeService);

        // Start a process instance with a specific process definition key and business key
        correlationExample.startProcessInstanceByKeyAndBusinessKey(processDefinition.getKey(), "myBusinessKey");

        // Find a process instance with a specific process definition key and business key
        ProcessInstance processInstance = correlationExample.findProcessInstanceByKeyAndBusinessKey(processDefinition.getKey(), "myBusinessKey");
        if (processInstance != null) {
            System.out.println("Found process instance with id " + processInstance.getId() + " and business key " + processInstance.getBusinessKey());
        } else {
            System.out.println("Could not find process instance with specified keys");
        }

        // Correlate a message to a process instance with a specific business key
        correlationExample.correlateMessage("myMessage", "myBusinessKey");
        correlationExample.correlateMessage("myMessage2", "myBusinessKey");
    }
}