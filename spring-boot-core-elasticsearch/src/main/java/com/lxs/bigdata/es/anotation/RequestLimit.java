package com.lxs.bigdata.es.anotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * 自定义请求限制注解
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface RequestLimit {

    /**
     * 运行访问的次数
     */
    int count() default 5;

    /**
     * 时间范围
     */
    long time() default 6000;
}
