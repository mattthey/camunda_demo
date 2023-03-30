package org.example.delegates;

import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CreateVariable implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        final String varName = Objects.requireNonNullElse(execution.getVariable("varName"), "NONE").toString();
        final String value = Objects.requireNonNullElse(execution.getVariable("value"), "NONE").toString();
        execution.setVariable(varName, value);
    }
}
