package io.springbatch.springbatchlecture.section3.executioncontext;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExecutionContextConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ExecutionContextTasklet1 executionContextTasklet1;
    private final ExecutionContextTasklet2 executionContextTasklet2;
    private final ExecutionContextTasklet3 executionContextTasklet3;
    private final ExecutionContextTasklet4 executionContextTasklet4;

    @Bean
    public Job ExecutionBatchJob() {
        return this.jobBuilderFactory.get("ExecutionBatchJob")
                .start(executionBatchStep1())
                .next(executionBatchStep2())
                .next(executionBatchStep3())
                .next(executionBatchStep4())
                .build();
    }

    @Bean
    public Step executionBatchStep1() {
        return stepBuilderFactory.get("executionBatchStep1")
                .tasklet(executionContextTasklet1)
                .build();
    }

    @Bean
    public Step executionBatchStep2() {
        return stepBuilderFactory.get("executionBatchStep2")
                .tasklet(executionContextTasklet2)
                .build();
    }

    @Bean
    public Step executionBatchStep3() {
        return stepBuilderFactory.get("executionBatchStep3")
                .tasklet(executionContextTasklet3)
                .build();
    }

    @Bean
    public Step executionBatchStep4() {
        return stepBuilderFactory.get("executionBatchStep4")
                .tasklet(executionContextTasklet4)
                .build();
    }
}
