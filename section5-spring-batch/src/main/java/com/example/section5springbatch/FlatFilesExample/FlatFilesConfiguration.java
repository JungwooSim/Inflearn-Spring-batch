package com.example.section5springbatch.FlatFilesExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class FlatFilesConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesBatchJob() {
        return jobBuilderFactory.get("flatFilesBatchJob")
                .start(flatFilesBatchJobStep1())
                .next(flatFilesBatchJobStep2())
                .build();
    }

    @Bean
    public Step flatFilesBatchJobStep1() {
        return stepBuilderFactory.get("flatFilesBatchJobStep1")
                .<String, String>chunk(5)
                .reader(itemReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List list) throws Exception {
                        System.out.println("items = " + list);
                    }
                })
                .build();
    }

    @Bean
    public Step flatFilesBatchJobStep2() {
        return stepBuilderFactory.get("flatFilesBatchJobStep2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("step2 has executed");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public ItemReader itemReader() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenize(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());

        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("/customer.csv"));
        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);

        return itemReader;
    }
}
