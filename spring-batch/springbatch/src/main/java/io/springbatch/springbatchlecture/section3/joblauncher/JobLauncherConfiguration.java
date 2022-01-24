package io.springbatch.springbatchlecture.section3.joblauncher;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobLauncherConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job JobLauncherJob() {
        return this.jobBuilderFactory.get("JobLauncherJob")
                .start(JobLauncherStep1())
                .next(JobLauncherStep2())
                .build();
    }

    @Bean
    public Step JobLauncherStep1() {
        return stepBuilderFactory.get("jobLauncherStep1")
                .tasklet(null)
                .build();
    }

    @Bean
    public Step JobLauncherStep2() {
        return stepBuilderFactory.get("jobLauncherStep2")
                .tasklet(null)
                .build();
    }
}
