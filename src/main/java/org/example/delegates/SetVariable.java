package org.example.delegates;

import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SetVariable implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        final String fromVarName = Objects.requireNonNullElse(execution.getVariable("fromVarName"), "NONE").toString();
        final String toVarName = Objects.requireNonNullElse(execution.getVariable("toVarName"), "NONE").toString();
        final String value = Objects.requireNonNullElse(execution.getVariable(fromVarName), "NONE").toString();
        execution.setVariable(toVarName, value);
    }
}
