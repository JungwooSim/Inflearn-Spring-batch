package com.example.section7springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Section7SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Section7SpringBatchApplication.class, args);
    }

}
