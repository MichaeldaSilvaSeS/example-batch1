package br.com.example.batch.controller.listener;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("====================");
        System.out.println("step before");
        System.out.println("====================");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("====================");
        System.out.println("step status");
        System.out.println("====================");
        return ExitStatus.COMPLETED;
    }
}
