package com.example.section4springbatch.incrementerExample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CustomJobParametersIncrementer implements JobParametersIncrementer {
    static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-hhmmss");

    @Override
    public JobParameters getNext(JobParameters jobParameters) {
        String id = format.format(new Date());

        log.info("CustomJobParametersIncrementer : {}", id);
        return new JobParametersBuilder().addString("run.id", id).toJobParameters();
    }
}
