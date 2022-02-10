package com.example.section5springbatch.DatabaseReaderExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JdbcPagingConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingBasicJob() throws Exception {
        return jobBuilderFactory.get("jdbcPagingBasicJob")
                .start(jdbcPagingBasicJobStep1())
                .build();
    }

    @Bean
    public Step jdbcPagingBasicJobStep1() throws Exception {
        return stepBuilderFactory.get("jdbcPagingBasicJobStep1")
                .<Customer, Customer>chunk(chunkSize)
                .reader(jdbcPagingItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> jdbcPagingItemReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "A%");

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPagingItemReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .queryProvider(jdbcPagingCreateQueryProvider())
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public PagingQueryProvider jdbcPagingCreateQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("customer");
        queryProvider.setWhereClause("firstname like :firstName");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public ItemWriter<Customer> jdbcCursorItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item);
            }
        };
    }
}
