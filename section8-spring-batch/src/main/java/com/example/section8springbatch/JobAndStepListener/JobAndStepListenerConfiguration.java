package com.example.section8springbatch.JobAndStepListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobAndStepListenerConfiguration {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobAndStepListenerBatchJob() {
        return jobBuilderFactory.get("jobAndStepListenerBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(this.step1())
                .next(this.step2())
//                .listener(new CustomJobExecutionListener()) // 인터페이스 구현을 통해 사용
                .listener(new CustomAnnotationJobExecutionListener()) // 어노테이션 기반 사용
                .build();
    }

    private Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(((stepContribution, chunkContext) -> RepeatStatus.FINISHED))
                .listener(new CustomStepExecutionListener())// 빈으로 등록 후 사용 가능
                .build();
    }

    private Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(((stepContribution, chunkContext) -> RepeatStatus.FINISHED))
                .listener(new CustomStepExecutionListener())
                .build();
    }
}
