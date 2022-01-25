package com.example.section4springbatch.JobBuilderFactoryExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobBuilderFactoryTestBatchSimpleJob() {
        // SimpleJob
        return jobBuilderFactory.get("jobBuilderFactoryTestBatchSimpleJob")
                .start(jobBuilderFactoryTestBatchStep1())
                .next(jobBuilderFactoryTestBatchStep2())
                .build();
    }

    @Bean
    public Job jobBuilderFactoryTestBatchFlowJob() {
        // FlowJob
        return jobBuilderFactory.get("jobBuilderFactoryTestBatchFlowJob")
                .start(jobBuilderFactoryTestBatchFlow())
                .next(jobBuilderFactoryTestBatchStep3())
                .end()
                .build();
    }

    @Bean
    public Step jobBuilderFactoryTestBatchStep1() {
        return stepBuilderFactory.get("jobBuilderFactoryTestBatchStep1")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("jobBuilderFactoryTestBatchStep1 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }

    @Bean
    public Step jobBuilderFactoryTestBatchStep2() {
        return stepBuilderFactory.get("jobBuilderFactoryTestBatchStep2")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("jobBuilderFactoryTestBatchStep2 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }

    @Bean
    public Step jobBuilderFactoryTestBatchStep3() {
        return stepBuilderFactory.get("jobBuilderFactoryTestBatchStep3")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("jobBuilderFactoryTestBatchStep3 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }

    @Bean
    public Flow jobBuilderFactoryTestBatchFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");

        flowBuilder.start(jobBuilderFactoryTestBatchStep1())
                .next(jobBuilderFactoryTestBatchStep2())
                .end();

        return flowBuilder.build();
    }
}
