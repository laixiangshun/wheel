package com.lxs.queue.anotation;

import java.lang.annotation.*;

/**
 * 自定义redis消息队列注解
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface RedisQueue {
    
}
