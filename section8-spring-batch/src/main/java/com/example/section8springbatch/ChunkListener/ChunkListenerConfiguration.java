package com.example.section8springbatch.ChunkListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ChunkListenerConfiguration {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkListenerBatchJob() {
        return jobBuilderFactory.get("chunkListenerBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(this.step1())
                .build();
    }

    private Step step1() {
        return stepBuilderFactory.get("step1")
                .<Integer, String>chunk(10)
                .listener(new CustomChunkListener())
                .listener(new CustomItemReaderListener())
                .listener(new CustomItemProcessListener())
                .listener(new CustomItemWriterListener())
                .reader(this.listItemReader())
                .processor((ItemProcessor) item -> {
//                    throw new RuntimeException("failed");
                    return "item" + item;
                })
                .writer((ItemWriter<String>) items -> {
                    System.out.println("items = " + items);
                })
                .build();
    }

    private ItemReader<Integer> listItemReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return new ListItemReader<>(list);
    }
}
