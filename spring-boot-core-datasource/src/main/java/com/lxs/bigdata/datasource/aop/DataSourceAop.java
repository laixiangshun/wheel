package com.lxs.bigdata.datasource.aop;

import com.lxs.bigdata.datasource.core.DBContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAop {

    /**
     * 查询
     */
    @Pointcut("!@annotation(com.lxs.bigdata.datasource.anotation.Master) " +
            "&& (execution(* com.lxs.bigdata.datasource.service..*.select*(..)) " +
            "|| execution(* com.lxs.bigdata.datasource.service..*.get*(..)))")
    public void readPointcut() {
    }

    /**
     * 增删改
     */
    @Pointcut("@annotation(com.lxs.bigdata.datasource.anotation.Master) " +
            "&& (execution(* com.lxs.bigdata.datasource.service..*.add*(..)) " +
            "|| execution(* com.lxs.bigdata.datasource.service..*.insert*(..)) " +
            "|| execution(* com.lxs.bigdata.datasource.service..*.update*(..)) " +
            "|| execution(* com.lxs.bigdata.datasource.service..*.edit*(..)) " +
            "|| execution(* com.lxs.bigdata.datasource.service..*.delete*(..)) " +
            "|| execution(* com.lxs.bigdata.datasource.service..*.remove*(..)))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        DBContextHolder.slave();
    }

    @Before("writePointcut()")
    public void write() {
        DBContextHolder.master();
    }
}
