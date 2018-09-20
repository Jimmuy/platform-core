package com.qcec.dataservice.service;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * ExecutorUtil
 */
public final class ExecutorUtil {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    public static final Executor DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue());

    public static final Executor MAIN_THREAD_EXECUTOR = new MainThreadExecutor();

    static class MainThreadExecutor implements Executor {

        private final Handler handler = new Handler(Looper.getMainLooper());

        public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
