package com.example.section4springbatch.FlowJobExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SimpleFlowConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleFlowBatchJob() {
        return jobBuilderFactory.get("simpleFlowBatchJob")
                .start(flow())
                .next(simpleFlowBatchStep3())
                .end()
                .build();
    }

    @Bean
    public Flow flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
        flowBuilder.start(simpleFlowBatchStep1())
                .next(simpleFlowBatchStep2())
                .end();

        return flowBuilder.build();
    }

    @Bean
    public Step simpleFlowBatchStep1() {
        return stepBuilderFactory.get("simpleFlowBatchStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowBatchStep1 has executed");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step simpleFlowBatchStep2() {
        return stepBuilderFactory.get("simpleFlowBatchStep2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowBatchStep2 has executed");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step simpleFlowBatchStep3() {
        return stepBuilderFactory.get("simpleFlowBatchStep3")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowBatchStep3 has executed");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
