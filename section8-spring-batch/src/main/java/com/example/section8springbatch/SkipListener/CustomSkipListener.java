package com.example.section8springbatch.SkipListener;

import org.springframework.batch.core.SkipListener;

public class CustomSkipListener implements SkipListener<Integer, String> {
    @Override
    public void onSkipInRead(Throwable throwable) {
        System.out.println(">> onSkipRead : " + throwable.getMessage());
    }

    @Override
    public void onSkipInWrite(String item, Throwable throwable) {
        System.out.println(">> onSkipWrite : " + item);
        System.out.println(">> onSkipWrite : " + throwable.getMessage());
    }

    @Override
    public void onSkipInProcess(Integer item, Throwable throwable) {
        System.out.println(">> onSkipInProcess : " + item);
        System.out.println(">> onSkipInProcess : " + throwable.getMessage());
    }
}
