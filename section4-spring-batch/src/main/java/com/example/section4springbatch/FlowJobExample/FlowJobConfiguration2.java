package com.example.section4springbatch.FlowJobExample;

import lombok.RequiredArgsConstructor;
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
public class FlowJobConfiguration2 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJobBatchJob3() {
        return jobBuilderFactory.get("flowJobBatchJob3")
                .start(flowJobBatchFlow1())
                    .on("COMPLETED").to(flowJobBatchFlow2())
                .from(flowJobBatchJob3Step1())
                    .on("FAILED").to(flowJobBatchFlow3())
                .end()
                .build();
    }

    @Bean
    public Flow flowJobBatchFlow1() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobBatchFlow1");
        flowBuilder.start(flowJobBatchJob3Step1());
        return flowBuilder.end();
    }

    @Bean
    public Flow flowJobBatchFlow2() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobBatchFlow2");
        return flowBuilder.start(flowJobBatchFlow3())
                .next(flowJobBatchJob3Step5())
                .next(flowJobBatchJob3Step6())
                .end();
    }

    @Bean
    public Flow flowJobBatchFlow3() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobBatchFlow3");
        return flowBuilder.start(flowJobBatchFlow3())
                .next(flowJobBatchJob3Step4())
                .end();
    }

    @Bean
    public Step flowJobBatchJob3Step1() {
        return stepBuilderFactory.get("flowJobBatchJob3Step1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJob3Step1 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJob3Step2() {
        return stepBuilderFactory.get("flowJobBatchJob3Step2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJob3Step2 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJob3Step3() {
        return stepBuilderFactory.get("flowJobBatchJob3Step3")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJob3Step3 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJob3Step4() {
        return stepBuilderFactory.get("flowJobBatchJob3Step4")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJob3Step4 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJob3Step5() {
        return stepBuilderFactory.get("flowJobBatchJob3Step5")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJob3Step5 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step flowJobBatchJob3Step6() {
        return stepBuilderFactory.get("flowJobBatchJob3Step6")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("flowJobBatchJob3Step6 has executed");
                    return RepeatStatus.FINISHED;
                })).build();
    }


}
