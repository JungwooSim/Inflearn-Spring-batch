package com.example.section6springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Section6SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Section6SpringBatchApplication.class, args);
    }

}
