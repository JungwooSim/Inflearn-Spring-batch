package io.springbatch.springbatchlecture.section3.jobrepository;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobRepositoryConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobRepositoryListener jobRepositoryListener;

    @Bean
    public Job JobRepositoryJob() {
        return this.jobBuilderFactory.get("ExecutionBatchJoJobRepositoryJob")
                .start(JobRepositoryStep1())
                .next(JobRepositoryStep2())
                .listener(jobRepositoryListener)
                .build();
    }

    @Bean
    public Step JobRepositoryStep1() {
        return stepBuilderFactory.get("jobRepositoryStep1")
                .tasklet(null)
                .build();
    }

    @Bean
    public Step JobRepositoryStep2() {
        return stepBuilderFactory.get("jobRepositoryStep2")
                .tasklet(null)
                .build();
    }
}
