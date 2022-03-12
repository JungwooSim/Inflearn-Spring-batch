package com.example.section6springbatch.retry.template;

import com.example.section6springbatch.retry.RetryAbleException;
import com.example.section6springbatch.retry.template.RetryItemProcessor;
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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RetryConfiguration2 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryBatchJob2() {
        return jobBuilderFactory.get("retryBatchJob2")
                .incrementer(new RunIdIncrementer())
                .start(retryBatchJobStep2())
                .build();
    }

    @Bean
    public Step retryBatchJobStep2() {
        return stepBuilderFactory.get("retryBatchJobStep2")
                .<String, Customer>chunk(5)
                .reader(retryBatchJobReader2())
                .processor(retryBatchJobProcessor2())
                .writer(items -> items.forEach(item -> System.out.println(item)))
                .faultTolerant()
                .skip(RetryAbleException.class)
                .skipLimit(2)
//                .retry(RetryAbleException.class) // retry 설정 - 1
//                .retryLimit(2) // retry limit 설정 - 1
                .retryPolicy(retryPolicy2()) // retry 설정 - 2
                .build();
    }

    @Bean
    public ItemProcessor<String, Customer> retryBatchJobProcessor2() {
        return new RetryItemProcessor();
    }

    @Bean
    public ListItemReader<String> retryBatchJobReader2() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryPolicy2() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap();
        exceptionClass.put(RetryAbleException.class, true);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);
        return simpleRetryPolicy;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap();
        exceptionClass.put(RetryAbleException.class, true);

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
//        retryTemplate.setBackOffPolicy(simpleRetryPolicy);

        return retryTemplate;
    }
}
