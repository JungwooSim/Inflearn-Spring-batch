package com.example.section8springbatch.SkipListener;

public class CustomSkipException extends Exception {
    public CustomSkipException() {
        super();
    }

    public CustomSkipException(String message) {
        super(message);
    }
}
