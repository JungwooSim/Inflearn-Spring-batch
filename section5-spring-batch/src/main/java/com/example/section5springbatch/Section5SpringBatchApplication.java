package com.example.section5springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Section5SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Section5SpringBatchApplication.class, args);
    }

}
