package com.example.section5springbatch.FlatFileItemWriter;

import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class JpaWriterBatchJobProcessor implements ItemProcessor<Customer, Customer2Entity> {
    ModelMapper modelMapper = new ModelMapper();

    @Override
    public Customer2Entity process(Customer customer) throws Exception {
        Customer2Entity customer2 = modelMapper.map(customer, Customer2Entity.class);

        return customer2;
    }
}
