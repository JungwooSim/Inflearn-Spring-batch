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
    public Job flowJobBatchJob2() {
        return jobBuilderFactory.get("flowJobBatchJob2")
                .start(flowJobBatchJobFlowA())
                .next(flowJobBatchJobStep3())
                .next(flowJobBatchJobFlowB())
                .next(flowJobBatchJobStep6())
                .end()
                .build();
    }

    @Bean
    public Flow flowJobBatchJobFlowA() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobBatchJobFlowA");
        flowBuilder.start(flowJobBatchJobStep1())
                .next(flowJobBatchJobStep2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow flowJobBatchJobFlowB() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobBatchJobFlowB");
        flowBuilder.start(flowJobBatchJobStep4())
                .next(flowJobBatchJobStep5())
                .end();
        return flowBuilder.build();
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
                    // stepContribution.setExitStatus(ExitStatus.FAILED); // DB 에 ExitStatus 설정 가능
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

    @Bean
    public Step flowJobBatchJobStep4() {
        return stepBuilderFactory.get("flowJobBatchJobStep4")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJobStep4 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJobStep5() {
        return stepBuilderFactory.get("flowJobBatchJobStep5")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJobStep5 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJobStep6() {
        return stepBuilderFactory.get("flowJobBatchJobStep6")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJobStep6 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
