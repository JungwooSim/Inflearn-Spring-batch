package com.example.section7springbatch.ayncitem;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;

@RequiredArgsConstructor
@Configuration
public class AsyncConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job asyncBatchJob() throws Exception {
        return jobBuilderFactory.get("asyncBatchJob")
                .incrementer(new RunIdIncrementer())
//                .start(asyncBatchJobStep1()) // 동기 방식
                .start(asyncBatchJobSyncStep1()) // 비동기 방식
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step asyncBatchJobStep1() throws Exception {
        return stepBuilderFactory.get("asyncBatchJobStep1")
                .<Customer, Customer>chunk(100)
                .reader(this.pagingItemReader())
                .processor(this.customerItemProcessor())
                .writer(this.customerItemWriter())
                .build();
    }

    @Bean
    public Step asyncBatchJobSyncStep1() throws Exception {
        return stepBuilderFactory.get("asyncBatchJobSyncStep1")
                .<Customer, Customer>chunk(100)
                .reader(this.pagingItemReader())
                .processor(this.asyncItemProcessor())
                .writer(this.asyncItemWriter())
                .build();
    }

    private AsyncItemWriter asyncItemWriter() throws Exception {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(this.customerItemWriter());
        asyncItemWriter.afterPropertiesSet(); // 빈으로 등록하지 않을시 필요
        return null;
    }

    private AsyncItemProcessor asyncItemProcessor() throws Exception {
        AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(this.customerItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        asyncItemProcessor.afterPropertiesSet(); // 빈으로 등록하지 않을시 필요
        return asyncItemProcessor;
    }

    //    @Bean
    public ItemProcessor<Customer, Customer> customerItemProcessor() throws InterruptedException {

        return new ItemProcessor<Customer, Customer>() {
            @Override
            public Customer process(Customer item) throws Exception {
                Thread.sleep(30);

                return new Customer(
                        item.getId(),
                        item.getFirstName().toUpperCase(),
                        item.getLastName().toUpperCase(),
                        item.getBirthDate()
                );
            }
        };
    }

    public JdbcPagingItemReader<Customer> pagingItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(300);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        HashMap<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    public JdbcBatchItemWriter customerItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(this.dataSource);
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
