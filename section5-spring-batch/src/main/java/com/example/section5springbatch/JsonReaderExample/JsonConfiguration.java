package com.example.section5springbatch.JsonReaderExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@RequiredArgsConstructor
@Configuration
public class JsonConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jsonBatchJob() {
        return jobBuilderFactory.get("jsonBatchJob")
                .start(jsonBatchJobStep1())
                .build();
    }

    @Bean
    public Step jsonBatchJobStep1() {
        return stepBuilderFactory.get("jsonBatchJobStep1")
                .<Customer, Customer>chunk(1)
                .reader(jsonCustomItemReader())
                .writer(jsonCustomItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<Customer> jsonCustomItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public ItemReader<Customer> jsonCustomItemReader() {
        return new JsonItemReaderBuilder<Customer>()
                .name("jsonReader")
                .resource(new ClassPathResource("customer.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
                .build();
    }

}
