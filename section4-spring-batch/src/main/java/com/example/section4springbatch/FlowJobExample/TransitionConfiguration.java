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
public class TransitionConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job transitionBatchJob() {
        // 3개의 transition
        return jobBuilderFactory.get("transitionBatchJob")
                .start(transitionBatchStep1())
                    .on("FAILED")
                    .to(transitionBatchStep2())
                    .on("FAILED")
                    .stop()
                .from(transitionBatchStep1())
                    .on("*")
                    .to(transitionBatchStep3())
                    .next(transitionBatchStep4())
                .from(transitionBatchStep2())
                    .on("*")
                    .to(transitionBatchStep5())
                .end()
                .build();
    }

    @Bean
    public Step transitionBatchStep1() {
        return stepBuilderFactory.get("transitionBatchStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("transitionBatchStep1 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step transitionBatchStep2() {
        return stepBuilderFactory.get("transitionBatchStep2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("transitionBatchStep2 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step transitionBatchStep3() {
        return stepBuilderFactory.get("transitionBatchStep3")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("transitionBatchStep3 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step transitionBatchStep4() {
        return stepBuilderFactory.get("transitionBatchStep4")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("transitionBatchStep4 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step transitionBatchStep5() {
        return stepBuilderFactory.get("transitionBatchStep5")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("transitionBatchStep5 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
