package com.example.section5springbatch;

public class ItemReaderAdapterCustomService<T> {
    private int cnt = 0;

    public T customRead() {
        return (T) ("item" + cnt++);
    }
}
