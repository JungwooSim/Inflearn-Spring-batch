package com.example.section4springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration2 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob2() {
        return jobBuilderFactory.get("batchJob2")
                .start(step3())
                .next(step4())
                .build();
    }
    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step3 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
                // tasklet 는 step 안에서 loop 도는데, return 값에 범위에 따라 설정 가능하다.
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step4 was executed");
                        return RepeatStatus.FINISHED; // 한번만 실행.
                    }
                })
                .build();
    }
}
