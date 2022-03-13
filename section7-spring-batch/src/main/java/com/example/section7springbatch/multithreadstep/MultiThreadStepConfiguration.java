package com.example.section7springbatch.multithreadstep;

import com.example.section7springbatch.ayncitem.Customer;
import com.example.section7springbatch.ayncitem.CustomerRowMapper;
import com.example.section7springbatch.ayncitem.StopWatchJobListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;

@RequiredArgsConstructor
@Configuration
public class MultiThreadStepConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job multiThreadStepBatchJob() {
        return jobBuilderFactory.get("multiThreadStepBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(multiThreadStepBatchJobStep1())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step multiThreadStepBatchJobStep1() {
        return stepBuilderFactory.get("multiThreadStepBatchJobStep1")
                .<Customer, Customer>chunk(100)
                .reader(this.pagingItemReader()) // 중요! Thread-safe 하도록 reader 구현 필요
                .listener(new CustomItemReadListener())
                .processor((ItemProcessor<Customer, Customer>) item -> item)
                .listener(new CustomerItemProcessListener())
                .writer(this.customerItemWriter())
                .listener(new CustomItemWriterListener())
                .taskExecutor(this.taskExecutor()) // 비동기로 실행하기 위한 옵션
                .build();
    }

    private TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("async-thread");

        return taskExecutor;
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
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
