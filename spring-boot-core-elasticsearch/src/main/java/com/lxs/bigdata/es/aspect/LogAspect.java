package com.lxs.bigdata.es.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 收集日志切面
 * 日志收集到Logstash
 */
@Aspect
@Component
public class LogAspect {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Pointcut(value = "execution(public * com.lxs.bigdata.es.api.*.*(..))")
    public void webLog() {

    }

    @Before("webLog()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        logger.info("-----------用户发起请求------------------");
        //记录请求内容
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        //如果是表单，参数值是普通键值对。如果是application/json，则request.getParameter是取不到的。
        logger.info("HTTP_HEAD Type : " + request.getHeader("Content-Type"));
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            //记录application/json时的传参，SpringMVC中使用@RequestBody接收的值
            logger.info(getRequestPayload(request));
        } else {
            //记录请求的键值对
            for (String key : request.getParameterMap().keySet()) {
                logger.info(key + "----" + request.getParameter(key));
            }
        }
    }

    @AfterReturning(returning = "obj", pointcut = "webLog()")
    public void doAfterReturning(JoinPoint joinPoint, Object obj) {
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info("{}方法返回的值：{}", method, obj);
        logger.info("--------------请求结束---------------");
    }

    /**
     * 后置异常
     */
    @AfterThrowing(throwing = "e", pointcut = "webLog()")
    public void throwsException(JoinPoint joinPoint, Exception e) {
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.error("请求{}方法执行出错，出错内容：{}", method, e.getMessage());
    }

    /**
     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
     */
    @After("webLog()")
    public void after() {

    }

    /**
     * 获取application/json 类型post请求传递的参数
     */
    private String getRequestPayload(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
