package com.example.section5springbatch.chunkexample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
public class ItemReader_ItemProcessor_ItemWriter_Configuration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemReaderItemProcessorItemWriterBatchJob() {
        return jobBuilderFactory.get("itemReaderItemProcessorItemWriterBatchJob")
                .start(itemReaderItemProcessorItemWriterBatchJobStep1())
                .next(itemReaderItemProcessorItemWriterBatchJobStep2())
                .build();
    }

    @Bean
    public Step itemReaderItemProcessorItemWriterBatchJobStep1() {
        return stepBuilderFactory.get("itemReaderItemProcessorItemWriterBatchJobStep1")
                .<Customer, Customer>chunk(3)
                .reader(customerItemReader())
                .processor(customerProcessor())
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> customerItemWriter() {
        return new CustomItemWriter();
    }

    @Bean
    public ItemReader<? extends Customer> customerItemReader() {
        return new CustomItemReader(
                Arrays.asList(
                        new Customer("user1"),
                        new Customer("user2"),
                        new Customer("user3")
                )
        );
    }

    @Bean
    public ItemProcessor<? super Customer, ? extends Customer> customerProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public Step itemReaderItemProcessorItemWriterBatchJobStep2() {
        return stepBuilderFactory.get("itemReaderItemProcessorItemWriterBatchJobStep2")
                .tasklet(new Tasklet() {
                             @Override
                             public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                                 return RepeatStatus.FINISHED;
                             }
                         }
                )
                .build();
    }
}
