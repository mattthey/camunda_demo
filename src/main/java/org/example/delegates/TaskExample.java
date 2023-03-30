package org.example.delegates;

import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class TaskExample implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        PrintVariables.exec(execution.getId(), execution);
    }
}
