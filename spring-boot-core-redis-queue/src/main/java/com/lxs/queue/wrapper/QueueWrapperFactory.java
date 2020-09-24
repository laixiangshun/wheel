package com.lxs.queue.wrapper;

import com.lxs.queue.anotation.RedisQueue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
@Getter
public enum QueueWrapperFactory {
    INSTANCE;
    
    private Map<Class<? extends Annotation>, QueueWrapper> cachedWrappers = new HashMap<>();
    
    QueueWrapperFactory() {
        cachedWrappers.put(RedisQueue.class, new RedisQueueWrapper());
    }
    
    public QueueWrapper instance(String clzName) throws Exception {
        Class clz = Class.forName(clzName);
        return instance(clz);
    }
    
    public QueueWrapper instance(Class<?> clz) throws Exception {
        if (clz.isAnnotationPresent(RedisQueue.class)) {
            return cachedWrappers.get(RedisQueue.class);
        } else {
            log.error("No Queue Annotation on Class [{}], please check your configuration.", clz.getName());
            throw new NullPointerException("No Queue Annotation on Class [" + clz.getName() + "], please check your configuration.");
        }
    }
}
