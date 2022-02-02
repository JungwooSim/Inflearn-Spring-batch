package com.example.section4springbatch.FlowJobExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobExecutionDeciderConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobExecutionDeciderBatchJob() {
        return jobBuilderFactory.get("jobExecutionDeciderBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(jobExecutionDeciderBatchStep1())
                .next(decider())
                .from(decider()).on("ODD").to(oddStep())
                .from(decider()).on("EVEN").to(evenStep())
                .end()
                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new CustomDecider();
    }

    @Bean
    public Step jobExecutionDeciderBatchStep1() {
        return stepBuilderFactory.get("jobExecutionDeciderBatchStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("jobExecutionDeciderBatchStep1 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step evenStep() {
        return stepBuilderFactory.get("eventStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("evenStep has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("oddStep has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
