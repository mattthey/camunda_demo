package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class TaskExample implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        System.out.println("Hello");
    }
}
