package com.example.section6springbatch.repeat;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RepeatConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job repeatBatchJob() {
        return jobBuilderFactory.get("repeatBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(repeatBatchJobStep1())
                .build();
    }

    @Bean
    public Step repeatBatchJobStep1() {
        return stepBuilderFactory.get("repeatBatchJobStep1")
                .<String, String>chunk(5)
                .reader(new ItemReader<String>() {
                    int i = 0;

                    @Override
                    public String read() {
                        i++;
                        return i > 3 ? null : "item" + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    RepeatTemplate repeatTemplate = new RepeatTemplate();

                    @Override
                    public String process(String item) throws Exception {
                        // Example1
//                        repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3000)); // 3??? ?????? loop

                        // Example2
//                        repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3)); // 3??? loop ?????????

                        // Example3
                        // ?????? ?????? ????????? ????????????. ?????????????????? and ?????? (???????????? true ?????? ?????? ???)
//                        CompositeCompletionPolicy completionPolicy = new CompositeCompletionPolicy();
//                        CompletionPolicy[] completionPolicies = new CompletionPolicy[]{
//                                new TimeoutTerminationPolicy(3000),
//                                new SimpleCompletionPolicy(3)
//                        };
//                        completionPolicy.setPolicies(completionPolicies);
//                        repeatTemplate.setCompletionPolicy(completionPolicy);

                        // repeatTemplate ??? ????????? ????????? ???????????? loop
//                        repeatTemplate.iterate(new RepeatCallback() {
//                            @Override
//                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
//                                System.out.println("repeatTemplate is testing");
//                                return RepeatStatus.CONTINUABLE;
//                            }
//                        });

                        // Example4
                        // ????????? ?????????????????? 3??? loop
                        repeatTemplate.setExceptionHandler(customSimpleLimitExceptionHandler());

                        // repeatTemplate ??? ????????? ????????? ???????????? loop
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                System.out.println("repeatTemplate is testing");
                                throw new RuntimeException("Exception is occurred.");
                            }
                        });

                        return item;
                    }
                })
                .writer(items -> System.out.println(items))
                .build();
    }

    @Bean
    public ExceptionHandler customSimpleLimitExceptionHandler() {
        return new SimpleLimitExceptionHandler(3);
    }
}
