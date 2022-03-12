package com.example.section6springbatch.retry.api;

import com.example.section6springbatch.retry.RetryAbleException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RetryConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryBatchJob() {
        return jobBuilderFactory.get("retryBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(retryBatchJobStep1())
                .build();
    }

    @Bean
    public Step retryBatchJobStep1() {
        return stepBuilderFactory.get("retryBatchJobStep1")
                .<String, String>chunk(5)
                .reader(retryBatchJobReader())
                .processor(retryBatchJobProcessor())
                .writer(items -> items.forEach(item -> System.out.println(item)))
                .faultTolerant()
                .skip(RetryAbleException.class)
                .skipLimit(2)
//                .retry(RetryAbleException.class) // retry 설정 - 1
//                .retryLimit(2) // retry limit 설정 - 1
                .retryPolicy(retryPolicy()) // retry 설정 - 2
                .build();
    }

    @Bean
    public ItemProcessor<String, String> retryBatchJobProcessor() {
        return new RetryItemProcessor();
    }

    @Bean
    public ListItemReader<String> retryBatchJobReader() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap();
        exceptionClass.put(RetryAbleException.class, true);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);
        return simpleRetryPolicy;
    }
}
