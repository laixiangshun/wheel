package com.lxs.queue.handler;

import com.lxs.queue.anotation.RedisQueue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义handler 枚举
 *
 * @author lxs
 */
@Getter
@Slf4j
public enum HandlerFactory {
    INSTANCE;
    
    public Map<Class<? extends Annotation>, IHandler> handlerMap = new ConcurrentHashMap<>();
    
    HandlerFactory() {
        handlerMap.put(RedisQueue.class, new RedisHandler());
    }
    
    public IHandler instance(String className) throws ClassNotFoundException {
        Class<?> aClass = Class.forName(className);
        return instance(aClass);
    }
    
    public IHandler instance(Class classs) {
        if (classs.isAnnotationPresent(RedisQueue.class)) {
            return handlerMap.get(classs);
        } else {
            log.error("No Queue Annotation on Class [{}], please check your configuration.", classs.getName());
            throw new NullPointerException("No Queue Annotation on Class [" + classs.getName() + "], please check your configuration.");
        }
    }
    
    /**
     * 动态修改添加的handler参数
     *
     * @param key
     * @param property
     * @param value
     */
    public void modifyHandlerProperty(Class<? extends Annotation> key, String property, Object value) throws NoSuchFieldException {
        IHandler iHandler = handlerMap.get(key);
        Field field = iHandler.getClass().getDeclaredField(property);
        ReflectionUtils.setField(field, iHandler, value);
        handlerMap.put(key, iHandler);
    }
}
