package com.example.section5springbatch.FlatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class JsonWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jsonWriterBatchJob() {
        return jobBuilderFactory.get("jsonWriterBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(jsonWriterBatchJobStep1())
                .build();
    }

    @Bean
    public Step jsonWriterBatchJobStep1() {
        return stepBuilderFactory.get("jsonWriterBatchJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(jsonWriterBatchJobItemReader())
                .writer(jsonWriterBatchJobItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> jsonWriterBatchJobItemWriter() {
        return new JsonFileItemWriterBuilder<Customer>()
                .name("jsonWriterBatchJobItemWriter")
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("/Users/bigpenguin/project/Inflearn-Spring-batch/section5-spring-batch/src/main/resources/FlatFileItemWriter/customer.json"))
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jsonWriterBatchJobItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer("hong gil dong1", 41, "year"),
                new Customer("hong gil dong2", 41, "year"),
                new Customer("hong gil dong3", 41, "year")
        );

        ListItemReader<Customer> reader = new ListItemReader<>(customers);

        return reader;
    }
}
