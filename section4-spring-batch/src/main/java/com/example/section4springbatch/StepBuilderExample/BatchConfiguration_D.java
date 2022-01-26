package com.example.section4springbatch.StepBuilderExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class BatchConfiguration_D {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job StepBuilderTestJob() {
        return jobBuilderFactory.get("StepBuilderTestJob")
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderTestStep1())
                .next(stepBuilderTestStep2())
                .next(stepBuilderTestStep3())
                .build();
    }

    @Bean
    public Step stepBuilderTestStep1() {
        return stepBuilderFactory.get("stepBuilderTestStep1")
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
    public Step stepBuilderTestStep2() {
        return stepBuilderFactory.get("stepBuilderTestStep2")
                .<String, String>chunk(3)
                .reader(new ItemReader<String>() {
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        return null;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String s) throws Exception {
                        return null;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> list) throws Exception {

                    }
                })
                .build();
    }

    @Bean
    public Step stepBuilderTestStep3() {
        return stepBuilderFactory.get("stepBuilderTestStep3")
                .partitioner(stepBuilderTestStep2())
                .gridSize(2)
                .build();
    }
    @Bean
    public Step stepBuilderTestStep4() {
        return stepBuilderFactory.get("stepBuilderTestStep4")
                .job(job())
                .build();
    }

    @Bean
    public Step stepBuilderTestStep5() {
        return stepBuilderFactory.get("stepBuilderTestStep4")
                .flow(flow())
                .build();
    }

    @Bean
    public Flow flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
        flowBuilder.start(stepBuilderTestStep2()).end();
        return flowBuilder.build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(stepBuilderTestStep1())
                .next(stepBuilderTestStep2())
                .next(stepBuilderTestStep3())
                .build();
    }
}
