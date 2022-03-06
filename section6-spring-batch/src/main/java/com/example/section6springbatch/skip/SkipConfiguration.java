package com.example.section6springbatch.skip;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class SkipConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job skipBatchJob() {
        return jobBuilderFactory.get("skipBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(skipBatchJobStep1())
                .build();
    }

    @Bean
    public Step skipBatchJobStep1() {
        return stepBuilderFactory.get("skipBatchJobStep1")
                .<String, String>chunk(5)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception {
                        i++;
                        if (i == 3) {
                            throw new SkipAbleException("skip");
                        }
                        System.out.println("ItemReader : " + i);
                        return i > 20 ? null : String.valueOf(i);
                    }
                })
                .processor(skipBatchJobItemProcess())
                .writer(skipBatchJobItemWriter())
                .faultTolerant()
                .skipPolicy(limitCheckingItemSkipPolicy()) // Bean 을 통해 Skip 정책 설정
//                .skip(SkipAbleException.class) // Skip 가능한 Exception
//                .skipLimit(2) // 2번까지 skip 가능
//                .noSkip(NoSkipAbleException.class) // Skip 불가능한 Exception
                .build();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(SkipAbleException.class, true);
        LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(2, exceptionClass);

        return limitCheckingItemSkipPolicy;
    }

    @Bean
    public ItemWriter<? super String> skipBatchJobItemWriter() {
        return new SkipItemWriter();
    }

    @Bean
    public ItemProcessor<? super String, String> skipBatchJobItemProcess() {
        return new SkipItemProcessor();
    }
}
