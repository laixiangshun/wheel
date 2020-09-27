package com.lxs.ioc.annotation;

import java.lang.annotation.*;

/**
 * 自定义标识bean注解
 *
 * @author lxs
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XBean {


}
