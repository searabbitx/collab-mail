package io.searabbitx.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPool {
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    public static void submit(Runnable task) {
        executor.submit(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
