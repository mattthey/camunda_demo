package org.example.delegates;

import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class PrintVariables
{
    public static void exec(String className, DelegateExecution execution)
    {
        System.out.printf("Task %s complete with args: %s%n",
                className,
                execution.getVariables().entrySet().stream()
                        .map(entry -> entry.getKey() + ':' + entry.getValue())
                        .collect(Collectors.joining(", "))
        );
    }
}
