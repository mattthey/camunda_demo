package org.example.delegates;

import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SleepTask implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        final String timeCountStr = Objects.requireNonNullElse(execution.getVariable("timeSleep"), "1").toString();
        Thread.sleep(Long.parseLong(timeCountStr));
    }
}
