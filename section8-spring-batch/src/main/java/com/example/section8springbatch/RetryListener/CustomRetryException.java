package com.example.section8springbatch.RetryListener;

public class CustomRetryException extends Exception {
    public CustomRetryException(String msg) {
        super(msg);
    }
}
