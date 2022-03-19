package com.example.section10firstspringbatch.service;

import com.example.section10firstspringbatch.batch.domain.ApiInfo;
import com.example.section10firstspringbatch.batch.domain.ApiResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService1 extends AbstractApiService {
    @Override
    public ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8081/product/1", apiInfo, String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();

        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCodeValue).build();
        return apiResponseVO;
    }
}
