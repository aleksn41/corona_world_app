package de.dhbw.corona_world_app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolHandler {
    static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors()*2 + 1;

    private static ExecutorService sInstance;

    public static ExecutorService getsInstance() {
        if (sInstance == null) {
            synchronized (ExecutorService.class) {
                sInstance = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
            }
        }
        return sInstance;
    }
}