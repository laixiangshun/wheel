package com.lxs.bigdata.aop.core;

import com.lxs.bigdata.aop.aop.NoRepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
@Aspect
@Slf4j
public class RepeatSubmitAspect {

    @Autowired
    private RedisLock redisLock;

    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoRepeatSubmit noRepeatSubmit) {
    }

    @Around(value = "pointCut(noRepeatSubmit)", argNames = "pjp,noRepeatSubmit")
    public Object around(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) {
        int lockTime = noRepeatSubmit.lockTime();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Assert.notNull(request, "request can not be null");
        String token = request.getHeader("token");
        String path = request.getServletPath();
        String clientId = UUID.randomUUID().toString();
        String key = token + path;
        boolean lock = redisLock.tryLock(key, clientId, lockTime);
        if (lock) {
            log.debug("tryLock success,key:{},clientId:{}", key, clientId);
            Object proceed = null;
            try {
                proceed = pjp.proceed();
            } catch (Throwable throwable) {
                boolean releaseLock = redisLock.releaseLock(key, clientId);
                if(releaseLock) {
                    log.info("releaseLock success,key:{},cilentId:{}", key, clientId);
                }
            }
            return proceed;
        } else {
            log.error("tryLock fail,key:[{}]", key);
            return new ResponseEntity<>("请求重复，请稍后再试。", HttpStatus.OK);
        }
    }
}
