package com.lxs.bigdata.es.aspect;

import com.lxs.bigdata.es.anotation.RequestLimit;
import com.lxs.bigdata.es.utils.HttpRequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 请求限制切面
 */
@Aspect
@Component
public class RequestLimitAspect {
    public static final Logger logger = LoggerFactory.getLogger(RequestLimitAspect.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

//    @Pointcut("execution(public * com.lxs.bigdata.es.api.*.*(..)) && @annotation()")
//    public void requestLimit(){
//
//    }

    @Before("execution(public * com.lxs.bigdata.es.api.*.*(..)) && @annotation(limit)")
    public void requestLimit(JoinPoint joinPoint, RequestLimit limit) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String ipAddr = HttpRequestUtil.getIpAddr(request);
        String requestURL = request.getRequestURL().toString();
        String key = "limit_key_".concat(requestURL).concat(ipAddr);
        long num = redisTemplate.opsForValue().increment(key, 1);
        if (num == 1) {
            redisTemplate.expire(key, limit.time(), TimeUnit.MILLISECONDS);
        }
        if (num > limit.count()) {
            String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            logger.info("用户IP[" + ipAddr + "]访问地址[" + requestURL + "]超过了限定的次数[" + limit.count() + "]");
            throw new RuntimeException("超出访问次数限制");
        }
    }
}
