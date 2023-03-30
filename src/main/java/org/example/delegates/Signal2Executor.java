package org.example.delegates;

import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class Signal2Executor implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        PrintVariables.exec(getClass().getSimpleName(), execution);
    }
}
