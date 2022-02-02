package com.example.section4springbatch.FlowJobExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CustomExitConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job customExitBatchJob() {
        return jobBuilderFactory.get("customExitBatchJob")
                .start(customExitBatchStep1())
                    .on("FAILED").to(customExitBatchStep2())
                    .on("PASS").stop() // customExitBatchStep2 의 ExitCode 를 "PASS" 로 변경
                .end()
                .build();
    }

    @Bean
    public Step customExitBatchStep1() {
        return stepBuilderFactory.get("customExitBatchStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("customExitBatchStep1 has executed");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step customExitBatchStep2() {
        return stepBuilderFactory.get("customExitBatchStep2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("customExitBatchStep2 has executed");
                    return RepeatStatus.FINISHED;
                }))
                .listener(new PassCheckingListener())
                .build();
    }
}
