package com.me.event.pipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElasticScheduler implements Scheduler{

    private static ExecutorService cachedThreadPool;

    private static ElasticScheduler elasticScheduler;

    private ElasticScheduler() {
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public static ElasticScheduler getInstance(){
        if(elasticScheduler==null){
            synchronized (ElasticScheduler.class){
                if(elasticScheduler==null){
                    elasticScheduler = new ElasticScheduler();
                }
            }
        }
        return elasticScheduler;
    }

    @Override
    public void execute(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }
}
