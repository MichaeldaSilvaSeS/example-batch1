package br.com.example.batch.controller;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchExecutor {

    @Qualifier("jobGerarRelatorioDeProposta")
    @Autowired
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    //https://docs.spring.io/spring-batch/docs/current/reference/html/job.html#configuringJobLauncher
    //https://www.alura.com.br/artigos/agendando-tarefas-com-scheduled-do-spring
    //https://www.baeldung.com/spring-scheduled-tasks
    //https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
    /*
     A B C D E F

    A: Segundos (0 - 59).
    B: Minutos (0 - 59).
    C: Horas (0 - 23).
    D: Dia (1 - 31).
    E: Mês (1 - 12).
    F: Dia da semana (0 - 6).
     */
    @Scheduled(zone = "America/Sao_Paulo", cron = "0 37 22 * * FRI-SUN")
    public JobExecution execute() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        System.out.println("==================");
        System.out.println("comecar job");
        System.out.println("==================");
        return jobLauncher.run(job, params);
    }
}
