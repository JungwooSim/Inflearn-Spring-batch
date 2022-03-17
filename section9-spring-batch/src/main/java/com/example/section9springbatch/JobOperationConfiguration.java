package com.example.section9springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobOperationConfiguration {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final JobRegistry jobRegistry;

    @Bean
    public Job jobOperationBatchJob() {
        return jobBuilderFactory.get("jobOperationBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(jobOperationBatchJobStep1())
                .start(jobOperationBatchJobStep2())
                .build();
    }

    @Bean
    public Step jobOperationBatchJobStep1() {
        return stepBuilderFactory.get("jobOperationBatchJobStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("step1 was executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step jobOperationBatchJobStep2() {
        return stepBuilderFactory.get("jobOperationBatchJobStep2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("step2 was executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public BeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
