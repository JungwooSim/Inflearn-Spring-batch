package io.springbatch.springbatchlecture.section3.executioncontext;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet3 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("step3 was execution");

        Object name = chunkContext.getStepContext().getStepExecution().getExecutionContext().get("name");

        if (name == null) {
            chunkContext.getStepContext().getStepExecution().getExecutionContext().put("name", "user1");
            throw new RuntimeException("step3 was faild");
        }

        return RepeatStatus.FINISHED;
    }
}
