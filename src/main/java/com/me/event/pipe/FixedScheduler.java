package com.me.event.pipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedScheduler implements Scheduler{

    private static ExecutorService cachedThreadPool;

    public FixedScheduler(int nThreads) {
        cachedThreadPool = Executors.newFixedThreadPool(1);
    }

    @Override
    public void execute(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }
}
