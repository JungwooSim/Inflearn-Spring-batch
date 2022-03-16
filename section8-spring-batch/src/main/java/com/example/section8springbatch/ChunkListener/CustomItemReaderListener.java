package com.example.section8springbatch.ChunkListener;


import org.springframework.batch.core.ItemReadListener;

public class CustomItemReaderListener implements ItemReadListener<Integer> {
    @Override
    public void beforeRead() {
        System.out.println(">> before Read");
    }

    @Override
    public void afterRead(Integer integer) {
        System.out.println(">> After Read");
    }

    @Override
    public void onReadError(Exception e) {
        System.out.println(">> after Read Error");
    }
}
