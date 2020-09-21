package com.lxs.bigdata.es.anotation;

import com.lxs.bigdata.es.core.Operator;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD,ElementType.ANNOTATION_TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RangeQuery {

    Operator operator();

    String column();
}
