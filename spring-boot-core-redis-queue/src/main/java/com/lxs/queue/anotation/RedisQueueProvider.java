package com.lxs.queue.anotation;

import com.lxs.queue.enums.SendStrategyEn;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 自定义注解：redis队列发送
 *
 * @author lxs
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface RedisQueueProvider {
    
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
    
    /**
     * 发送策略，默认同步发送
     *
     * @return
     */
    SendStrategyEn strategy() default SendStrategyEn.SYNC;
}
