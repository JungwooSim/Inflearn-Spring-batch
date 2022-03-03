package com.example.section5springbatch;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor5 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo processorInfo) throws Exception {
        System.out.println("CustomItemProcessor5");
        return processorInfo;
    }
}
