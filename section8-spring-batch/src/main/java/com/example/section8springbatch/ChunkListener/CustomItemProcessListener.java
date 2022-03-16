package com.example.section8springbatch.ChunkListener;


import org.springframework.batch.core.ItemProcessListener;

public class CustomItemProcessListener implements ItemProcessListener<Integer, String> {
    @Override
    public void beforeProcess(Integer item) {
        System.out.println(">> before Process");
    }

    @Override
    public void afterProcess(Integer item, String result) {
        System.out.println(">> After Process");
    }

    @Override
    public void onProcessError(Integer item, Exception e) {
        System.out.println(">> onProcessError");
    }
}
