package com.example.section7springbatch.ParallelStep;

import com.example.section7springbatch.ayncitem.StopWatchJobListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Configuration
public class ParallelStepConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job parallelStepBatchJob() {
        return jobBuilderFactory.get("parallelStepBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(this.flow1())
                .split(this.taskExecutor())
                    .add(flow2())
                .end()
                .listener(new StopWatchJobListener())
                .build();
    }

    private Flow flow1() {
        TaskletStep taskletStep = stepBuilderFactory.get("step1")
                .tasklet(this.tasklet()).build();

        return new FlowBuilder<Flow>("flow1")
                .start(taskletStep)
                .build();
    }

    private Flow flow2() {
        TaskletStep taskletStep2 = stepBuilderFactory.get("step2")
                .tasklet(this.tasklet()).build();

        TaskletStep taskletStep3 = stepBuilderFactory.get("step3")
                .tasklet(this.tasklet()).build();

        return new FlowBuilder<Flow>("flow1")
                .start(taskletStep2)
                .next(taskletStep3)
                .build();
    }

    private Tasklet tasklet() {
        return new CustomTasklet();
    }

    private TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("async-thread-");

        return executor;
    }
}
