package com.example.section8springbatch.ChunkListener;

import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class CustomItemWriterListener implements ItemWriteListener<String> {
    @Override
    public void beforeWrite(List<? extends String> list) {
        System.out.println(">> Before Writer");
    }

    @Override
    public void afterWrite(List<? extends String> list) {
        System.out.println(">> After Writer");
    }

    @Override
    public void onWriteError(Exception e, List<? extends String> list) {
        System.out.println(">> onWriteError");
    }
}
