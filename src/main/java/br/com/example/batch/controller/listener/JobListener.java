package br.com.example.batch.controller.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("====================");
        System.out.println("before job");
        System.out.println("====================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("====================");
        System.out.println("after job");
        System.out.println("====================");
    }
}
