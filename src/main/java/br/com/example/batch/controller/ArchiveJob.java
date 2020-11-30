package br.com.example.batch.controller;

import br.com.example.batch.controller.domain.Client;
import br.com.example.batch.controller.domain.Proposal;
import br.com.example.batch.controller.listener.JobListener;
import br.com.example.batch.controller.listener.StepListener;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

//https://blog.marcosbarbero.com/pt_BR/creating-a-batch-service/
@Configuration
public class ArchiveJob {

    //https://www.baeldung.com/introduction-to-spring-batch
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    //https://www.toptal.com/spring/spring-batch-tutorial
    @Qualifier("jobMichael")
    @Bean
    public Job jobPessoa(JobListener jobListener, @Qualifier("step1") @Autowired Step step1) {
        return jobBuilderFactory
                .get("jobMichael")
                .listener(jobListener)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    //https://docs.spring.io/spring-batch/docs/current/reference/html/appendix.html
    @Qualifier("step1")
    @Bean
    public Step step1(@Autowired StepListener stepListener) {
        return stepBuilderFactory
                .get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("=====================");
                        System.out.println("tasklet sucesso");
                        System.out.println("=====================");
                        return RepeatStatus.FINISHED;
                    }
                })
                .listener(stepListener)
                .build();
    }

    //https://giuliana-bezerra.medium.com/desenvolvimento-com-spring-batch-steps-4d42af2696ec
    //https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#configureStep
    //https://stackoverflow.com/questions/52168976/how-to-use-decider-in-spring-batch
    //https://www.baeldung.com/spring-batch-conditional-flow
    //https://grokonez.com/spring-framework/spring-batch/spring-batch-programmatic-flow-decision
    //https://grokonez.com/spring-framework/spring-batch/spring-batch-partition-scaling-parallel-processing
    @Qualifier("jobGerarRelatorioDeProposta")
    @Bean
    public Job jobGerarRelatorioDeProposta(@Autowired JobListener jobListener, @Qualifier("stepProcessarProposta") @Autowired Step stepProcessarProposta) {
        return jobBuilderFactory
                .get("jobGerarRelatorioDeProposta")
                .listener(jobListener)
                .start(stepProcessarProposta)
                .next((jobExecution, stepExecution) -> {
                    boolean fileExiste = new FileSystemResource("target/test-outputs/output.txt").exists();
                    return fileExiste ? new FlowExecutionStatus("EXISTE_ARQUIVO") : new FlowExecutionStatus("NAO_EXISTE_ARQUIVO");
                })
                    .on("EXISTE_ARQUIVO").to(
                        stepBuilderFactory.get("fim")
                            .tasklet((stepContribution, chunkContext) -> {
                                System.out.println("=====================");
                                System.out.println("deu certo");
                                System.out.println("=====================");
                                return RepeatStatus.FINISHED;
                            })
                            .build()
                    )
                    .on("NAO_EXISTE_ARQUIVO").fail().end()
                .build();
    }

    @Qualifier("stepProcessarProposta")
    @Bean
    public Step passoProcessarProposta(@Qualifier("itemReadLerPropostas") @Autowired ItemReader itemReadLerPropostas,
                                       @Qualifier("itemWriteLerPropostas") @Autowired ItemWriter itemWriteLerPropostas,
                                       @Qualifier("itemProcessProposal") @Autowired ItemProcessor itemProcessProposal,
                                       @Autowired StepListener stepListener) {
        return stepBuilderFactory
                .get("step1")
                .listener(stepListener)
                .chunk(10)
                .reader(itemReadLerPropostas)
                .processor(itemProcessProposal)
                .writer(itemWriteLerPropostas)
                .build();
    }

    @Qualifier("itemProcessProposal")
    @Bean
    public ItemProcessor itemProcessProposal(){
        return new ItemProcessor<Proposal, Proposal>() {
            @Override
            public Proposal process(Proposal o) throws Exception {
                o.getClient().setName(o.getClient().getName()+"processado");
                return o;
            }
        };
    }

//https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html#database
    @Qualifier("itemReadLerPropostas")
    @Bean
    public ItemReader<Proposal> itemReadLerPropostas() {
        return new JdbcCursorItemReaderBuilder<Proposal>()
                    .dataSource(dataSource)
                    .name("itemReadLerPropostas")
                    .sql("select tbl_proposal.id_proposal, tbl_client.client_name from tbl_proposal inner join tbl_client on tbl_client.id_proposal = tbl_proposal.id_proposal")
                    .rowMapper(new RowMapper<Proposal>() {
                        @Override
                        public Proposal mapRow(ResultSet resultSet, int i) throws SQLException {
                            return Proposal.builder()
                                        .id(resultSet.getLong("id_proposal"))
                                        .client( Client.builder()
                                                    .name(resultSet.getString("client_name"))
                                                .build())
                                    .build();
                        }
                    })
                .build();
    }

    //https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html#flatFiles
    @Qualifier("itemWriteLerPropostas")
    @Bean
    public ItemWriter itemWriteLerPropostas(){
        return new FlatFileItemWriterBuilder<Proposal>()
                    .name("itemWriteLerPropostas")
                    .resource(new FileSystemResource("target/test-outputs/output.txt"))
                    .lineAggregator(new LineAggregator<Proposal>() {
                        @Override
                        public String aggregate(Proposal proposal) {
                            return proposal.getId() + ";" + proposal.getClient().getName();
                        }
                    })
                .build();
    }
}
