package com.lxs.queue.wrapper;

public interface QueueWrapper {
    
    <T> T getInstance(Class<T> tClass) throws Exception;
}
