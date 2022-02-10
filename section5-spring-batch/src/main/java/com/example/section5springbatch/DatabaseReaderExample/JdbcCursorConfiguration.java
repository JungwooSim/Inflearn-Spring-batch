package com.example.section5springbatch.DatabaseReaderExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
public class JdbcCursorConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorBasicJob() {
        return jobBuilderFactory.get("jdbcCursorBasicJob")
                .start(jdbcCursorBasicJobStep1())
                .build();
    }

    @Bean
    public Step jdbcCursorBasicJobStep1() {
        return stepBuilderFactory.get("jdbcCursorBasicJobStep1")
                .<Customer, Customer>chunk(chunkSize)
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter2())
                .build();
    }

    @Bean
    public ItemReader<Customer> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("jdbcCursorItemReader")
                .fetchSize(chunkSize)
                .sql("select id, firstName, lastName, birthdate from customer where firstName like ? order by lastName, firstName")
                .beanRowMapper(Customer.class)
                .queryArguments("A%")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<Customer> jdbcCursorItemWriter2() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item);
            }
        };
    }
}
