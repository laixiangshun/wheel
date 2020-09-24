package com.lxs.queue.utils;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 核心线程
 *
 * @author lxs
 */
@Slf4j
public class ThreadPoolUtil {
    
    /**
     * 核心线程数，会一直存活，即使没有任务，线程池也会维护线程的最少数量
     */
    private static final int SIZE_CORE_POOL = 5;
    
    
    /**
     * 线程池维护线程的最大数量
     */
    private static final int SIZE_MAX_POOL = 10;
    
    
    /**
     * 线程池维护线程所允许的空闲时间
     */
    private static final long ALIVE_TIME = 2000;
    
    
    private static final ThreadFactory THREADFACTORY = ThreadUtil.createThreadFactoryBuilder()
            .setNamePrefix("sell-center")
            .setDaemon(true)
            .setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler())
            .build();
    
    
    /**
     * 线程缓冲队列
     */
    private static BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<>(100);
    
    
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(SIZE_CORE_POOL, SIZE_MAX_POOL, ALIVE_TIME,
            TimeUnit.MILLISECONDS, bqueue, THREADFACTORY, new ThreadPoolExecutor.CallerRunsPolicy());
    
    static {
        pool.prestartAllCoreThreads();
    }
    
    public static ThreadPoolExecutor getPool() {
        return pool;
    }
    
    /**
     * 线程池ThreadFactory设置自定义的UncaughtExceptionHanlder
     * 解决：异常最终只打印在System.err，而不会打印在项目的日志中
     */
    private static class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("线程ID:【{}】,线程名称：【{}】执行出现异常", t.getId(), t.getName(), e);
        }
    }
}
