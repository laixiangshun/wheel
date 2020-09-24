package com.lxs.queue.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * redis队列方法执行
 * @author lxs
 */
public interface IHandler extends InvocationHandler {
    
    /**
     * 方法执行
     *
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    Object run(Method method, Object[] args) throws Exception;
}
