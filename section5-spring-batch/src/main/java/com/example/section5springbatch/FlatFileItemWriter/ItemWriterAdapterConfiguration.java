package com.example.section5springbatch.FlatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ItemWriterAdapterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemWriterBatchJob() {
        return jobBuilderFactory.get("itemWriterBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(itemWriterBatchJobStep1())
                .build();
    }

    @Bean
    public Step itemWriterBatchJobStep1() {
        return stepBuilderFactory.get("itemWriterBatchJobStep1")
                .<String, String>chunk(10)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item" + i;
                    }
                })
                .writer(itemWriterBatchJobItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super String> itemWriterBatchJobItemWriter() {
        ItemWriterAdapter<String> writer = new ItemWriterAdapter<>();
        writer.setTargetObject(itemWriterAdapterCustomService());
        writer.setTargetMethod("customWrite");

        return writer;
    }

    @Bean
    public ItemWriterAdapterCustomService itemWriterAdapterCustomService() {
        return new ItemWriterAdapterCustomService();
    }

}
