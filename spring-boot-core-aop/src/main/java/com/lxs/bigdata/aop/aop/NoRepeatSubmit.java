package com.lxs.bigdata.aop.aop;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * 防止重复提交的注解
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface NoRepeatSubmit {

    /**
     * 获取锁等待时间
     */
    int lockTime() default 1;
}
