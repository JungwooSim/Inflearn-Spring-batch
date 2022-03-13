package com.example.section7springbatch.multithreadstep;

import com.example.section7springbatch.ayncitem.Customer;
import org.springframework.batch.core.ItemProcessListener;

public class CustomerItemProcessListener implements ItemProcessListener<Customer, Customer> {
    @Override
    public void beforeProcess(Customer customer) {

    }

    @Override
    public void afterProcess(Customer item, Customer result) {
        System.out.println("Thread : " + Thread.currentThread().getName() + "process item : " + item.getId());
    }

    @Override
    public void onProcessError(Customer customer, Exception e) {

    }
}
