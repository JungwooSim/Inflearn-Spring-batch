package com.example.section6springbatch.retry;

public class RetryAbleException extends RuntimeException {
    public RetryAbleException() {
        super();
    }

    public RetryAbleException(String message) {
        super(message);
    }
}
