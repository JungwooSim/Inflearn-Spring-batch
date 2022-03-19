package com.example.section10firstspringbatch.batch.chunk.processor;

import com.example.section10firstspringbatch.batch.domain.ApiRequestVO;
import com.example.section10firstspringbatch.batch.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;

public class ApiItemProcessor3 implements ItemProcessor<ProductVO, ApiRequestVO> {
    @Override
    public ApiRequestVO process(ProductVO item) throws Exception {
        return ApiRequestVO.builder()
                .id(item.getId())
                .productVO(item)
                .build();
    }
}
