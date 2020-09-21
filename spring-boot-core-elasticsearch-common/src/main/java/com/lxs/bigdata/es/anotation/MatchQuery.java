package com.lxs.bigdata.es.anotation;

import com.lxs.bigdata.es.core.Container;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD,ElementType.ANNOTATION_TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MatchQuery {
    Container container();

    String column();
}
