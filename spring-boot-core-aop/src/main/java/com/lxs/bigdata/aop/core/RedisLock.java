package com.lxs.bigdata.aop.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RedisLock {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 获取锁
     */
    public boolean tryLock(String key, String value, long lockTime) {
        try {
            RedisCallback<String> callback = (redisConnection) -> {
                JedisCommands commands = (JedisCommands) redisConnection.getNativeConnection();
                return commands.set(key, value, "NX", "PX", lockTime);
            };
            String execute = redisTemplate.execute(callback);
            return StringUtils.isNotBlank(execute);
        } catch (Exception e) {
            log.error("set redis occured an exception", e);
        }
        return false;
    }

    public String get(String key) {
        try {
            RedisCallback<String> callback = (redisConnection) -> {
                JedisCommands commands = (JedisCommands) redisConnection.getNativeConnection();
                return commands.get(key);
            };
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("get redis occured an exception", e);
        }
        return "";
    }

    /**
     * 释放锁
     * 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
     * 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
     * spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
     * 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
     */
    public boolean releaseLock(String key, String value) {
        try {
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> args = new ArrayList<>();
            args.add(value);

            RedisCallback<Long> callback = (redisConnection) -> {
                Object nativeConnection = redisConnection.getNativeConnection();
                //集群
                if (nativeConnection instanceof JedisCluster) {
                    Long eval = (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                    return eval;
                    //单机
                } else if (nativeConnection instanceof Jedis) {
                    Long eval = (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                    return eval;
                } else {
                    log.error("redis 不支持类型");
                    return 0L;
                }
            };
            Long execute = redisTemplate.execute(callback);
            return execute != null && execute > 0;
        } catch (Exception e) {
            log.error("release lock occured an exception", e);
        }
        return false;
    }
}
