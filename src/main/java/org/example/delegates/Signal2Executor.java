package org.example.delegates;

import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class Signal2Executor implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        System.out.printf("Task %s complete with args: %n%s%n",
                getClass().getSimpleName(),
                execution.getVariables().entrySet().stream()
                        .map(entry -> entry.getKey() + ':' + entry.getValue())
                        .collect(Collectors.joining(","))
        );
    }
}
