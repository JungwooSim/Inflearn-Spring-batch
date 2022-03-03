package com.example.section5springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class CompositionItemConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job compositionItemBatchJob() {
        return jobBuilderFactory.get("compositionItemBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(compositionItemBatchJobStep1())
                .build();
    }

    @Bean
    public Step compositionItemBatchJobStep1() {
        return stepBuilderFactory.get("compositionItemBatchJobStep1")
                .<String, String>chunk(10)
                .reader(new ItemReader<String>() {
                    int i = 0;

                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return 1 > 10 ? null : "item";
                    }
                })
                .processor(this.customCompositionItemProcessor())
                .writer(items -> System.out.println(items))
                .build();
    }

    @Bean
    public ItemProcessor<String, String> customCompositionItemProcessor() {
        List itemProcessor = new ArrayList<>();
        itemProcessor.add(new CustomItemProcessor1());
        itemProcessor.add(new CustomItemProcessor2());

        return new CompositeItemProcessorBuilder<>()
                .delegates(itemProcessor)
                .build();

    }
}
