package com.example.section7springbatch.multithreadstep;

import com.example.section7springbatch.ayncitem.Customer;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class CustomItemWriterListener implements ItemWriteListener<Customer> {
    @Override
    public void beforeWrite(List<? extends Customer> list) {

    }

    @Override
    public void afterWrite(List<? extends Customer> items) {
        System.out.println("Thread : " + Thread.currentThread().getName() + "write item : " + items.size());
    }

    @Override
    public void onWriteError(Exception e, List<? extends Customer> list) {

    }
}
