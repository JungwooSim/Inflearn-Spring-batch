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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ChunkBasicConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkBasicJob() {
        return jobBuilderFactory.get("chunkBasicJob")
                .start(chunkBasicJobStep1())
                .next(chunkBasicJobStep2())
                .build();
    }

    @Bean
    public Step chunkBasicJobStep1() {
        return stepBuilderFactory.get("chunkBasicJobStep1")
                .<String, String>chunk(5)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String s) throws Exception {
                        Thread.sleep(300);
                        System.out.println("item = " + s);
                        return "my" + s;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> list) throws Exception {
                        Thread.sleep(300);
                        System.out.println("items = " + list);
                    }
                })
                .build();
    }

    @Bean
    public Step chunkBasicJobStep2() {
        return stepBuilderFactory.get("chunkBasicJobStep2")
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
