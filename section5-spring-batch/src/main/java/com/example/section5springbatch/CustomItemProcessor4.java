package com.example.section5springbatch;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor4 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo processorInfo) throws Exception {
        System.out.println("CustomItemProcessor4");
        return processorInfo;
    }
}
