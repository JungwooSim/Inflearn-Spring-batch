package com.example.section5springbatch.FlatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class JpaWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaWriterBatchJob() {
        return jobBuilderFactory.get("jpaWriterBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(jpaWriterBatchJobStep1())
                .build();
    }

    @Bean
    public Step jpaWriterBatchJobStep1() {
        return stepBuilderFactory.get("jpaWriterBatchJobStep1")
                .<Customer, Customer2Entity>chunk(10)
                .reader(jpaWriterBatchJobItemReader())
                .processor(jpaWriterBatchJobProcessor())
                .writer(jpaWriterBatchJobItemWriter())
                .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer2Entity> jpaWriterBatchJobProcessor() {
        return new JpaWriterBatchJobProcessor();
    }


    @Bean
    public ItemWriter<? super Customer2Entity> jpaWriterBatchJobItemWriter() {
        return new JpaItemWriterBuilder<Customer2Entity>()
                .usePersist(true)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jpaWriterBatchJobItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer("hong gil dong1", 41, "year"),
                new Customer("hong gil dong2", 41, "year"),
                new Customer("hong gil dong3", 41, "year")
        );

        ListItemReader<Customer> reader = new ListItemReader<>(customers);

        return reader;
    }
}
