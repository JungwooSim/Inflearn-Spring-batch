package com.example.section7springbatch.Partion;

import com.example.section7springbatch.ayncitem.Customer;
import com.example.section7springbatch.ayncitem.CustomerRowMapper;
import com.example.section7springbatch.ayncitem.StopWatchJobListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;

@RequiredArgsConstructor
@Configuration
public class PartitioningConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job partitioningStepBatchJob() {
        return jobBuilderFactory.get("partitioningStepBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep())
                .build();
    }

    private Step masterStep() {
        return stepBuilderFactory.get("masterStep")
                .partitioner(this.slaveStep().getName(), this.partitioner())
                .step(this.slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    private Step slaveStep() {
        return stepBuilderFactory.get("slaveStep")
                .<Customer, Customer>chunk(100)
                .reader(this.pagingItemReader(null, null))
                .writer(this.customerItemWriter())
                .build();
    }

    private Partitioner partitioner() {
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();

        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(dataSource);
        columnRangePartitioner.setTable("customer");

        return columnRangePartitioner;
    }

    @StepScope
    private JdbcPagingItemReader<Customer> pagingItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(300);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where id >= " + minValue + " and id < " + maxValue);

        HashMap<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    @StepScope
    private JdbcBatchItemWriter customerItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
