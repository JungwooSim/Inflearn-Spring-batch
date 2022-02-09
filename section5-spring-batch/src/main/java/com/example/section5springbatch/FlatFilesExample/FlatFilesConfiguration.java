package com.example.section5springbatch.FlatFilesExample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

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
                .<String, String>chunk(1)
//                .reader(itemReader())
//                .reader(itemReader2())
//                .reader(itemReader3())
                .reader(itemReader4())
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


    /**
     * DelimitedLineTokenizer
     */
    @Bean
    public ItemReader itemReader2() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new ClassPathResource("/customer.csv"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("name", "age", "year")
                .build();
    }

    /**
     * FixedLengthTokenizer
     */
    @Bean
    public ItemReader itemReader3() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new FileSystemResource("/Users/bigpenguin/project/Inflearn-Spring-batch/section5-spring-batch/src/main/resources/customer.txt"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength()
                .addColumns(new Range(1, 5))
                .addColumns(new Range(6, 9))
                .addColumns(new Range(10, 11))
                .names("name", "year", "age")
                .build();
    }

    /**
     * FixedLengthTokenizer, ExceptionHandler(.strict(false))
     */
    @Bean
    public ItemReader itemReader4() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new FileSystemResource("/Users/bigpenguin/project/Inflearn-Spring-batch/section5-spring-batch/src/main/resources/customer-error-data.txt"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength()
                .strict(false)
                .addColumns(new Range(1, 5))
                .addColumns(new Range(6, 9))
                .addColumns(new Range(10, 11))
                .names("name", "year", "age")
                .build();
    }
}
