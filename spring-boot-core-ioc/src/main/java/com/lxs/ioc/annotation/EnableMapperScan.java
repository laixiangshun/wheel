package com.lxs.ioc.annotation;

import com.lxs.ioc.config.XBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 自定义扫描注解
 *
 * @author lxs
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value = {XBeanDefinitionRegistrar.class})
public @interface EnableMapperScan {
    
    @AliasFor(attribute = "value")
    String basePackage() default "";
    
    @AliasFor(attribute = "basePackage")
    String value() default "";
}
