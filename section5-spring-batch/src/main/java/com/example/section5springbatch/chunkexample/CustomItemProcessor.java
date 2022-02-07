package com.example.section5springbatch.chunkexample;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        customer.setName(customer.getName().toUpperCase());
        return customer;
    }
}
