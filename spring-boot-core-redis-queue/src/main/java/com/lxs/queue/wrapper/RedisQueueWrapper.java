package com.lxs.queue.wrapper;

import com.lxs.queue.handler.HandlerFactory;

import java.lang.reflect.Proxy;

/**
 * redis队列wrapper
 *
 * @author lxs
 */
public class RedisQueueWrapper implements QueueWrapper {
    
    @Override
    public <T> T getInstance(Class<T> tClass) throws Exception {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, HandlerFactory.INSTANCE.instance(tClass));
    }
}
