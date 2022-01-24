package io.springbatch.springbatchlecture.section3;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JobRunner implements ApplicationRunner {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*
        // 스프링 배치 도메인 이해 / JobInstance 강의 코드
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user2") // BATCH_JOB_EXECUTION_PARAMS 에 저장됨
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
         */

        /*
        java -jar springbatchlecture-0.0.1-SNAPSHOT.jar name=user1 seq(long)=3L date(date)=2021/01/01 age(double)=16.5 명령어로 실행 가능
         */

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user1")
                .addLong("seq", 2L)
                .addDate("date", new Date())
                .addDouble("age", 16.5)
                .toJobParameters();

        // jobLauncher.run(job, jobParameters); // 사용하지 않아서 주석
    }
}
