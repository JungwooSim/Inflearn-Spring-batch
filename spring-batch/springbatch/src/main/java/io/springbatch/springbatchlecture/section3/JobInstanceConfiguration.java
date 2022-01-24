package io.springbatch.springbatchlecture.section3;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobInstanceConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

//    @Bean // 사용하지 않아서 주석
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step3())
                .next(step4())
                .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

                        // Job Parameter 에 접근하는 방법1
                        JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                        System.out.println("name : " + jobParameters.getString("name"));
                        System.out.println("seq : " + jobParameters.getString("seq"));
                        System.out.println("date : " + jobParameters.getDate("date"));
                        System.out.println("age : " + jobParameters.getDouble("age"));

                        // Job Parameter 에 접근하는 방법2
                        Map<String, Object> params = chunkContext.getStepContext().getJobParameters();

                        System.out.println("step3 was executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step4 was executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
