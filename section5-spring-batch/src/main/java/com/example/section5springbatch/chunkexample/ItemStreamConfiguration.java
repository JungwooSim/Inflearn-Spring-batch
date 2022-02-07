package com.example.section5springbatch.chunkexample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ItemStreamConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemStreamBasicJob() {
        return jobBuilderFactory.get("itemStreamBasicJob")
                .start(itemStreamBasicJobStep1())
                .build();
    }

    @Bean
    public Step itemStreamBasicJobStep1() {
        return stepBuilderFactory.get("itemStreamBasicJobStep1")
                .<String, String>chunk(5)
                .reader(customItemStreamReader())
                .writer(customItemStreamWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super String> customItemStreamWriter() {
        return new CustomItemWriter2();
    }

    public CustomItemStreamReader customItemStreamReader() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i <= 10; i++) {
            items.add(String.valueOf(i));
        }

        return new CustomItemStreamReader(items);
    }

}
