package com.lxs.queue.consume;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lxs.queue.anotation.RedisQueueConsumer;
import com.lxs.queue.utils.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 自定义redis 队列消费抽象类
 *
 * @author lxs
 */
@Slf4j
public abstract class AbstractRedisQueueConsumer<T> implements IQueueConsumer {
    
    @Autowired
    private Pool<Jedis> jedisSentinelPool;
    
    private String queueName;
    
    /**
     * 自定义抽象方法
     * 继承AbstractRedisQueueConsumer并通过RedisQueueConsumer注解标明队列名称即可
     *
     * @param message
     */
    public abstract void doConsume(T message);
    
    @PostConstruct
    public void init() {
        log.info("The Consumer in Queue [{}] Started.", this.getClass().getName());
        ThreadPoolUtil.getPool().submit(() -> {
            try {
                this.consume();
            } catch (Exception e) {
                log.error("The Consumer in Queue [" + this.queueName + "] Error.", e);
            }
        });
    }
    
    @Override
    public void consume() throws Exception {
        if (this.getClass().isAnnotationPresent(RedisQueueConsumer.class)) {
            RedisQueueConsumer redisQueueConsumer = this.getClass().getAnnotation(RedisQueueConsumer.class);
            queueName = StringUtils.isNotBlank(redisQueueConsumer.value()) ? redisQueueConsumer.value() : redisQueueConsumer.queueName();
            if (StringUtils.isBlank(queueName)) {
                throw new Exception("Redis Queue Consumer Java Config Error.");
            }
            try (Jedis jedis = jedisSentinelPool.getResource()) {
                while (true) {
                    List<String> messages = jedis.blpop(0, queueName);
                    if (!CollectionUtils.isEmpty(messages)) {
                        messages.forEach(message -> {
                            this.doConsume(JSON.parseObject(message, new TypeReference<T>() {
                            }));
                        });
                    }
                }
            }
        } else {
            throw new Exception("Redis Queue Consumer Java Config Error.");
        }
    }
}
