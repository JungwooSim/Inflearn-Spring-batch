package com.example.section10firstspringbatch.batch.chunk.writer;

import com.example.section10firstspringbatch.batch.domain.ApiRequestVO;
import com.example.section10firstspringbatch.batch.domain.ApiResponseVO;
import com.example.section10firstspringbatch.service.AbstractApiService;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ApiItemWriter1 implements ItemWriter<ApiRequestVO> {

    private final AbstractApiService apiService;

    public ApiItemWriter1(AbstractApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void write(List<? extends ApiRequestVO> items) throws Exception {
        ApiResponseVO responseVO = apiService.service(items);
        System.out.println("responseVO = " + responseVO);
    }
}
