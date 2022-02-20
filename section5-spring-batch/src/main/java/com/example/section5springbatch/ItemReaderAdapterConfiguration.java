package com.example.section5springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ItemReaderAdapterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;

    @Bean
    public Job itemReaderAdapterBasicJob() {
        return jobBuilderFactory.get("itemReaderAdapterBasicJob")
                .start(itemReaderAdapterBasicJobStep1())
                .build();
    }

    @Bean
    public Step itemReaderAdapterBasicJobStep1() {
        return stepBuilderFactory.get("itemReaderAdapterBasicJobStep1")
                .<String, String>chunk(chunkSize)
                .reader(itemReaderAdapterItemReader())
                .writer(itemReaderAdapterItemWriter())
                .build();
    }

    @Bean
    public ItemReader<String> itemReaderAdapterItemReader() {
        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(customService());
        reader.setTargetMethod("customRead"); // method name
        return reader;
    }

    @Bean
    public Object customService() {
        return new ItemReaderAdapterCustomService();
    }

    @Bean
    public ItemWriter<String> itemReaderAdapterItemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(item);
            }
        };
    }

}
