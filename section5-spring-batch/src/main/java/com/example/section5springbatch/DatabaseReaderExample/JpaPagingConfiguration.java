package com.example.section5springbatch.DatabaseReaderExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JpaPagingConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingBasicJob() {
        return jobBuilderFactory.get("jpaPagingBasicJob")
                .start(jpaPagingBasicJobStep1())
                .build();
    }

    @Bean
    public Step jpaPagingBasicJobStep1() {
        return stepBuilderFactory.get("jpaPagingBasicJobStep1")
                .<Customer, Customer>chunk(chunkSize)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select c from customer c")
                .build();
    }

    @Bean
    public ItemWriter<Customer> jpaPagingItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item);
            }
        };
    }
}
