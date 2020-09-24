package com.lxs.queue.anotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 自定义注解：消费redis队列中数据
 *
 * @author lxs
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface RedisQueueConsumer {
    
    /**
     * 队列名称
     *
     * @return
     */
    @AliasFor("queueName")
    String value() default "";
    
    /**
     * 队列名称
     *
     * @return
     */
    @AliasFor("value")
    String queueName() default "";
}
