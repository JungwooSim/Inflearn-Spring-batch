package io.springbatch.springbatchlecture.section3.execution;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet4 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("step4 was execution");

        System.out.println("name : " + chunkContext.getStepContext().getStepExecution().getExecutionContext().get("name"));

        return RepeatStatus.FINISHED;
    }
}
