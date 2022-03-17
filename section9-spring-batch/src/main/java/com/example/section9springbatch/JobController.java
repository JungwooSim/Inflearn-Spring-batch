package com.example.section9springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Set;

@RequiredArgsConstructor
@RestController
public class JobController {
    private final JobRegistry jobRegistry;

    private final JobExplorer jobExplorer;

    private final JobOperator jobOperator;

    @PostMapping(value = "/batch/start")
    public String start(@RequestBody JobInfo jobInfo) throws NoSuchJobException, JobInstanceAlreadyExistsException, JobParametersInvalidException {
        for (Iterator<String> iterator = jobRegistry.getJobNames().iterator(); iterator.hasNext();) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(iterator.next());
            System.out.println("jobName : " + job.getName());

            String param = "id=" + jobInfo.getId();
            jobOperator.start(job.getName(), param);
        }

        return "batch is started";
    }

    @PostMapping(value = "/batch/stop")
    public String step() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException {
        for (Iterator<String> iterator = jobRegistry.getJobNames().iterator(); iterator.hasNext();) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(iterator.next());
            System.out.println("jobName : " + job.getName());

            // 실행중인 Job 조회
            Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
            JobExecution jobExecution = runningJobExecutions.iterator().next();

            jobOperator.stop(jobExecution.getId());
        }

        return "batch is stop";
    }

    @PostMapping(value = "/batch/restart")
    public String restart() throws NoSuchJobException, NoSuchJobExecutionException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException {
        for (Iterator<String> iterator = jobRegistry.getJobNames().iterator(); iterator.hasNext();) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(iterator.next());
            System.out.println("jobName : " + job.getName());

            // 마지막으로 실행한 Job 조회
            JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
            JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);

            jobOperator.restart(lastJobExecution.getJobId());
        }

        return "batch is restart";
    }
}
