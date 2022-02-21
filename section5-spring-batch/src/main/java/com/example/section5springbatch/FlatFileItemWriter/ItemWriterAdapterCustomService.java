package com.example.section5springbatch.FlatFileItemWriter;

public class ItemWriterAdapterCustomService<T> {
    public void customWrite(T item) {
        System.out.println(item);
    }
}
