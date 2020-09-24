package com.lxs.queue.anotation;

import com.lxs.queue.spring.RedisQueueScannerRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 开启redis队列自动装载
 *
 * @author lxs
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RedisQueueScannerRegistrar.class)
public @interface RedisQueueScanner {
    
    /**
     * 扫描包的位置
     *
     * @return
     */
    @AliasFor("basePackages")
    String[] value() default {};
    
    /**
     * 扫描包的位置，与value的值叠加
     *
     * @return
     */
    @AliasFor("value")
    String[] basePackages() default {};
}
