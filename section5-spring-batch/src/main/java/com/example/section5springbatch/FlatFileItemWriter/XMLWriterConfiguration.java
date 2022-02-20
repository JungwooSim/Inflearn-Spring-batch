package com.example.section5springbatch.FlatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class XMLWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job xMLWriterBatchJob() {
        return jobBuilderFactory.get("xMLWriterBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(xMLWriterBatchJobStep1())
                .build();
    }

    @Bean
    public Step xMLWriterBatchJobStep1() {
        return stepBuilderFactory.get("xMLWriterBatchJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(xMLWriterBatchJobItemReader())
                .writer(xMLWriterBatchJobItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> xMLWriterBatchJobItemWriter() {
        return new StaxEventItemWriterBuilder<>()
                .name("xMLWriterBatchJobItemWriter")
                .marshaller(xMLWriterBatchJobItemWriterMarshaller())
                .resource(new FileSystemResource("/Users/bigpenguin/project/Inflearn-Spring-batch/section5-spring-batch/src/main/resources/FlatFileItemWriter/customer.xml"))
                .rootTagName("customer")
                .build();
    }

    @Bean
    public Marshaller xMLWriterBatchJobItemWriterMarshaller() {
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);
        aliases.put("name", String.class);
        aliases.put("age", Integer.class);
        aliases.put("year", String.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);

        return xStreamMarshaller;
    }


    @Bean
    public ItemReader<? extends Customer> xMLWriterBatchJobItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer("hong gil dong1", 41, "year"),
                new Customer("hong gil dong2", 41, "year"),
                new Customer("hong gil dong3", 41, "year")
        );

        ListItemReader<Customer> reader = new ListItemReader<>(customers);

        return reader;
    }
}
