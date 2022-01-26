package com.example.section4springbatch.incrementerExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BatchConfiguration_C {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job incrementerTestBatchSimpleJob() {
        return jobBuilderFactory.get("incrementerTestBatchSimpleJob")
                .start(incrementerTestBatchStep1())
                .next(incrementerTestBatchStep2())
//                .incrementer(new CustomJobParametersIncrementer())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step incrementerTestBatchStep1() {
        return stepBuilderFactory.get("incrementerTestBatchStep1")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step1 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }

    @Bean
    public Step incrementerTestBatchStep2() {
        return stepBuilderFactory.get("incrementerTestBatchStep1")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step2 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }
}
