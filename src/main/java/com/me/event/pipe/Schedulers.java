package com.me.event.pipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Schedulers {

    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static void elastic(Runnable runnable){
        cachedThreadPool.execute(runnable);
    }
}
