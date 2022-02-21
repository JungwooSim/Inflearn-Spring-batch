package com.example.section5springbatch.FlatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class JdbcWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcWriterBatchJob() {
        return jobBuilderFactory.get("jdbcWriterBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(jdbcWriterBatchJobStep1())
                .build();
    }

    @Bean
    public Step jdbcWriterBatchJobStep1() {
        return stepBuilderFactory.get("jdbcWriterBatchJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(jdbcWriterBatchJobItemReader())
                .writer(jdbcWriterBatchJobItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> jdbcWriterBatchJobItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("insert into customer values (:name, :age, :year)")
                .beanMapped()
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jdbcWriterBatchJobItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer("hong gil dong1", 41, "year"),
                new Customer("hong gil dong2", 41, "year"),
                new Customer("hong gil dong3", 41, "year")
        );

        ListItemReader<Customer> reader = new ListItemReader<>(customers);

        return reader;
    }
}
