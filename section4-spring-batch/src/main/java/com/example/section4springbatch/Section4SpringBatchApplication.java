package com.example.section4springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Section4SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Section4SpringBatchApplication.class, args);
    }

}
