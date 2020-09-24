package com.lxs.queue.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.lxs.queue.anotation.RedisQueue;
import com.lxs.queue.anotation.RedisQueueProvider;
import com.lxs.queue.utils.ThreadPoolUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.Pool;

import java.lang.reflect.Method;

/**
 * 自定义redis发送数据到队列执行方法
 *
 * @author lxs
 */
@Slf4j
@Setter
public class RedisHandler implements IHandler {
    
    private Pool<Jedis> pool;
    
    private JedisCluster jedisCluster;
    
    @Override
    public Object run(Method method, Object[] args) throws Exception {
        long rId = 0;
        if (!method.getDeclaringClass().isAnnotationPresent(RedisQueue.class) || !method.isAnnotationPresent(RedisQueueProvider.class)) {
            return rId;
        }
        if (args == null || args.length != 1) {
            throw new Exception("Redis Queue Provider Java Interface Defined Error.");
        }
        RedisQueueProvider redisQueueProvider = method.getDeclaredAnnotation(RedisQueueProvider.class);
        String queueName = StringUtils.isNotBlank(redisQueueProvider.value()) ? redisQueueProvider.value() : redisQueueProvider.queueName();
        if (StringUtils.isBlank(queueName)) {
            throw new Exception("Redis Queue Provider Java Config Error,please Check your configuration");
        }
        if (ObjectUtil.isNotNull(pool)) {
            try (Jedis jedis = pool.getResource()) {
                switch (redisQueueProvider.strategy()) {
                    case SYNC:
                        try {
                            rId = jedis.rpush(queueName, JSON.toJSONString(args[0]));
                            log.info("Sync Send Message [{}] To Queue [{}].", JSON.toJSONString(args[0]), queueName);
                        } catch (Exception e) {
                            log.error("Sync Send Message [" + JSON.toJSONString(args[0]) + "] To Queue [" + queueName + "] Error.", e);
                        }
                        return rId;
                    case ASYNC:
                        ThreadPoolUtil.getPool().submit(() -> {
                            try {
                                jedis.rpush(queueName, JSON.toJSONString(args[0]));
                                log.info("Async Send Message [{}] To Queue [{}].", JSON.toJSONString(args[0]), queueName);
                            } catch (Exception e) {
                                log.error("ASync Send Message [" + JSON.toJSONString(args[0]) + "] To Queue [" + queueName + "] Error.", e);
                            }
                        });
                        return 1;
                }
            }
        } else if (ObjectUtil.isNotNull(jedisCluster)) {
            switch (redisQueueProvider.strategy()) {
                case SYNC:
                    try {
                        rId = jedisCluster.rpush(queueName, JSON.toJSONString(args[0]));
                        log.info("Sync Send Message [{}] To Queue [{}].", JSON.toJSONString(args[0]), queueName);
                    } catch (Exception e) {
                        log.error("Sync Send Message [" + JSON.toJSONString(args[0]) + "] To Queue [" + queueName + "] Error.", e);
                    }
                    return rId;
                case ASYNC:
                    ThreadPoolUtil.getPool().submit(() -> {
                        try {
                            jedisCluster.rpush(queueName, JSON.toJSONString(args[0]));
                            log.info("Async Send Message [{}] To Queue [{}].", JSON.toJSONString(args[0]), queueName);
                        } catch (Exception e) {
                            log.error("ASync Send Message [" + JSON.toJSONString(args[0]) + "] To Queue [" + queueName + "] Error.", e);
                        }
                    });
                    return 1;
            }
        } else {
            log.error(" Send Message not support redis client！");
            throw new Exception(" Send Message not support redis client！");
        }
        return rId;
    }
    
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            return run(method, args);
        }
    }
}
