package com.example.section5springbatch.FlatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class FlatFilesDelimitedConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesDelimitedBatchJob() {
        return jobBuilderFactory.get("flatFilesDelimitedBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFilesDelimitedBatchJobStep1())
                .build();
    }

    private Step flatFilesDelimitedBatchJobStep1() {
        return stepBuilderFactory.get("flatFilesDelimitedBatchJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(flatFilesDelimitedBatchJobItemReader())
                //.writer(flatFilesDelimitedBatchJobItemWriter2())
                .writer(flatFilesDelimitedBatchJobItemWriter1())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFilesDelimitedBatchJobItemWriter2() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFilesDelimitedBatchJobItemWriter2")
                .resource(new FileSystemResource("/Users/bigpenguin/project/Inflearn-Spring-batch/section5-spring-batch/src/main/resources/customer.txt"))
                .formatted()
                .format("%-2d%-14s%-2d")
                .names(new String[]{"name", "year", "age"})
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFilesDelimitedBatchJobItemWriter1() {
        return new FlatFileItemWriterBuilder<>()
                .name("flatFileWriter")
                .resource(new FileSystemResource("/Users/bigpenguin/project/Inflearn-Spring-batch/section5-spring-batch/src/main/resources/customer.txt"))
                .delimited()
                .delimiter("|")
                .names(new String[]{"name", "year", "age"})
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> flatFilesDelimitedBatchJobItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer("hong gil dong1", 41, "year"),
                new Customer("hong gil dong2", 41, "year"),
                new Customer("hong gil dong3", 41, "year")
        );

        ListItemReader<Customer> reader = new ListItemReader<>(customers);

        return reader;
    }
}
