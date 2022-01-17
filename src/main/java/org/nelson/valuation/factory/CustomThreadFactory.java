package org.nelson.valuation.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

    private final String name;
    private final AtomicInteger integer = new AtomicInteger(1);

    public CustomThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name + integer.getAndIncrement());
    }

}
