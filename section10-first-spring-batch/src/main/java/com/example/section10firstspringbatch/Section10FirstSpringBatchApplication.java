package com.example.section10firstspringbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Section10FirstSpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Section10FirstSpringBatchApplication.class, args);
    }

}
