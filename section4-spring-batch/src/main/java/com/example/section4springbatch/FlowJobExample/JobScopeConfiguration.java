package com.example.section4springbatch.FlowJobExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobScopeConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job JobScopeBatchJob() {
        return jobBuilderFactory.get("JobScopeBatchJob")
                .start(JobScopeBatchJobStep1(null))
                .next(JobScopeBatchJobStep2())
                .listener(new JobListener())
                .build();
    }

    @Bean
    @JobScope
    public Step JobScopeBatchJobStep1(@Value("#{jobParameters['message']}") String message) {
        System.out.println("message = " + message);

        return stepBuilderFactory.get("JobScopeBatchJobStep1")
                .tasklet(JobScopeBatchJobTasklet1(null))
                .build();
    }

    @Bean
    public Step JobScopeBatchJobStep2() {
        return stepBuilderFactory.get("JobScopeBatchJobStep2")
                .tasklet(JobScopeBatchJobTasklet2(null))
                .listener(new CustomStepListener())
                .build();
    }

    @Bean
    @StepScope
    public Tasklet JobScopeBatchJobTasklet1(@Value("#{jobExecutionContext['name']}") String name) {
        System.out.println("name = " + name);
        return (stepContribution, chunkContext) -> {
            System.out.println("JobScopeBatchJobStep1 has executed");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet JobScopeBatchJobTasklet2(@Value("#{jobExecutionContext['name2']}") String name) {
        System.out.println("name = " + name);
        return (stepContribution, chunkContext) -> {
            System.out.println("JobScopeBatchJobStep1 has executed");
            return RepeatStatus.FINISHED;
        };
    }

}
