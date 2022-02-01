package com.example.section4springbatch.FlowJobExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FlowJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJobBatchJob() {
        return jobBuilderFactory.get("flowJobBatchJob")
                .start(flowJobBatchJobStep1())
                .on("COMPLETED").to(flowJobBatchJobStep3()) // flowJobBatchJobStep1() 의 상태값이 "COMPLETED" 일때 flowJobBatchJobStep3()
                .from(flowJobBatchJobStep1())
                .on("FAILED").to(flowJobBatchJobStep2()) // flowJobBatchJobStep1() 의 상태값이 "FAILED" 일때 flowJobBatchJobStep2()
                .end()
                .build();
    }

    @Bean
    public Step flowJobBatchJobStep1() {
        return stepBuilderFactory.get("flowJobBatchJobStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJobStep1 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJobStep2() {
        return stepBuilderFactory.get("flowJobBatchJobStep2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJobStep2 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJobStep3() {
        return stepBuilderFactory.get("flowJobBatchJobStep3")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJobStep3 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
